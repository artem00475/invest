package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.InstrumentTypeEnum;
import project.invest.jpa.entities.SummaryEntity;

import java.util.List;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {
    List<SummaryEntity> findAllByUserName(String user);
    List<SummaryEntity> findAllByUserNameAndInstrumentTypeEnum(String user, InstrumentTypeEnum instrumentTypeEnum);
    SummaryEntity findByInstrumentName(String name);
}
