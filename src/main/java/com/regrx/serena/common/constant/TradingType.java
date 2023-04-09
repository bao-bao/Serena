package com.regrx.serena.common.constant;

public enum TradingType {
    NO_ACTION("Null"),
    SHORT_SELLING("Short"),
    PUT_BUYING("Long"),
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
