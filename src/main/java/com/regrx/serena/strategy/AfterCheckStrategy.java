package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.data.base.Decision;

public abstract class AfterCheckStrategy extends AbstractStrategy {

    AfterCheckStrategy() {
        super(IntervalEnum.NULL, Setting.DEFAULT_PRIORITY);
    }

    public abstract Decision check(Decision origin);
}
