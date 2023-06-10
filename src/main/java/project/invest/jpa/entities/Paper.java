package project.invest.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Paper {

    @Id
    private String ticker;
    private float cost;

}
