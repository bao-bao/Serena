package com.regrx.serena.data.base;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.constant.TrendType;

public class Status {
    private boolean trading;        // 交易开关
    private int count;              // 持有手数（暂无作用）
    private TradingType status;     // 当前交易方向
    private TrendType trend;        // 走势（MA）
    private TrendType trendEMA;        // 走势（EMA）
    private IntervalEnum interval;  // 上次交易使用数据间隔
    private StrategyEnum strategy;  // 上次交易使用逻辑
    private double lastTradePrice;  // 上次交易价格
    private static Status stat;

    private Status() {
        trading = true;
        count = 0;
        status = TradingType.EMPTY;
        trend = TrendType.NULL;
        trendEMA = TrendType.NULL;
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

    public static void reset() {
        stat = new Status();
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

    public TrendType getTrendEMA() {
        return trendEMA;
    }

    public void setTrendEMA(TrendType trendEMA) {
        this.trendEMA = trendEMA;
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

    // DO NOT use in strategies
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
        return status.toString();
    }
}
