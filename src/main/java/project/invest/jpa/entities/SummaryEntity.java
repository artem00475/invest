package project.invest.jpa.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "summary")
@Getter
@Setter
public class SummaryEntity {
    @Id
    @GeneratedValue
    private long id;

    private String instrumentName;
    private float sum;
    private float change;
    private float changeInPercents;
    private float percentFromAll;
    private float invested;
    private float changeFromInvested;
    private String userName;
}
