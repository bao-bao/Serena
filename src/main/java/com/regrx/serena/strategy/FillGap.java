package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.MovingAverage;


public class FillGap extends AbstractStrategy {

    public FillGap(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_FILL_GAP_PRIORITY);
        super.setName("Fill Gap");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing Fill Gap...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_520, interval);

        if(Status.getInstance().getStatus() != TradingType.EMPTY) {
            return decision;
        }

        MinutesData data = dataSvcMgr.queryData(interval);
        MovingAverage currentMA = data.getNewMAvgs();
        double currentPrice = data.getNewPrice();

        if(currentMA.getMA5() > currentMA.getMA20() && currentPrice - currentMA.getMA5() > Setting.FILL_GAP_THRESHOLD) {
            decision.make(TradingType.PUT_BUYING, "exceed MA5 too far");
        }

        if(currentMA.getMA5() < currentMA.getMA20() && currentMA.getMA5() - currentPrice < Setting.FILL_GAP_THRESHOLD) {
            decision.make(TradingType.SHORT_SELLING, "exceed MA5 too far");
        }
        return decision;
    }
}
