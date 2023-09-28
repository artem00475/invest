package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import project.invest.jpa.entities.Account;
import project.invest.jpa.entities.Amortization;
import project.invest.jpa.repositories.AmortizationRepository;


@Service
public class AmortizationService {

    @Autowired
    private final AmortizationRepository amortizationRepository;

    public AmortizationService(AmortizationRepository amortizationRepository) {
        this.amortizationRepository = amortizationRepository;
    }

    public Page<Amortization> getAmortizations(String instrumentName, String user, Pageable pageable) {return amortizationRepository.findAmortizationsByInstrumentNameAndUser_Username(instrumentName, user, pageable);}

    public Account addAmortization(Amortization amortization, Account account) {
        if (account != null) {
            account.setAverageCost(account.getAverageCost()-amortization.getCost());
            amortizationRepository.save(amortization);
        }
        return account;
        }
}
