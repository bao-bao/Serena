package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.constant.TrendType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.MovingAverage;

public class MA520 extends AbstractStrategy {

    private TrendType lastTradeInTrend;

    public MA520(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_MA_520_PRIORITY);
        super.setName("MA 520");
        this.lastTradeInTrend = TrendType.NULL;
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing MA 520...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_520, interval);

        MovingAverage currentMA = dataSvcMgr.queryData(interval).getNewMAvgs();
        MovingAverage lastMA = dataSvcMgr.queryData(interval).getLastMAvgs();
        double cMA5 = currentMA.getMA5();
        double cMA20 = currentMA.getMA20();
        double lMA5 = lastMA.getMA5();
        double lMA20 = lastMA.getMA20();
        if(cMA20 == 0 || lMA20 == 0) {
            return decision;
        }

        TradingType currStatus = Status.getInstance().getStatus();

        if(cMA5 > cMA20 && lMA5 <= lMA20) {
            // Close the prior Short Selling if MA5 up cross MA20
            if(currStatus == TradingType.SHORT_SELLING) {
                decision.make(TradingType.EMPTY, "MA cross");
                return decision;
            }
            // Try to open a Put Buying if over the threshold
            else if(currStatus == TradingType.EMPTY) {
                return PutBuyingByThreshold(cMA5, cMA20, decision);
            }
            // Do nothing if current status is put buying
            else {
                return decision;
            }

        }

        else if(cMA5 < cMA20 && lMA5 >= lMA20) {
            // Close the prior Put Buying if MA5 and MA20 cross
            if(currStatus == TradingType.PUT_BUYING) {
                decision.make(TradingType.EMPTY, "MA cross");
                return decision;
            }
            // Try to open a Short Selling if over the threshold
            else if(currStatus == TradingType.EMPTY) {
                return ShortSellingByThreshold(cMA5, cMA20, decision);
            }
            // Do nothing if current status is put buying
            else {
                return decision;
            }
        }
        else {
            if(cMA5 > cMA20 && lMA5 > lMA20 && currStatus == TradingType.EMPTY && lastTradeInTrend != TrendType.TREND_UP) {
                return PutBuyingByThreshold(cMA5, cMA20, decision);
            } else if(cMA5 < cMA20 && lMA5 < lMA20 && currStatus == TradingType.EMPTY && lastTradeInTrend != TrendType.TREND_DOWN) {
                return ShortSellingByThreshold(cMA5, cMA20, decision);
            }
        }
        return decision;
    }

    private Decision PutBuyingByThreshold(double cMA5, double cMA20, Decision decision) {
        if(Math.abs(cMA5 - cMA20) >= Setting.TRADE_THRESHOLD) {
            lastTradeInTrend = TrendType.TREND_UP;
            decision.make(TradingType.PUT_BUYING, "empty");
        }
        return decision;
    }

    private Decision ShortSellingByThreshold(double cMA5, double cMA20, Decision decision) {
        if(Math.abs(cMA5 - cMA20) >= Setting.TRADE_THRESHOLD) {
            lastTradeInTrend = TrendType.TREND_DOWN;
            decision.make(TradingType.SHORT_SELLING, "empty");
        }
        return decision;
    }
}
