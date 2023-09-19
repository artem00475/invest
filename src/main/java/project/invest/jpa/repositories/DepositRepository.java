package project.invest.jpa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Deposit;


public interface DepositRepository extends JpaRepository<Deposit, Long> {
    Page<Deposit> findAllByInstrumentName(String name, Pageable pageable);
}
