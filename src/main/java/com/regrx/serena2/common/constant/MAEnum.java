package com.regrx.serena2.common.constant;

import java.util.HashMap;
import java.util.Map;

public enum MAEnum {
    MA5(5),
    MA10(10),
    MA20(20),
    MA30(30),
    MA60(60),
    MA90(90),
    MA120(120),
    MA250(250),
    MA_NULL(0);

    private final int value;

    private static final Map<Integer, MAEnum> intMap = new HashMap<>();
    static {
        for (MAEnum type : MAEnum.values()) {
            intMap.put(type.value, type);
        }
    }

    MAEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MAEnum fromInt(int i) {
        MAEnum type = intMap.get(i);
        if (type == null) {
            return MAEnum.MA_NULL;
        }
        return type;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
