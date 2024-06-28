package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import project.invest.jpa.entities.*;
import project.invest.jpa.repositories.AccountRepository;
import project.invest.jpa.repositories.PaperRepository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Getter
@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final SummaryService summaryService;

    @Autowired
    private final PaperRepository paperRepository;

    @Autowired
    private final AccountBuyService accountBuyService;

    @Autowired
    private final DividendsService dividendsService;

    @Autowired
    private final SellsService sellsService;

    @Autowired
    private final DepositService depositService;

    @Autowired
    private final CommissionService commissionService;

    @Autowired
    private final AmortizationService amortizationService;

    public AccountService(AccountRepository accountRepository, SummaryService summaryService, PaperRepository paperRepository, AccountBuyService accountBuyService, DividendsService dividendsService, SellsService sellsService, DepositService depositService, CommissionService commissionService, AmortizationService amortizationService) {
        this.accountRepository = accountRepository;
        this.summaryService = summaryService;
        this.paperRepository = paperRepository;
        this.accountBuyService = accountBuyService;
        this.dividendsService = dividendsService;
        this.sellsService = sellsService;
        this.depositService = depositService;
        this.commissionService = commissionService;
        this.amortizationService = amortizationService;
    }

    //Buy
    public void addAccount(AccountBuy accountBuy, PaperTypeEnum type, String username) {
        Account account = accountRepository.findByInstrumentNameAndTickerAndUser_Username(accountBuy.getInstrumentName(), accountBuy.getTicker(), username);
        account = accountBuyService.addBuy(accountBuy, account);
        accountRepository.save(account);
            if (paperRepository.findByTicker(account.getTicker())==null) {
                Paper paper = new Paper();
                paper.setTicker(account.getTicker());
                paper.setType(type);
                System.out.println(paper.getTicker()+' '+paper.getType());
                paperRepository.save(paper);
            }
        summaryService.setBalance(accountBuy.getInstrumentName(),-accountBuy.getSum());
        updateSummary(accountBuy.getInstrumentName(), username);
    }

    //Dividends
    public void addAccount(Dividends dividends, String username) {
        Account account = accountRepository.findByInstrumentNameAndTickerAndUser_Username(dividends.getInstrumentName(), dividends.getTicker(), username);
        if (account == null) return;
        account = dividendsService.addDividends(dividends, account);
        summaryService.setBalance(dividends.getInstrumentName(), dividends.getSum());
        summaryService.setResult(dividends.getInstrumentName(), dividends.getSum());
        updateSummary(dividends.getInstrumentName(), username);
    }

    //Amortization
    public void addAccount(Amortization amortization, String username) {
        Account account = accountRepository.findByInstrumentNameAndTickerAndUser_Username(amortization.getInstrumentName(), amortization.getTicker(), username);
        account = amortizationService.addAmortization(amortization, account);
        summaryService.setBalance(amortization.getInstrumentName(), amortization.getSum());
        updateSummary(amortization.getInstrumentName(), username);
    }

    //Sell
    @Transactional
    public void addAccount(AccountSell accountSell, String username) {
        Account account = accountRepository.findByInstrumentNameAndTickerAndUser_Username(accountSell.getInstrumentName(), accountSell.getTicker(), username);
        if (account != null) {
            account.setCount(account.getCount()-accountSell.getCount());
            if (account.getCount() ==0) {
                accountRepository.removeById(account.getId());
                if (accountRepository.findAllByTicker(accountSell.getTicker()).isEmpty()) paperRepository.removeByTicker(account.getTicker());
            } else {
                account.setChange((account.getCount()*account.getCurrentCost()-account.getCount()*account.getAverageCost()));
                accountRepository.save(account);

            }
            sellsService.addSell(accountSell);
            summaryService.setBalance(accountSell.getInstrumentName(), accountSell.getSum());
            summaryService.setResult(accountSell.getInstrumentName(), accountSell.getChange());
            updateSummary(accountSell.getInstrumentName(), username);
        }
    }

    //Commission
    public void addAccount(Commission commission) {
        commissionService.addCommission(commission);
    }

    //Deposit
    public void addAccount(Deposit deposit) {
        depositService.deposit(deposit);
    }

    public List<Account> getAccounts(String instrumentName, String username) {return accountRepository.findAllByInstrumentNameAndUser_Username(instrumentName, username);}

    public Account getAccount(String name, String ticker, String username) {return accountRepository.findByInstrumentNameAndTickerAndUser_Username(name, ticker, username);}

    public void updateSummary(String instrumentName, String username) {
        SummaryEntity summaryEntity = summaryService.getSummary(instrumentName);
        List<Account> accounts = accountRepository.findAllByInstrumentNameAndUser_Username(instrumentName, username);
        float sum = 0;
        for (Account account : accounts) {
            account.setChange(account.getCount()*account.getCurrentCost()-account.getCount()*account.getAverageCost());
            account.setChangeInPercents(Float.parseFloat(new DecimalFormat("#.###").format(account.getChange()/(account.getAverageCost()*account.getCount())*100).replace(',','.')));
            sum += account.getCount()*account.getCurrentCost();
        }
        sum += summaryEntity.getBalance();
        for (Account account : accounts) {
            account.setCurrentShare(Float.parseFloat(new DecimalFormat("#.#").format((account.getCurrentCost()*account.getCount())/sum*100).replace(',','.')));
            account.setShareDifference(account.getMaxShare()-account.getCurrentShare());
            account.setToBuy(account.getShareDifference()*0.01f*sum);
            accountRepository.save(account);
        }
        summaryEntity.setSum(sum);
        summaryEntity.setChange(summaryEntity.getSum()-summaryEntity.getResult());
        summaryEntity.setChangeInPercents(Float.parseFloat(new DecimalFormat("#.###").format(summaryEntity.getChange()/summaryEntity.getResult()*100).replace(',','.')));
        summaryService.addToSummery(summaryEntity);
    }

    public List<List<String>> getTickers() {
        List<String> list = new ArrayList<>();
        List<Paper> papers = paperRepository.findAllByType(PaperTypeEnum.STOCK);
        papers.forEach(paper -> list.add(paper.getTicker()));
        List<String> list1 = new ArrayList<>();
        papers = paperRepository.findAllByType(PaperTypeEnum.BOND);
        papers.forEach(paper -> list1.add(paper.getTicker()));
        List<String> list2 = new ArrayList<>();
        papers = paperRepository.findAllByType(PaperTypeEnum.CURRENCY);
        papers.forEach(paper -> list2.add(paper.getTicker()));
        List<String> list3 = new ArrayList<>();
        papers = paperRepository.findAllByType(PaperTypeEnum.FUND);
        papers.forEach(paper -> list3.add(paper.getTicker()));
        List<List<String>> tickers = new ArrayList<>();
        tickers.add(list);
        tickers.add(list1);
        tickers.add(list2);
        tickers.add(list3);
        return tickers;
    }

    public void updateCost(List<String> costRequest, String username) {
        costRequest.forEach(el -> {
            String[] a = el.split("###");
            Paper paper = paperRepository.findByTicker(a[0]);
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
        List<SummaryEntity> summaryEntities = summaryService.getAllByUserNameAndType("Artem", InstrumentTypeEnum.brokerageAccount);
        summaryEntities.forEach(el -> {
            updateSummary(el.getInstrumentName(), username);
        });
        summaryService.updatePercentFromAll(username);
        summaryService.updateChangeFromInvested(username);
    }

    public float getBalance(String instrumentName) {
        return summaryService.getSummary(instrumentName).getBalance();
    }

    public String getBalanceShare(String instrumentName) {
        SummaryEntity summary = summaryService.getSummary(instrumentName);
        return new DecimalFormat( "#.##" ).format(summary.getBalance()/ summary.getSum() * 100).replace(',', '.');
    }

    public PaperTypeEnum getType(String ticker) {return paperRepository.findByTicker(ticker).getType();}

    public void changePercent(ArrayList<String> arrayList, String instrumentName, String username) {
        arrayList.forEach(el -> {
            String[] a = el.split("###");
            Account account = accountRepository.findByInstrumentNameAndTickerAndUser_Username(instrumentName, a[1], username);
            try {
                account.setMaxShare(Float.parseFloat(a[0]));
                accountRepository.save(account);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
