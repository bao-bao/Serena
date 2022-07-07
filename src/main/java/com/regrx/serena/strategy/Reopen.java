package com.regrx.serena.strategy;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.service.StrategyManager;

public class Reopen extends ForceTriggerStrategy {

    public Reopen() {
        super(IntervalEnum.NULL);
        super.setName("Reopen");
        this.setTriggerTime(6, 0);      // 06:00:00 to reopen trade for next day
    }


    public Reopen(int hour, int minute) {
        super(IntervalEnum.NULL);
        super.setName("Reopen");
        this.setTriggerTime(hour, minute);

    }

    @Override
    public Decision execute(ExPrice price) {
        Status.getInstance().setTrading(true);
        StrategyManager.getInstance().removeStrategy(StrategyEnum.STRATEGY_REOPEN);
        StrategyManager.getInstance().addStrategy(StrategyEnum.STRATEGY_CLOSE_ON_END, new CloseOnEnd());
        return new Decision();
    }

    @Override
    public boolean isTriggered(int hour, int minute) {
        return hour == this.triggerHour && minute == this.triggerMinute;
    }
}
