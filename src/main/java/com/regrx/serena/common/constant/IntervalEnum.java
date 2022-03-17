package com.regrx.serena.common.constant;

import java.util.HashMap;
import java.util.Map;

public enum IntervalEnum {
    MIN_1(1),
    MIN_3(3),
    MIN_5(5),
    MIN_15(15),
    MIN_30(30),
    MIN_60(60),
    NULL(0);

    private final int value;

    private static final Map<Integer, IntervalEnum> intMap = new HashMap<>();
    static {
        for (IntervalEnum type : IntervalEnum.values()) {
            intMap.put(type.value, type);
        }
    }

    IntervalEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static IntervalEnum fromInt(int i) {
        IntervalEnum type = intMap.get(i);
        if (type == null) {
            return IntervalEnum.NULL;
        }
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
