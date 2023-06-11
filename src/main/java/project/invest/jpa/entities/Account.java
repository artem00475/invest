package project.invest.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue
    private long id;

    private String ticker;
    private float maxShare;
    private float currentCost;
    private int count;
    private float currentShare;
    private float averageCost;
    private float change;
    private float shareDifference;
    private float toBuy;
    private float changeInPercents;
    private float dividends;
    private String instrumentName;
    private String name;
}
