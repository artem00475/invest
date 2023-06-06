package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByInstrumentNameAndTicker(String name, String ticker);
    void removeAccountByInstrumentNameAndTicker(String name, String ticker);
    List<Account> findAllByInstrumentName(String name);
}
