package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Amortization;

import java.util.List;

public interface AmortizationRepository extends JpaRepository<Amortization, Long> {

    List<Amortization> findAllByInstrumentName(String name);
}
