package project.invest.jpa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Dividends;

public interface DividendsRepository extends JpaRepository<Dividends, Long> {

    Page<Dividends> findAllByInstrumentName(String name, Pageable pageable);
}
