package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.AccountBuy;

import java.util.List;

public interface AccountBuyRepository extends JpaRepository<AccountBuy, Long> {
    List<AccountBuy> findAllByInstrumentName(String name);
}
