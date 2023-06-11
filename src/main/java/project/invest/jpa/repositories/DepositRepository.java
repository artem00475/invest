package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Deposit;

import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, String> {
    List<Deposit> findAllByInstrumentName(String name);
}
