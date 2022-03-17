package com.regrx.serena.common.constant;

public enum TradingType {
    NO_ACTION("Null"),
    SHORT_SELLING("ShortSelling"),
    PUT_BUYING("PutBuying"),
    EMPTY("Empty");

    private final String value;

    TradingType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
