package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.MovingAverage;

public class MA240520 extends AbstractStrategy {
    public MA240520(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_MA_240_520_PRIORITY);
        super.setName("MA 240 520");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing MA 240 520...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_240_520, interval);

        MovingAverage currentMA = dataSvcMgr.queryData(interval).getNewMAvgs();
        MovingAverage lastMA = dataSvcMgr.queryData(interval).getLastMAvgs();
        double cMA5 = currentMA.getMA5();
        double cMA20 = currentMA.getMA20();
        double cMA240 = currentMA.getMA240();
        double lMA5 = lastMA.getMA5();
        double lMA20 = lastMA.getMA20();
        if(cMA240 == 0) {
            return decision;
        }

        TradingType currStatus = Status.getInstance().getStatus();

        if(cMA5 > cMA240) {
            if(cMA5 > cMA20 && lMA5 <= lMA20) {
                PutBuyingByThreshold(cMA5, cMA20, decision, currStatus);
            }
            if(cMA5 > cMA20 && lMA5 > lMA20 && currStatus != TradingType.PUT_BUYING) {
                PutBuyingByThreshold(cMA5, cMA20, decision, currStatus);
            }
            if(cMA5 < cMA20 && lMA5 > lMA20) {
                EmptyByThreshold(cMA5, cMA20, decision,currStatus);
            }
            if(cMA5 < cMA20 && lMA5 < lMA20 && currStatus != TradingType.EMPTY) {
                EmptyByThreshold(cMA5, cMA20, decision,currStatus);
            }
        }
        if(cMA5 < cMA240) {
            if(cMA5 < cMA20 && lMA5 > lMA20) {
                ShortSellingByThreshold(cMA5, cMA20, decision, currStatus);
            }
            if(cMA5 < cMA20 && lMA5 < lMA20 && currStatus != TradingType.SHORT_SELLING) {
                ShortSellingByThreshold(cMA5, cMA20, decision, currStatus);
            }
            if(cMA5 > cMA20 && lMA5 <= lMA20) {
                EmptyByThreshold(cMA5, cMA20, decision, currStatus);
            }
            if(cMA5 > cMA20 && lMA5 > lMA20 && currStatus != TradingType.EMPTY) {
                EmptyByThreshold(cMA5, cMA20, decision,currStatus);
            }
        }
        return decision;
    }
}
