package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Amortization> getAmortizations(String instrumentName, Pageable pageable) {return amortizationRepository.findAllByInstrumentName(instrumentName, pageable);}

    public void addAmortization(Amortization amortization) {amortizationRepository.save(amortization);}
}
