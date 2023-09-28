package project.invest.jpa.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    @ManyToOne
    private  User user;
    private float balance;
    private float result;
    private InstrumentTypeEnum instrumentTypeEnum;
}
