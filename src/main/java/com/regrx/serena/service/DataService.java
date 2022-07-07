package com.regrx.serena.service;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.FutureType;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.common.network.HistoryDownloader;
import com.regrx.serena.common.network.PriceDownloader;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.PreparationUtil;
import com.regrx.serena.common.utils.TimeUtil;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

public class DataService implements Runnable {
    private final String type;
    private final IntervalEnum interval;
    private MinutesData minutesData;

    public DataService(String type, IntervalEnum interval) {
        super();
        this.type = type;
        this.interval = interval;
        this.minutesData = new MinutesData(interval);
    }

    @Override
    public void run() {

        FutureType breed = PreparationUtil.getBreed(type);

        LogUtil.getInstance().info(type + ": Start fetching " + interval.getValue() + " minute(s) data...");
        String url = "https://hq.sinajs.cn/list=nf_" + type;
        minutesData = HistoryDownloader.getHistoryData(type, interval, breed);

        while(true) {
            if(!PreparationUtil.isTrading(breed)) {
                try{
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                // get the next fetch timing
                long current = System.currentTimeMillis();
                Date currentDate = new Date(current);
                long nextPoint = TimeUtil.getNextMillisEveryNMinutes(currentDate, interval.getValue());
                try {
                    Thread.sleep(nextPoint - current);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ExPrice newPrice;
                if(breed == FutureType.STOCK) {
                    newPrice = PriceDownloader.getPriceDataForStockFutures(url, type);
                } else {
                    newPrice = PriceDownloader.getPriceDataForOtherFutures(url, type);
                }
                minutesData.update(newPrice, type);
                callback(newPrice);
            }
        }
    }

    private void callback(ExPrice newPrice) {
        if(interval != DataServiceManager.getInstance().getMinimumInterval()) {
            return;
        }

        LogUtil.getInstance().info("Making trade decision on point " + newPrice + "...");
        Decision decision = StrategyManager.getInstance().execute(newPrice);
        LogUtil.getInstance().info("Decision making complete!");

        ArrayBlockingQueue<Decision> queue;
        synchronized (queue = Controller.getDecisionQueue()) {
            while (queue.size() == Setting.MAX_DECISION_QUEUE_SIZE) {
                try {
                    queue.notify();
                    queue.wait();
                } catch (InterruptedException ignored) {
                }
            }
            queue.add(decision);
            try {
                queue.notify();
                queue.wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public MinutesData getMinutesData() {
        return minutesData;
    }
}
