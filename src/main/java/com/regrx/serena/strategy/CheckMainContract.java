package com.regrx.serena.strategy;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.service.StrategyManager;

import java.util.Calendar;
import java.util.TimeZone;


public class CheckMainContract extends ForceTriggerStrategy {

    public CheckMainContract(IntervalEnum interval) {
        super(interval);
        super.setName("Check Main Contract");
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_CHECK_MAIN_CONTRACT, interval);

        Status status = Status.getInstance();
        if (status.getStatus() != TradingType.EMPTY) {
            Calendar lastTrade = Status.getInstance().getLastTradTime();
            int tradeMonth = lastTrade.get(Calendar.MONTH);

            Calendar currTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            int currMonth = currTime.get(Calendar.MONTH);

            if (tradeMonth != currMonth) {
                TradingType trade = status.getStatus() == TradingType.PUT_BUYING ? TradingType.SHORT_SELLING : TradingType.PUT_BUYING;
                decision.make(trade, "main contract change");
            }
        }

        StrategyManager.getInstance().removeStrategy(StrategyEnum.STRATEGY_CHECK_MAIN_CONTRACT);
        return decision;
    }

    @Override
    public boolean isTriggered(int hour, int minute) {
        return true;
    }
}
