package ma.ip.enums;

public enum AccountingStatusEnum {
    OK("y"),
    REJECTED("r"),
    ERROR("e"),
    TIMEOUT("t");

    private String value;

    private AccountingStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
