package project.invest.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Setter
@Getter
public class AccountSell {
    @Id
    @GeneratedValue
    private long id;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;
    private String ticker;
    private float cost;
    private int count;
    private float sum;
    private float averageCost;
    private float averageSum;
    private float change;
    private String instrumentName;
    @ManyToOne
    private  User user;
}
