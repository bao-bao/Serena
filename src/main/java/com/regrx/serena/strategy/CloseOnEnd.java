package com.regrx.serena.strategy;

import com.regrx.serena.common.constant.FutureType;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.PreparationUtil;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.service.StrategyManager;

public class CloseOnEnd extends ForceTriggerStrategy {

    public CloseOnEnd() {
        super(IntervalEnum.NULL);
        super.setName("Close On End");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing Close On End...");

        Decision decision = new Decision(price, StrategyEnum.STRATEGY_CLOSE_ON_END, interval);
        Status currStat = Status.getInstance();
        if(currStat.getStatus() == TradingType.PUT_BUYING || currStat.getStatus() == TradingType.SHORT_SELLING) {
            decision.make(TradingType.EMPTY, "near to daily end");
        }
        currStat.setTrading(false);
        StrategyManager.getInstance().removeStrategy(StrategyEnum.STRATEGY_CLOSE_ON_END);
        StrategyManager.getInstance().addStrategy(StrategyEnum.STRATEGY_REOPEN, new Reopen());  // default reopen time: 06:00:00

        return decision;
    }

    @Override
    public boolean isTriggered(int hour, int minute) {
        FutureType futureType = PreparationUtil.getBreed(Controller.getInstance().getType());
        return PreparationUtil.fiveMinutesLeft(futureType, hour, minute);
    }
}
