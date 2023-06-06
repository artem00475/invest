package project.invest.controllers.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyRequest {
    private String ticker;
    private String cost;
    private String count;
}
