package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.Deposit;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.jpa.repositories.DepositRepository;
import project.invest.jpa.repositories.SummaryRepository;

import java.util.List;

@Service
public class DepositService {

    @Autowired
    private final SummaryRepository summaryRepository;

    @Autowired
    private final DepositRepository depositRepository;

    public DepositService(SummaryRepository summaryRepository, DepositRepository depositRepository) {
        this.summaryRepository = summaryRepository;
        this.depositRepository = depositRepository;
    }

    public List<Deposit>  getDeposits(String instrumentName) {return depositRepository.findAllByInstrumentName(instrumentName);}

    public void deposit(Deposit deposit) {
        depositRepository.save(deposit);
        SummaryEntity summaryEntity =  summaryRepository.findByInstrumentName(deposit.getInstrumentName());
        summaryEntity.setInvested(summaryEntity.getInvested()+deposit.getSum());
        summaryEntity.setResult(summaryEntity.getResult()+deposit.getSum());
        summaryEntity.setBalance(summaryEntity.getBalance()+deposit.getSum());
        summaryRepository.save(summaryEntity);
    }
}
