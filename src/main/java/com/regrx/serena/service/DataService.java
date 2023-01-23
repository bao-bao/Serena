package com.regrx.serena.service;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.FutureType;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.utils.SyncLock;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.common.network.HistoryDownloader;
import com.regrx.serena.common.network.PriceDownloader;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.PreparationUtil;
import com.regrx.serena.common.utils.TimeUtil;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

public class DataService implements Runnable {
    private final String type;
    private final IntervalEnum interval;
    private MinutesData minutesData;
    private final SyncLock lock;

    public DataService(String type, IntervalEnum interval) {
        super();
        this.type = type;
        this.interval = interval;
        this.minutesData = new MinutesData(interval);
        this.lock = new SyncLock();
    }

    @Override
    public void run() {

        FutureType breed = PreparationUtil.getBreed(type);
        LogUtil.getInstance().info(type + ": Start fetching " + interval.getValue() + " minute(s) data...");
        // https://stock2.finance.sina.com.cn/futures/api/jsonp.php/var=/InnerFuturesNewService.getFewMinLine?symbol=RB0&type=15
        String url = "https://hq.sinajs.cn/list=nf_" + type;

        if(interval == IntervalEnum.MIN_2 || interval == IntervalEnum.MIN_3) {
            minutesData = HistoryDownloader.getHistoryDataForSpecialInterval(type, interval, breed);
        } else {
            minutesData = HistoryDownloader.getHistoryData(type, interval, breed);
        }

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
                    Thread.sleep((nextPoint - current) / 2);
                    lock.lockOn();
                    Thread.sleep((nextPoint - current) / 2);
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
                lock.lockOff();
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

    public SyncLock getLock() {
        return lock;
    }
}
