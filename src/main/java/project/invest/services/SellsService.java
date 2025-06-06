package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.AccountSell;
import project.invest.jpa.repositories.SellsRepository;


@Service
public class SellsService {

    @Autowired
    private final SellsRepository sellsRepository;


    public SellsService(SellsRepository sellsRepository) {
        this.sellsRepository = sellsRepository;
    }

    public Page<AccountSell> getSells(String instrumentName, String user, Pageable pageable) {return sellsRepository.findAccountSellsByInstrumentNameAndUser_Username(instrumentName, user, pageable);}

    public void addSell(AccountSell accountSell) {sellsRepository.save(accountSell);}
}
