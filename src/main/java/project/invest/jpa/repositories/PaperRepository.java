package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Paper;
import project.invest.jpa.entities.PaperTypeEnum;

import java.util.List;

public interface PaperRepository extends JpaRepository<Paper, String> {
    List<Paper> findAllByType(PaperTypeEnum type);

    Paper findByTicker(String ticker);
    void removeByTicker(String ticker);
}
