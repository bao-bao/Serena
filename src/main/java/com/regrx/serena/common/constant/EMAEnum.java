package com.regrx.serena.common.constant;

import java.util.HashMap;
import java.util.Map;

public enum EMAEnum {
    UP_SHORT_TERM_EMA(0),
    UP_LONG_TERM_EMA(1),
    DOWN_SHORT_TERM_EMA(2),
    DOWN_LONG_TERM_EMA(3),
    SINGLE_EMA_UP(4),
    SINGLE_EMA_DOWN(5),
    EMA_NULL(-1);

    private final int value;

    private static final Map<Integer, EMAEnum> intMap = new HashMap<>();
    static {
        for (EMAEnum type : EMAEnum.values()) {
            intMap.put(type.value, type);
        }
    }

    EMAEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static EMAEnum fromInt(int i) {
        EMAEnum type = intMap.get(i);
        if (type == null) {
            return EMAEnum.EMA_NULL;
        }
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}

