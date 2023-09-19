package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Dividends> getDividends(String instrumentName, Pageable pageable) {return dividendsRepository.findAllByInstrumentName(instrumentName, pageable);}

    public void addDividends(Dividends dividends) {dividendsRepository.save(dividends);}
}
