package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.jpa.repositories.SummaryRepository;

import java.util.List;

@Service
public class SummaryService {
    @Autowired
    private final SummaryRepository summaryRepository;


    public SummaryService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public void addToSummery(SummaryEntity summaryEntity) {summaryRepository.save(summaryEntity);}

    public List<SummaryEntity> getAllByUserName(String user) {return summaryRepository.findAllByUserName(user);}
}
