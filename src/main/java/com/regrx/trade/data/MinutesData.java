package com.regrx.trade.data;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.file.CsvWriter;
import com.regrx.trade.statistic.MovingAverage;

import java.util.Date;
import java.util.LinkedList;

public class MinutesData {
    private final int interval;
    private final LinkedList<Double> prices;
    private final LinkedList<MovingAverage> movingAverages;
    private Date currentTime;

    public MinutesData(int interval) {
        this.interval = interval;
        prices = new LinkedList<>();
        movingAverages = new LinkedList<>();
    }

    private void updatePrice(double price) {
        if(prices.size() >= Constant.MAX_LENGTH) {
            prices.remove();
        }
        prices.add(price);
    }

    private void updateMovingAverage() {
        if(movingAverages.size() >= Constant.MAX_LENGTH) {
            movingAverages.remove();
        }
        movingAverages.add(new MovingAverage(prices));
    }

    public void update(PriceData newPrice, String type, boolean write) {
        long priceTime = newPrice.getDate().getTime();
        if(currentTime != null && currentTime.getTime() == priceTime) {
            return;
        }
        currentTime = newPrice.getDate();
        this.updatePrice(newPrice.getPrice());
        this.updateMovingAverage();
        if(write) {
            CsvWriter.write("Minute_" + type + "_" + interval, this);
        }
    }

    public Double getLastPrice() {
        return prices.getLast();
    }

    public MovingAverage getLastMovingAverage() {
        return movingAverages.getLast();
    }

    public Date getCurrentTime() {
        return currentTime;
    }

    public LinkedList<MovingAverage> getMovingAverages() {
        return movingAverages;
    }

    public LinkedList<Double> getPrices() {
        return prices;
    }
}