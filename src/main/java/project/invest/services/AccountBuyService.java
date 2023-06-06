package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.AccountBuy;
import project.invest.jpa.repositories.AccountBuyRepository;

import java.util.List;

@Service
public class AccountBuyService {

    @Autowired
    private final AccountBuyRepository accountBuyRepository;

    public AccountBuyService(AccountBuyRepository accountBuyRepository) {
        this.accountBuyRepository = accountBuyRepository;
    }

    public List<AccountBuy> getBuys(String instrumentName) {return accountBuyRepository.findAllByInstrumentName(instrumentName);}

    public void addBuy(AccountBuy accountBuy) {accountBuyRepository.save(accountBuy);}
}
