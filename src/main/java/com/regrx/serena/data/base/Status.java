package com.regrx.serena.data.base;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.constant.TrendType;

public class Status {
    private boolean trading;
    private int count;
    private TradingType status;
    private TrendType trend;
    private IntervalEnum interval;
    private StrategyEnum strategy;
    private double lastTradePrice;
    private static Status stat;

    private Status() {
        trading = true;
        count = 0;
        status = TradingType.EMPTY;
        interval = IntervalEnum.NULL;
        strategy = StrategyEnum.STRATEGY_NULL;
        lastTradePrice = 0;
    }

    public static Status getInstance() {
        if(stat == null) {
            stat = new Status();
        }
        return stat;
    }

    public static Status reset() {
        stat = new Status();
        return stat;
    }

    public boolean isTrading() {
        return trading;
    }

    public void setTrading(boolean trading) {
        this.trading = trading;
    }

    public TrendType getTrend() {
        return trend;
    }

    public void setTrend(TrendType trend) {
        this.trend = trend;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public TradingType getStatus() {
        return status;
    }

    public void setStatus(TradingType status) {
        this.status = status;
    }

    public IntervalEnum getInterval() {
        return interval;
    }

    public void setInterval(IntervalEnum interval) {
        this.interval = interval;
    }

    public StrategyEnum getStrategy() {
        return strategy;
    }

    public void setStrategy(StrategyEnum strategy) {
        this.strategy = strategy;
    }

    public double getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(double lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    @Override
    public String toString() {
        if(status == TradingType.EMPTY) {
            return "Empty";
        } else if(status == TradingType.SHORT_SELLING) {
            return "Short Selling";
        } else if(status == TradingType.PUT_BUYING) {
            return "Put Buying";
        }
        return "";
    }
}
