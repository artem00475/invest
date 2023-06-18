package project.invest.controllers.requests;

import lombok.Getter;
import lombok.Setter;
import project.invest.jpa.entities.InstrumentTypeEnum;

@Getter
@Setter
public class SummaryRequest {
    private String instrumentName;
    private String type;
}
