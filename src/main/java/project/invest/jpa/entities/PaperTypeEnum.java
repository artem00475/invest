package project.invest.jpa.entities;

public enum PaperTypeEnum {
    STOCK("Акция"),
    BOND("Облигация"),
    CURRENCY("Валюта"),
    FUND("Фонд");

    private final String displayValue;

    private PaperTypeEnum(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
