package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByInstrumentNameAndTickerAndUser_Username(String name, String ticker, String userName);
    void removeById(long id);
    List<Account> findAllByInstrumentNameAndUser_Username(String name, String userName);
    List<Account> findAll();
    List<Account> findAllByTicker(String ticker);
}
