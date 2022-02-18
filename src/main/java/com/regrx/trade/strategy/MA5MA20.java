package com.regrx.trade.strategy;


import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.PriceData;
import com.regrx.trade.data.Status;
import com.regrx.trade.statistic.MovingAverage;
import com.regrx.trade.util.Log;
import com.regrx.trade.util.Trade;
import com.regrx.trade.util.Utils;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MA5MA20 implements Callable<Status> {
    private final LinkedList<MovingAverage> movingAverages;
    private final Status status;
    private final String type;
    private final int interval;
    private final int breed;
    private final PriceData currPrice;

    public MA5MA20(LinkedList<MovingAverage> ma, Status status, String type, int interval, int breed, PriceData currPrice) {
        this.movingAverages = ma;
        this.status = status;
        this.type = type;
        this.interval = interval;
        this.breed = breed;
        this.currPrice = currPrice;
    }

    @Override
    public Status call() {
        MovingAverage currentMA = movingAverages.getLast();
        MovingAverage lastMA = movingAverages.get(movingAverages.size() - 2);
        double cMA5 = currentMA.getMA5();
        double cMA20 = currentMA.getMA20();
        double lMA5 = lastMA.getMA5();
        double lMA20 = lastMA.getMA20();
        if(cMA20 == 0 || lMA20 == 0) {
            return status;
        }
        //TODO: 如果交叉长度小于10/15，多空双开，直到交叉长度大于10/15.
        //TODO：现价大于、小于平均值过多，放弃
        //TODO: 开盘尾盘不拿单
        int currStatus = this.status.getStatus();

        // Thread for key sprite
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        // empty if only 5 minutes left
        if(Utils.fiveMinutesLeft(breed)) {
            if(currStatus == Constant.PUT_BUYING || currStatus == Constant.SHORT_SELLING) {
                Log.emptyLog(currPrice, interval, "5 minutes left", type);
                return Trade.empty(currPrice, status, newCachedThreadPool);
            } else if(currStatus == Constant.EMPTY) {
                return status;
            }
        }
//
//        if(currStatus == Constant.BOTH) {
//            if(Utils.LastCrossDiffGreaterThan(movingAverages, Constant.MA5, Constant.MA20, Constant.KEEP_THRESHOLD)) {
//
//            }
//        }
        if(cMA5 > cMA20 && lMA5 <= lMA20) {
            status.setInterval(interval);
            // Cross to much, keep both
//            if(!Utils.TradeIntervalGreaterThan(movingAverages, Constant.MA5, Constant.MA20, Constant.SHAKE_THRESHOLD)) {
//                String trade = "ShortSelling at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Under " + interval + " minute data" + ", Current: " + status;
//                CsvWriter.writeTradeHistory("Trade_" + type, trade);
//                status.setStatus(Constant.BOTH);
//                return status;
//            }
            // Close the prior Short Selling if MA5 and MA20 cross
            if(currStatus == Constant.SHORT_SELLING) {
                Log.emptyLog(currPrice, interval, "MA cross", type);
                return Trade.empty(currPrice, status, newCachedThreadPool);
            }

            if(currStatus == Constant.EMPTY) {
                // Try to open a Put Buying if over the threshold
                return PutBuyingByThreshold(cMA5, cMA20, currPrice, newCachedThreadPool);
            }
            return status;

        } else if(cMA5 < cMA20 && lMA5 >= lMA20) {
            status.setInterval(interval);
            // Cross to much, keep both
//            if(!Utils.TradeIntervalGreaterThan(movingAverages, Constant.MA5, Constant.MA20,  Constant.SHAKE_THRESHOLD)) {
//                String trade = "PutBuying at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Under " + interval + " minute data" + ", Current: " + status;
//                CsvWriter.writeTradeHistory("Trade_" + type, trade);
//                status.setStatus(Constant.BOTH);
//                return status;
//            }
            // Close the prior Put Buying if MA5 and MA20 cross
            if(currStatus == Constant.PUT_BUYING) {
                Log.emptyLog(currPrice, interval, "MA cross", type);
                return Trade.empty(currPrice, status, newCachedThreadPool);
            }

            if(currStatus == Constant.EMPTY) {
                // Try to open a Short Selling if over the threshold
                return ShortSellingByThreshold(cMA5, cMA20, currPrice, newCachedThreadPool);
            }
            return status;
        }

        // last trade is a fast trade, skip until MA cross
        else if (status.getInterval() != interval) {
            return status;
        }

        else {
            if(cMA5 > cMA20 && lMA5 > lMA20 && currStatus == Constant.EMPTY) {
                return PutBuyingByThreshold(cMA5, cMA20, currPrice, newCachedThreadPool);
            } else if(cMA5 < cMA20 && lMA5 < lMA20 && currStatus == Constant.EMPTY) {
                return ShortSellingByThreshold(cMA5, cMA20, currPrice, newCachedThreadPool);
            }
        }
        return status;
    }

    private Status PutBuyingByThreshold(double cMA5, double cMA20, PriceData currPrice, ExecutorService newCachedThreadPool) {
        if(Math.abs(cMA5 - cMA20) >= Constant.TRADE_THRESHOLD) {
            Log.putBuyingLog(currPrice, interval, "empty", type);
            return Trade.putBuying(currPrice, status, newCachedThreadPool);
        }
        return status;
    }

    private Status ShortSellingByThreshold(double cMA5, double cMA20, PriceData currPrice, ExecutorService newCachedThreadPool) {
        if(Math.abs(cMA5 - cMA20) >= Constant.TRADE_THRESHOLD) {
            Log.shortSellingLog(currPrice, interval, "empty", type);
            return Trade.shortSelling(currPrice, status, newCachedThreadPool);
        }
        return status;
    }
}
