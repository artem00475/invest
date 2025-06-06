package project.invest.jpa.entities;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;


@Entity
@Getter
@Setter
@Table(name = "t_role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    @Override
    public String getAuthority() {
        return getName();
    }

    public Role() {
    }

    public Role(Long id) {
        this.id = id;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
