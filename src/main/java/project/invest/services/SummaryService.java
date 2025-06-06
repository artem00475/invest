package project.invest.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.invest.jpa.entities.InstrumentTypeEnum;
import project.invest.jpa.entities.SummaryEntity;
import project.invest.jpa.repositories.SummaryRepository;

import java.text.DecimalFormat;
import java.util.List;

@Service
public class SummaryService {

    private final SummaryRepository summaryRepository;

    @Autowired
    public SummaryService(SummaryRepository summaryRepository) {
        this.summaryRepository = summaryRepository;
    }

    public void addToSummery(SummaryEntity summaryEntity) {summaryRepository.save(summaryEntity);}

    public void setBalance(String name, float change) {
        SummaryEntity summaryEntity = getSummary(name);
        summaryEntity.setBalance(summaryEntity.getBalance()+change);
        addToSummery(summaryEntity);
    }

    public void setResult(String name, float change) {
        SummaryEntity summaryEntity = getSummary(name);
        summaryEntity.setResult(summaryEntity.getResult()+change);
        addToSummery(summaryEntity);
    }

    public List<SummaryEntity> getAllByUserName(String user) {return summaryRepository.findSummaryEntitiesByUser_Username(user);}
    public List<SummaryEntity> getAllByUserNameAndType(String user, InstrumentTypeEnum instrumentTypeEnum) {return summaryRepository.findAllByUserUsernameAndInstrumentTypeEnum(user, instrumentTypeEnum);}

    public boolean checkInstrument(String instrumentName) {return summaryRepository.findByInstrumentName(instrumentName)==null;}

    public SummaryEntity getSummary(String instrumentName) {return summaryRepository.findByInstrumentName(instrumentName);}

    public float getTotal(String name) {
        float total = 0;
        List<SummaryEntity> summaryEntities = summaryRepository.findSummaryEntitiesByUser_Username(name);
        for (SummaryEntity summaryEntity : summaryEntities) {
            total += summaryEntity.getSum();
        }
        return total;
    }

    public void updatePercentFromAll(String username) {
        float total = getTotal(username);
        List<SummaryEntity> summaryEntities = summaryRepository.findSummaryEntitiesByUser_Username(username);
        for (SummaryEntity summaryEntity : summaryEntities) {
            summaryEntity.setPercentFromAll(Float.parseFloat(new DecimalFormat("#.#").format(summaryEntity.getSum()/total*100).replace(',','.')));
        }
        summaryRepository.saveAll(summaryEntities);
    }

    public void updateChangeFromInvested(String username) {
        List<SummaryEntity> summaryEntities = summaryRepository.findSummaryEntitiesByUser_Username(username);
        for (SummaryEntity summaryEntity : summaryEntities) {
            summaryEntity.setChangeFromInvested(summaryEntity.getSum()- summaryEntity.getInvested());
        }
        summaryRepository.saveAll(summaryEntities);
    }
}
