package project.invest.jpa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Amortization;


public interface AmortizationRepository extends JpaRepository<Amortization, Long> {

    Page<Amortization> findAllByInstrumentName(String name, Pageable pageable);
}
