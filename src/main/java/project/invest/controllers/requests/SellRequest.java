package project.invest.controllers.requests;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class SellRequest {
    private String ticker;
    private String cost;
    private String count;
}
