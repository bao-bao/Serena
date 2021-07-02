package com.regrx.trade.strategy;


import com.regrx.trade.constant.Constant;
import com.regrx.trade.control.KeySprite;
import com.regrx.trade.data.PriceData;
import com.regrx.trade.data.Status;
import com.regrx.trade.file.CsvWriter;
import com.regrx.trade.network.PriceDataDownloader;
import com.regrx.trade.statistic.MovingAverage;
import com.regrx.trade.util.Utils;

import java.util.LinkedList;
import java.util.concurrent.Callable;

public class MA5MA20 implements Callable<Status> {
    private final LinkedList<MovingAverage> movingAverages;
    private final Status status;
    private final String url;
    private final int interval;

    public MA5MA20(LinkedList<MovingAverage> ma, Status status, String url, int interval) {
        this.movingAverages = ma;
        this.status = status;
        this.url = url;
        this.interval = interval;
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
        int currStatus = this.status.getStatus();
        System.out.println("" + cMA5 + " " + cMA20 + " " + lMA5 + " " + lMA20 + "\n");
        PriceData currPrice = PriceDataDownloader.getPriceData(url);
//
//        if(currStatus == Constant.BOTH) {
//            if(Utils.LastCrossDiffGreaterThan(movingAverages, Constant.MA5, Constant.MA20, Constant.KEEP_THRESHOLD)) {
//
//            }
//        }
        if(cMA5 > cMA20 && lMA5 < lMA20) {
            // Cross to much, keep both
//            if(!Utils.TradeIntervalGreaterThan(movingAverages, Constant.MA5, Constant.MA20, Constant.SHAKE_THRESHOLD)) {
//                String trade = "ShortSelling at " + currPrice.getDate() + " for " + currPrice.getPrice();
//                CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
//                status.setStatus(Constant.BOTH);
//                return status;
//            }
            // Close the prior Short Selling if MA5 and MA20 cross
            if(currStatus == Constant.SHORT_SELLING) {
                status.setStatus(Constant.EMPTY);
                KeySprite.Empty();
                String trade = "Close at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Current: " + status;
                CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
            }
            // Try to open a Put Buying if over the threshold
            PutBuyingByThreshold(cMA5, cMA20, currPrice);
//            PutBuyingByMA60(Utils.EvalLastCrossPrice(movingAverages, Constant.MA5, Constant.MA20), currentMA.getMA60(), currPrice);
            return status;

        } else if(cMA5 < cMA20 && lMA5 > lMA20) {
            // Cross to much, keep both
//            if(!Utils.TradeIntervalGreaterThan(movingAverages, Constant.MA5, Constant.MA20,  Constant.SHAKE_THRESHOLD)) {
//                String trade = "PutBuying at " + currPrice.getDate() + " for " + currPrice.getPrice();
//                CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
//                status.setStatus(Constant.BOTH);
//                return status;
//            }
            // Close the prior Put Buying if MA5 and MA20 cross
            if(currStatus == Constant.PUT_BUYING) {
                status.setStatus(Constant.EMPTY);
                KeySprite.Empty();
                String trade = "Close at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Current: " + status;
                CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
            }
            // Try to open a Short Selling if over the threshold
            ShortSellingByThreshold(cMA5, cMA20, currPrice);
//            ShortSellingByMA60(Utils.EvalLastCrossPrice(movingAverages, Constant.MA5, Constant.MA20), currentMA.getMA60(), currPrice);
            return status;
        }
        else if(cMA5 > cMA20 && lMA5 > lMA20 && currStatus == Constant.EMPTY) {
            if (PutBuyingByThreshold(cMA5, cMA20, currPrice)) return status;
        } else if(cMA5 < cMA20 && lMA5 < lMA20 && currStatus == Constant.EMPTY) {
            if (ShortSellingByThreshold(cMA5, cMA20, currPrice)) return status;
        }
        return status;
    }

    private boolean PutBuyingByThreshold(double cMA5, double cMA20, PriceData currPrice) {
        if(Math.abs(cMA5 - cMA20) >= Constant.TRADE_THRESHOLD) {
            status.setStatus(Constant.PUT_BUYING);
            KeySprite.PutBuying();
            String trade = "PutBuying at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Current: " + status;
            CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
            return true;
        }
        return false;
    }

    private boolean ShortSellingByThreshold(double cMA5, double cMA20, PriceData currPrice) {
        if(Math.abs(cMA5 - cMA20) >= Constant.TRADE_THRESHOLD) {
            status.setStatus(Constant.SHORT_SELLING);
            KeySprite.ShortSelling();
            String trade = "ShortSelling at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Current: " + status;
            CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
            return true;
        }
        return false;
    }

    private boolean PutBuyingByMA60(double crossPoint, double MA60, PriceData currPrice) {
        if(crossPoint > MA60) {
            status.setStatus(Constant.PUT_BUYING);
            KeySprite.PutBuying();
            String trade = "PutBuying at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Current: " + status;
            CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
            return true;
        }
        return false;
    }

    private boolean ShortSellingByMA60(double crossPoint, double MA60, PriceData currPrice) {
        if(crossPoint < MA60) {
            status.setStatus(Constant.SHORT_SELLING);
            KeySprite.ShortSelling();
            String trade = "ShortSelling at " + currPrice.getDate() + " for " + currPrice.getPrice() + ", Current: " + status;
            CsvWriter.writeTradeHistory("520Trade_" + interval, trade);
            return true;
        }
        return false;
    }
}
