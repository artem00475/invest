package project.invest.jpa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Commission;


public interface CommissionRepository extends JpaRepository<Commission, Long> {
    Page<Commission> findAllByInstrumentName(String name, Pageable pageable);
}
