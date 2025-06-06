package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
