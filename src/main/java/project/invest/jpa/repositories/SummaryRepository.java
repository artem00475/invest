package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.InstrumentTypeEnum;
import project.invest.jpa.entities.SummaryEntity;

import java.util.List;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {
    List<SummaryEntity> findSummaryEntitiesByUser_Username(String user);
    List<SummaryEntity> findAllByUserUsernameAndInstrumentTypeEnum(String user, InstrumentTypeEnum instrumentTypeEnum);
    SummaryEntity findByInstrumentName(String name);
}
