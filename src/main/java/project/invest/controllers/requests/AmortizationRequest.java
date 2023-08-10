package project.invest.controllers.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class AmortizationRequest {
    private String ticker;
    private String cost;
    private String count;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
