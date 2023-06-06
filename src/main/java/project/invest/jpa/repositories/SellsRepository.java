package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.AccountSell;

import java.util.List;

public interface SellsRepository extends JpaRepository<AccountSell, Long> {
    List<AccountSell> findAllByInstrumentName(String name);
}
