package com.regrx.serena.common.constant;

import java.util.HashMap;
import java.util.Map;

public enum EMAEnum {
    EMA_1(1),
    EMA_2(2),
    EMA_3(3),
    EMA_4(4),
    EMA_NULL(0);

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

