package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.Commission;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.jpa.repositories.CommissionRepository;
import project.invest.jpa.repositories.SummaryRepository;


@Service
public class CommissionService {

    @Autowired
    private final CommissionRepository commissionRepository;

    @Autowired
    private final SummaryRepository summaryRepository;

    public CommissionService(CommissionRepository commissionRepository, SummaryRepository summaryRepository) {
        this.commissionRepository = commissionRepository;
        this.summaryRepository = summaryRepository;
    }

    public Page<Commission> getCommissions(String instrumentName, Pageable pageable) {return commissionRepository.findAllByInstrumentName(instrumentName, pageable);}

    public void addCommission(Commission commission) {
        commissionRepository.save(commission);
        SummaryEntity summaryEntity =  summaryRepository.findByInstrumentName(commission.getInstrumentName());
        summaryEntity.setResult(summaryEntity.getResult()-commission.getSum());
        summaryEntity.setBalance(summaryEntity.getBalance()-commission.getSum());
        summaryRepository.save(summaryEntity);
    }


}
