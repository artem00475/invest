package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.SummaryEntity;

import java.util.List;

public interface SummaryRepository extends JpaRepository<SummaryEntity, Long> {
    List<SummaryEntity> findAllByUserName(String user);
}
