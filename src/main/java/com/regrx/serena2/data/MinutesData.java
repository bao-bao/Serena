package com.regrx.serena2.data;

import com.regrx.serena2.common.Setting;
import com.regrx.serena2.common.constant.EMAEnum;
import com.regrx.serena2.common.constant.IntervalEnum;
import com.regrx.serena2.common.constant.MAEnum;
import com.regrx.serena2.common.constant.TrendType;
import com.regrx.serena2.common.utils.FileUtil;
import com.regrx.serena2.common.utils.LogUtil;
import com.regrx.serena2.data.base.ExPrice;
import com.regrx.serena2.data.statistic.ExpMovingAverage;
import com.regrx.serena2.data.statistic.MovingAverage;
import com.regrx.serena2.data.statistic.PostAnalysis;

import java.util.ArrayList;
import java.util.LinkedList;

public class MinutesData {
    private final IntervalEnum interval;
    private final LinkedList<ExPrice> prices;
    private final LinkedList<MovingAverage> mAvgs;
    private final ExpMovingAverage expMAvgs;
    private final PostAnalysis postAnalysis;
    private int size;
    private MovingAverage lastMAvg;
    private MovingAverage newMAvg;
    private ArrayList<Double> lastEMAvg;
    private ArrayList<Double> newEMAvg;
    private String lastRecordTime;
    private String newRecordTime;
    private double lastPrice;
    private double newPrice;

    public MinutesData(IntervalEnum interval) {
        this.interval = interval;
        this.prices = new LinkedList<>();
        this.mAvgs = new LinkedList<>();
        this.expMAvgs = new ExpMovingAverage();
        this.postAnalysis = new PostAnalysis();
        this.size = 0;
        this.lastMAvg = new MovingAverage();
        this.newMAvg = new MovingAverage();
        this.lastEMAvg = new ArrayList<>();
        this.newEMAvg = new ArrayList<>();
        this.lastRecordTime = "NULL";
        this.newRecordTime = "NULL";
        this.lastPrice = 0.0;
        this.newPrice = 0.0;
    }

    public void update(ExPrice newPrice, String type) {
        updateWithoutWrite(newPrice);
        FileUtil.writeMinutesDataToCsv("Minute_" + type + "_" + interval.getValue(), this);

        String msg = "update " + interval.getValue() + " min(s) data for " + type +
                " at " + newPrice.getPrice() +
                ", EMAs are: [" +
                String.format("%.2f", newEMAvg.get(0)) + ", " +
                String.format("%.2f", newEMAvg.get(1)) + ", " +
                String.format("%.2f", newEMAvg.get(2)) + ", " +
                String.format("%.2f", newEMAvg.get(3)) + "]";
        LogUtil.getInstance().info(msg);
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

        lastMAvg = newMAvg;
        newMAvg = new MovingAverage(prices);
        this.updateList(mAvgs, newMAvg);

        lastEMAvg = new ArrayList<>(newEMAvg);
        expMAvgs.update(comingPrice);
        newEMAvg = expMAvgs.getAllCurrentEMA();


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

    public ExpMovingAverage getExpMAvgs() {
        return expMAvgs;
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

    public double getNewPrice() {
        return newPrice;
    }

    public MovingAverage getLastMAvg() {
        return lastMAvg;
    }

    public MovingAverage getNewMAvg() {
        return newMAvg;
    }

    public String getLastRecordTime() {
        return lastRecordTime;
    }

    public String getNewRecordTime() {
        return newRecordTime;
    }

    public ArrayList<Double> getLastEMAvg() {
        return lastEMAvg;
    }

    public ArrayList<Double> getNewEMAvg() {
        return newEMAvg;
    }

    public TrendType getTrend() {
        double variation = newPrice - lastPrice;
        if(variation <= 0) {
            return TrendType.TREND_DOWN;
        } else {
            return TrendType.TREND_UP;
        }
    }

    public double getLastMACrossPrice(MAEnum ind_1, MAEnum ind_2) {
        return MovingAverage.evalLastCrossPrice(mAvgs, ind_1, ind_2);
    }

    public  double getLastEMACrossPrice(EMAEnum ema1, EMAEnum ema2) {
        int crossIndex = expMAvgs.findLastCrossIndex(ema1, ema2);
        if(crossIndex != -1) {
            return prices.get(crossIndex).getPrice();
        }
        return 0.0;
    }

}