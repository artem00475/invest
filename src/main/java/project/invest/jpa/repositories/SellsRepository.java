package project.invest.jpa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.AccountSell;


public interface SellsRepository extends JpaRepository<AccountSell, Long> {
    Page<AccountSell> findAllByInstrumentName(String name, Pageable pageable);
}
