package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Commission;

import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findAllByInstrumentName(String name);
}
