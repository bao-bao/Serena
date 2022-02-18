package com.regrx.trade.strategy;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.PriceData;
import com.regrx.trade.data.Status;
import com.regrx.trade.network.PriceDataDownloader;
import com.regrx.trade.util.Log;
import com.regrx.trade.util.Trade;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LossLimit implements Callable<Status> {
    private final Double lastMinPrice;
    private final Status status;
    private final String type;
    private final int interval;
    private final PriceData currPrice;

    public LossLimit(LinkedList<Double> prices, Status status, String type, int interval, PriceData currPrice) {
        this.lastMinPrice = prices.get(prices.size() - 2);
        this.status = status;
        this.type = type;
        this.interval = interval;
        this.currPrice = currPrice;
    }

    @Override
    public Status call() {

        int currStatus = this.status.getStatus();
        double lTP = this.status.getLastTradePrice();   // last trade price
        double currP = currPrice.getPrice();
        // Thread for key sprite
        ExecutorService newThreadPool = Executors.newCachedThreadPool();

        // current is empty, put buying or short selling based on threshold and MA trend
        if (currStatus == Constant.EMPTY) {
            if (status.getTrend() && currP - lTP > Constant.RESTORE_THRESHOLD) {
                Log.putBuyingLog(currPrice, interval, "exceed limit", type);
                return Trade.putBuying(currPrice, status, newThreadPool);
            } else if (!status.getTrend() && lTP - currP > Constant.RESTORE_THRESHOLD) {
                Log.putBuyingLog(currPrice, interval, "exceed limit", type);
                return Trade.putBuying(currPrice, status, newThreadPool);
            } else {
                return status;
            }
        }

        // current is not empty, limit the loss into a threshold
        if (currStatus == Constant.PUT_BUYING) {
            return lossLimit(currPrice, newThreadPool, currP > lTP, lastMinPrice - lTP, currP - lTP);
        } else if (currStatus == Constant.SHORT_SELLING) {
            return lossLimit(currPrice, newThreadPool, currP < lTP, lTP - lastMinPrice, lTP - currP);
        }
        return status;
    }

    private Status lossLimit(PriceData currPriceData, ExecutorService newThreadPool, boolean hasProfit, double historyProfit, double currProfit) {
        if (hasProfit && currProfit < Constant.PROFIT_LIMIT_THRESHOLD && historyProfit > Constant.PROFIT_LIMIT_THRESHOLD) {         // has profit but not much, and was much
            Log.emptyLog(currPriceData, interval, "profit limit", type);
            return Trade.empty(currPriceData, status, newThreadPool);
        } else if (!hasProfit && Math.abs(currProfit) > Constant.LOSS_LIMIT_THRESHOLD) {         // deficit much
            Log.emptyLog(currPriceData, interval, "loss limit", type);
            return Trade.empty(currPriceData, status, newThreadPool);
        }
        return status;
    }
}
