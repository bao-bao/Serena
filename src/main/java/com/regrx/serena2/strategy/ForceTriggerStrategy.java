package com.regrx.serena2.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;

public abstract class ForceTriggerStrategy extends AbstractStrategy {
    protected int triggerHour;
    protected int triggerMinute;

    ForceTriggerStrategy(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_FORCE_TRIGGER_PRIORITY);
    }

    protected void setTriggerTime(int hour, int minute) {
        this.triggerHour = hour;
        this.triggerMinute = minute;
    }

    public abstract boolean isTriggered(int hour, int minute);
}
