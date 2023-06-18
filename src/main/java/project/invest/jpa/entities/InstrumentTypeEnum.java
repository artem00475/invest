package project.invest.jpa.entities;

public enum InstrumentTypeEnum {
    brokerageAccount("Брокерский счёт"),
    crowdfunding("Краудфандинг");

    private final String displayValue;

    private InstrumentTypeEnum(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
