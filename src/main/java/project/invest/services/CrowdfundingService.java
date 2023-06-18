package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.jpa.repositories.SummaryRepository;

@Service
public class CrowdfundingService {

    @Autowired
    private final SummaryRepository summaryRepository;

    public CrowdfundingService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public SummaryEntity getSummary(String instrumentName) {return summaryRepository.findByInstrumentName(instrumentName);}

    public void update(SummaryEntity summaryEntity) {summaryRepository.save(summaryEntity);}
}
