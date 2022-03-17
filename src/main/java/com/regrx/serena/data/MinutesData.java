package com.regrx.serena.data;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.TrendType;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.Setting;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.statistic.MovingAverage;
import com.regrx.serena.data.statistic.PostAnalysis;

import java.util.LinkedList;

public class MinutesData {
    private final IntervalEnum interval;
    private final LinkedList<ExPrice> prices;
    private final LinkedList<MovingAverage> mAvgs;
    private final PostAnalysis postAnalysis;
    private int size;
    private double lastPrice;
    private MovingAverage lastMAvgs;
    private String lastRecordTime;
    private double newPrice;
    private MovingAverage newMAvgs;
    private String newRecordTime;

    public MinutesData(IntervalEnum interval) {
        this.interval = interval;
        this.prices = new LinkedList<>();
        this.mAvgs = new LinkedList<>();
        this.postAnalysis = new PostAnalysis();
        this.size = 0;
        this.lastPrice = 0.0;
        this.lastMAvgs = new MovingAverage();
        this.lastRecordTime = "NULL";
        this.newPrice = 0.0;
        this.newMAvgs = new MovingAverage();
        this.newRecordTime = "NULL";
    }

    public void update(ExPrice newPrice, String type) {
        updateWithoutWrite(newPrice);
        FileUtil.writeMinutesDataToCsv("Minute_" + type + "_" + interval.getValue(), this);

        LogUtil.getInstance().info("update " + interval.getValue() + " min(s) data for " + type + " at " + newPrice.getPrice());
    }

    public void updateWithoutWrite(ExPrice comingPrice) {
        String comingRecordTime = comingPrice.getTime();
        if(newRecordTime != null && newRecordTime.equals(comingRecordTime)) {
            return;
        }
        lastRecordTime = newRecordTime;
        newRecordTime = comingRecordTime;

        lastPrice = newPrice;
        newPrice = comingPrice.getPrice();
        this.updateList(prices, comingPrice);

        lastMAvgs = newMAvgs;
        newMAvgs = new MovingAverage(prices);
        this.updateList(mAvgs, newMAvgs);

        postAnalysis.update(prices, mAvgs);
        size = prices.size();
    }

    private <T> void updateList(LinkedList<T> list, T newItem) {
        if(list.size() >= Setting.MAX_LENGTH) {
            list.removeLast();
        }
        list.addFirst(newItem);
    }

    public IntervalEnum getInterval() {
        return interval;
    }

    public LinkedList<ExPrice> getPrices() {
        return prices;
    }

    public LinkedList<MovingAverage> getMAvgs() {
        return mAvgs;
    }

    public PostAnalysis getPostAnalysis() {
        return postAnalysis;
    }

    public int size() {
        return size;
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public MovingAverage getLastMAvgs() {
        return lastMAvgs;
    }

    public String getLastRecordTime() {
        return lastRecordTime;
    }

    public double getNewPrice() {
        return newPrice;
    }

    public MovingAverage getNewMAvgs() {
        return newMAvgs;
    }

    public String getNewRecordTime() {
        return newRecordTime;
    }

    public TrendType getTrend() {
        double variation = prices.get(0).getPrice() + prices.get(1).getPrice();
        if(variation <= 0) {
            return TrendType.TREND_DOWN;
        } else {
            return TrendType.TREND_UP;
        }
    }
}