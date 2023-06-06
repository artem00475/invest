package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.invest.jpa.entities.*;
import project.invest.jpa.repositories.AccountRepository;

import java.util.List;

@Service
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    @Autowired
    private final SummaryService summaryService;

    public AccountService(AccountRepository accountRepository, SummaryService summaryService) {
        this.accountRepository = accountRepository;
        this.summaryService = summaryService;
    }

    public void addAccount(AccountBuy accountBuy) {
        Account account = accountRepository.findByInstrumentNameAndTicker(accountBuy.getInstrumentName(), accountBuy.getTicker());
        if (account == null) {
            account = new Account();
            account.setAverageCost(accountBuy.getCost());
            account.setInstrumentName(accountBuy.getInstrumentName());
            account.setCount(accountBuy.getCount());
            account.setTicker(accountBuy.getTicker());
            accountRepository.save(account);
        } else {
            account.setAverageCost((account.getAverageCost()*account.getCount()+accountBuy.getSum())/(account.getCount()+accountBuy.getCount()));
            account.setCount(account.getCount()+accountBuy.getCount());
            accountRepository.save(account);
        }
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
        updateSummary(dividends.getInstrumentName());
    }
    @Transactional
    public void addAccount(AccountSell accountSell) {
        Account account = accountRepository.findByInstrumentNameAndTicker(accountSell.getInstrumentName(), accountSell.getTicker());
        if (account != null) {
            account.setCount(account.getCount()-accountSell.getCount());
            if (account.getCount() ==0) {
                accountRepository.removeById(account.getId());
            } else {
                account.setChange(account.getCount()*account.getCurrentCost()-account.getCount()*account.getAverageCost());
                accountRepository.save(account);
            }
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
            sum += account.getCount()*account.getCurrentCost();
            change += account.getChange();
        }
        summaryEntity.setSum(sum);
        summaryEntity.setChange(change);
        summaryService.addToSummery(summaryEntity);
    }
}
