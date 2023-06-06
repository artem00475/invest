package project.invest.controllers.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Setter
@Getter
public class SellRequest {
    private String ticker;
    private String cost;
    private String count;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
