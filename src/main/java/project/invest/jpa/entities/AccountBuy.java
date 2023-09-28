package project.invest.jpa.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
public class AccountBuy {
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
    private String instrumentName;
    @ManyToOne
    private  User user;
}