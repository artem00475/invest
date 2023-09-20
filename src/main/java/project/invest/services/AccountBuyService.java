package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import project.invest.jpa.entities.Account;
import project.invest.jpa.entities.AccountBuy;
import project.invest.jpa.repositories.AccountBuyRepository;


@Service
public class AccountBuyService {

    @Autowired
    private final AccountBuyRepository accountBuyRepository;

    public AccountBuyService(AccountBuyRepository accountBuyRepository) {
        this.accountBuyRepository = accountBuyRepository;
    }

    public Page<AccountBuy> getBuys(String instrumentName, Pageable pageable) {return accountBuyRepository.findAllByInstrumentName(instrumentName, pageable);}

    public Account addBuy(AccountBuy accountBuy, Account account) {
        accountBuyRepository.save(accountBuy);
        if (account == null) {
            account = new Account();
            account.setAverageCost(accountBuy.getCost());
            account.setInstrumentName(accountBuy.getInstrumentName());
            account.setCount(accountBuy.getCount());
            account.setTicker(accountBuy.getTicker());
        } else {
            account.setAverageCost((account.getAverageCost()*account.getCount()+accountBuy.getSum())/(account.getCount()+accountBuy.getCount()));
            account.setCount(account.getCount()+accountBuy.getCount());
        }
        return account;
    }
}
