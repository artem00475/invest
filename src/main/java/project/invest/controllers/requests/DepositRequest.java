package project.invest.controllers.requests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class DepositRequest {
    private String sum;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
}
