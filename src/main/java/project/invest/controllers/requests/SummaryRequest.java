package project.invest.controllers.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryRequest {
    private String instrumentName;
    private String sum;
    private String change;
    private String changeInPercents;
    private String percentFromAll;
    private String invested;
    private String changeFromInvested;
}
