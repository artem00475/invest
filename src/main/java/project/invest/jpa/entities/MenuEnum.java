package project.invest.jpa.entities;

public enum MenuEnum {
    HOME("Аккаунт", "/Accounting"),
    DEPOSIT("Пополнить", "/Accounting/Deposit"),
    BUYS("Покупки", "/Accounting/Buy"),
    DIVIDENDS("Дивиденды", "/Accounting/Dividends"),
    SELLS("Продажи", "/Accounting/Sell"),
    COMMISION("Комиссия", "/Accounting/Commission"),
    AMMORTISATION("Аммортизация", "/Accounting/Amortization");
    private final String displayValue;
    private final String url;

    private MenuEnum(String displayValue, String url) {
        this.displayValue = displayValue;
        this.url = url;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getUrl() {
        return url;
    }

    public static String getValueByUri(String uri) {
        for (MenuEnum menu : MenuEnum.values()) {
            if (menu.url.equals(uri)) return menu.displayValue;
        }
        return "";
    }
}
