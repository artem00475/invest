package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.AccountSell;
import project.invest.jpa.repositories.SellsRepository;

import java.util.List;

@Service
public class SellsService {

    @Autowired
    private final SellsRepository sellsRepository;


    public SellsService(SellsRepository sellsRepository) {
        this.sellsRepository = sellsRepository;
    }

    public List<AccountSell> getSells(String instrumentName) {return sellsRepository.findAllByInstrumentName(instrumentName);}

    public void addSell(AccountSell accountSell) {sellsRepository.save(accountSell);}
}
