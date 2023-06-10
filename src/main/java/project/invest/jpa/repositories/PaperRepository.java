package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Paper;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, String> {
    List<Paper> findAll();

    Paper findByTicker(String ticker);
    void removeByTicker(String ticker);
}
