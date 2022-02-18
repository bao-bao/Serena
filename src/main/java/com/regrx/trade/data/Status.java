package com.regrx.trade.data;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.statistic.MovingAverage;

public class Status {
    private int count;
    private int status;
    private int interval;
    private double lastTradePrice;
    private Boolean trend;

    public Status() {
        count = 0;
        status = Constant.EMPTY;
        interval = 0;
        lastTradePrice = 0;
        trend = null;
    }

    public Status(int count, int status, int interval, double lastTradePrice, boolean trend) {
        this.count = count;
        this.status = status;
        this.interval = interval;
        this.lastTradePrice = lastTradePrice;
        this.trend = trend;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public double getLastTradePrice() {
        return lastTradePrice;
    }

    public void setLastTradePrice(double lastTradePrice) {
        this.lastTradePrice = lastTradePrice;
    }

    public Boolean getTrend() {
        return trend;
    }

    public void setTrend(Boolean trend) {
        this.trend = trend;
    }

    public void updateTrend(MovingAverage ma) {
        this.trend = (ma.getMA5() - ma.getMA20()) > 0;
    }

    @Override
    public String toString() {
        if(status == Constant.EMPTY) {
            return "Empty";
        } else if(status == Constant.SHORT_SELLING) {
            return "Short Selling";
        } else if(status == Constant.PUT_BUYING) {
            return "Put Buying";
        } else if(status == Constant.BOTH) {
            return "Both";
        }
        return "";
    }
}
