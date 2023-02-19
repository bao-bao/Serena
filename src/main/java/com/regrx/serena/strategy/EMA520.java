package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;
import com.regrx.serena.service.StrategyManager;

public class EMA520 extends AbstractStrategy {

    private TrendType lastTradeInTrend;

    public EMA520(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_MA_520_PRIORITY);
        super.setName("EMA 520");
        this.lastTradeInTrend = TrendType.NULL;
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing EMA 520...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_520, interval);

        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        double cShortEMA = EMA.getCurrentEMAByEnum(EMAEnum.UP_SHORT_TERM_EMA);
        double cLongEMA = EMA.getCurrentEMAByEnum(EMAEnum.UP_LONG_TERM_EMA);
        double lShortEMA = EMA.getHistoryEMAByEnum(EMAEnum.UP_SHORT_TERM_EMA, 1);
        double lLongEMA = EMA.getHistoryEMAByEnum(EMAEnum.UP_LONG_TERM_EMA, 1);
        if(cLongEMA == 0) {
            return decision;
        }

        TradingType currStatus = Status.getInstance().getStatus();

        if((cShortEMA - cLongEMA) * (lShortEMA - lLongEMA) < 0) {
            StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }

        if(cShortEMA > cLongEMA && lShortEMA <= lLongEMA) {
            PutBuyingByThreshold(cShortEMA, cLongEMA, decision, currStatus);
        }

        else if(cShortEMA < cLongEMA && lShortEMA >= lLongEMA) {
            ShortSellingByThreshold(cShortEMA, cLongEMA, decision, currStatus);
        }
        else {
            if(cShortEMA > cLongEMA && lShortEMA > lLongEMA && currStatus != TradingType.PUT_BUYING && lastTradeInTrend != TrendType.TREND_UP) {
                PutBuyingByThreshold(cShortEMA, cLongEMA, decision, currStatus);
            } else if(cShortEMA < cLongEMA && lShortEMA < lLongEMA && currStatus != TradingType.SHORT_SELLING && lastTradeInTrend != TrendType.TREND_DOWN) {
                ShortSellingByThreshold(cShortEMA, cLongEMA, decision, currStatus);
            }
        }
        if(decision.isExecute()) {
            TrendType currTradeInTrend = cShortEMA >= cLongEMA ? TrendType.TREND_UP : TrendType.TREND_DOWN;
            Status.getInstance().setTrend(currTradeInTrend);
            if(currTradeInTrend != lastTradeInTrend && decision.getTradingType() == TradingType.EMPTY && Setting.MA_PRIMARY) {
                StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.DEFAULT_LOSS_LIMIT_PRIORITY);
                StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.DEFAULT_PROFIT_LIMIT_PRIORITY);
            }
            if(decision.getTradingType() != TradingType.EMPTY) {
                StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
                StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
//                lastTradeInTrend = currTradeInTrend;
            }
            lastTradeInTrend = currTradeInTrend;        // sometime will not open
        }
        return decision;
    }
}
