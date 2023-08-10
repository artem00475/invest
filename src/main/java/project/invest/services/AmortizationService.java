package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.Amortization;
import project.invest.jpa.repositories.AmortizationRepository;

import java.util.List;

@Service
public class AmortizationService {

    @Autowired
    private final AmortizationRepository amortizationRepository;

    public AmortizationService(AmortizationRepository amortizationRepository) {
        this.amortizationRepository = amortizationRepository;
    }

    public List<Amortization> getAmortizations(String instrumentName) {return amortizationRepository.findAllByInstrumentName(instrumentName);}

    public void addAmortization(Amortization amortization) {amortizationRepository.save(amortization);}
}
