package project.invest.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import project.invest.jpa.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getRoleById(Long id);
}