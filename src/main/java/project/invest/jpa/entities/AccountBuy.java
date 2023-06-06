package project.invest.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class AccountBuy {
    @Id
    @GeneratedValue
    private long id;

    private Date date;
    private String ticker;
    private float cost;
    private int count;
    private float sum;
    private String instrumentName;

}