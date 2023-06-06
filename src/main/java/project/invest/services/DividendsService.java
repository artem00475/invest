package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.Dividends;
import project.invest.jpa.repositories.DividendsRepository;

import java.util.List;

@Service
public class DividendsService {

    @Autowired
    private final DividendsRepository dividendsRepository;

    public DividendsService(DividendsRepository dividendsRepository) {
        this.dividendsRepository = dividendsRepository;
    }

    public List<Dividends> getDividends(String instrumentName) {return dividendsRepository.findAllByInstrumentName(instrumentName);}

    public void addDividends(Dividends dividends) {dividendsRepository.save(dividends);}
}
