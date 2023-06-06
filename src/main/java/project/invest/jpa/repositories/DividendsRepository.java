package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Dividends;

import java.util.List;

public interface DividendsRepository extends JpaRepository<Dividends, Long> {

    List<Dividends> findAllByInstrumentName(String name);
}
