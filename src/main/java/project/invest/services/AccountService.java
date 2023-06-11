package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.invest.jpa.entities.*;
import project.invest.jpa.repositories.AccountRepository;
import project.invest.jpa.repositories.PaperRepository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final SummaryService summaryService;

    @Autowired
    private final PaperRepository paperRepository;

    public AccountService(AccountRepository accountRepository, SummaryService summaryService, PaperRepository paperRepository) {
        this.accountRepository = accountRepository;
        this.summaryService = summaryService;
        this.paperRepository = paperRepository;
    }

    public void addAccount(AccountBuy accountBuy, PaperTypeEnum type) {
        Account account = accountRepository.findByInstrumentNameAndTicker(accountBuy.getInstrumentName(), accountBuy.getTicker());
        if (account == null) {
            account = new Account();
            account.setAverageCost(accountBuy.getCost());
            account.setInstrumentName(accountBuy.getInstrumentName());
            account.setCount(accountBuy.getCount());
            account.setTicker(accountBuy.getTicker());
            accountRepository.save(account);
            if (paperRepository.findByTicker(account.getTicker())==null) {
                Paper paper = new Paper();
                paper.setTicker(account.getTicker());
                paper.setType(type);
                paperRepository.save(paper);
            }
        } else {
            account.setAverageCost((account.getAverageCost()*account.getCount()+accountBuy.getSum())/(account.getCount()+accountBuy.getCount()));
            account.setCount(account.getCount()+accountBuy.getCount());
            accountRepository.save(account);
        }
        SummaryEntity summaryEntity = summaryService.getSummary(accountBuy.getInstrumentName());
        summaryEntity.setBalance(summaryEntity.getBalance()-accountBuy.getSum());
        summaryService.addToSummery(summaryEntity);
        updateSummary(accountBuy.getInstrumentName());
    }

    public void addAccount(Dividends dividends) {
        Account account = accountRepository.findByInstrumentNameAndTicker(dividends.getInstrumentName(), dividends.getTicker());
        if (account == null) {
            account = new Account();
            account.setInstrumentName(dividends.getInstrumentName());
            account.setTicker(dividends.getTicker());
            account.setDividends(dividends.getSum());
            accountRepository.save(account);
        } else {
            account.setDividends(account.getDividends()+dividends.getSum());
            accountRepository.save(account);
        }
        SummaryEntity summaryEntity = summaryService.getSummary(dividends.getInstrumentName());
        summaryEntity.setBalance(summaryEntity.getBalance()+dividends.getSum());
        summaryEntity.setResult(summaryEntity.getResult()+dividends.getSum());
        summaryService.addToSummery(summaryEntity);
        updateSummary(dividends.getInstrumentName());
    }
    @Transactional
    public void addAccount(AccountSell accountSell) {
        Account account = accountRepository.findByInstrumentNameAndTicker(accountSell.getInstrumentName(), accountSell.getTicker());
        if (account != null) {
            account.setCount(account.getCount()-accountSell.getCount());
            if (account.getCount() ==0) {
                accountRepository.removeById(account.getId());
                if (accountRepository.findAllByTicker(accountSell.getTicker()).isEmpty()) paperRepository.removeByTicker(account.getTicker());
            } else {
                account.setChange((account.getCount()*account.getCurrentCost()-account.getCount()*account.getAverageCost()));
                accountRepository.save(account);

            }
            SummaryEntity summaryEntity = summaryService.getSummary(accountSell.getInstrumentName());
            summaryEntity.setBalance(summaryEntity.getBalance()+accountSell.getSum());
            summaryEntity.setResult(summaryEntity.getResult()+accountSell.getChange());
            summaryService.addToSummery(summaryEntity);
            updateSummary(accountSell.getInstrumentName());
        }
    }

    public List<Account> getAccounts(String instrumentName) {return accountRepository.findAllByInstrumentName(instrumentName);}

    public Account getAccount(String name, String ticker) {return accountRepository.findByInstrumentNameAndTicker(name, ticker);}

    public void updateSummary(String instrumentName) {
        SummaryEntity summaryEntity = summaryService.getSummary(instrumentName);
        List<Account> accounts = accountRepository.findAllByInstrumentName(instrumentName);
        float sum = 0;
        float change = 0;
        for (Account account : accounts) {
            account.setChange(account.getCount()*account.getCurrentCost()-account.getCount()*account.getAverageCost());
            account.setChangeInPercents(Float.parseFloat(new DecimalFormat("#.###").format(account.getChange()/(account.getAverageCost()*account.getCount())).replace(',','.')));
            accountRepository.save(account);
            sum += account.getCount()*account.getCurrentCost();
            change += account.getChange();
        }
        sum += summaryEntity.getBalance();
        summaryEntity.setSum(sum);
        summaryEntity.setChange(change);
        summaryService.addToSummery(summaryEntity);
    }

    public List<List<String>> getTickers() {
        List<String> list = new ArrayList<>();
        List<Paper> papers = paperRepository.findAllByType(PaperTypeEnum.STOCK);
        papers.forEach(paper -> list.add(paper.getTicker()));
        List<String> list1 = new ArrayList<>();
        papers = paperRepository.findAllByType(PaperTypeEnum.BOND);
        papers.forEach(paper -> list1.add(paper.getTicker()));
        List<List<String>> tickers = new ArrayList<>();
        tickers.add(list);
        tickers.add(list1);
        return tickers;
    }

    public void updateCost(List<String> costRequest) {
        costRequest.forEach(el -> {
            String[] a = el.split("###");
            Paper paper = new Paper();
            paper.setTicker(a[0]);
            paper.setCost(Float.parseFloat(a[1]));
            paper.setName(a[2]);
            paperRepository.save(paper);
        });
        List<Account> accounts = accountRepository.findAll();
        accounts.forEach(el -> {
            Paper paper = paperRepository.findByTicker(el.getTicker());
            el.setCurrentCost(paper.getCost());
            el.setName(paper.getName());
        });
        accountRepository.saveAll(accounts);
        List<SummaryEntity> summaryEntities = summaryService.getAllByUserName("Artem");
        summaryEntities.forEach(el -> {
            updateSummary(el.getInstrumentName());
        });
    }

    public float getBalance(String instrumentName) {
        return summaryService.getSummary(instrumentName).getBalance();
    }
}
