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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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

        FutureType breed = PreparationUtil.getBreed(type);
        if (interval == IntervalEnum.MIN_2 || interval == IntervalEnum.MIN_3) {
            minutesData = HistoryDownloader.getHistoryDataForSpecialInterval(type, interval, breed);
        } else {
            minutesData = HistoryDownloader.getHistoryData(type, interval, breed);
            HistoryDownloader historyDownloader = new HistoryDownloader(type, interval, PreparationUtil.getBreed(type));
            Thread thread = new Thread(historyDownloader);
            thread.start();
        }
    }

    @Override
    public void run() {
        FutureType breed = PreparationUtil.getBreed(type);
        LogUtil.getInstance().info(type + ": Start fetching " + interval.getValue() + " minute(s) data...");
        // https://stock2.finance.sina.com.cn/futures/api/jsonp.php/var=/InnerFuturesNewService.getFewMinLine?symbol=RB0&type=15
        String url = "https://hq.sinajs.cn/list=nf_" + type;

        while (true) {
            if (!PreparationUtil.isTrading(breed)) {
                try {
                    Thread.sleep(30000);
                    // try shutdown after normal daily trade
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    if (breed == FutureType.STOCK && (hour == 15 && minute > 5 && minute < 10)) {
                        try {
                            Runtime.getRuntime().exec("shutdown /s /t 0");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
                if (breed == FutureType.STOCK) {
                    newPrice = PriceDownloader.getPriceDataForStockFutures(url, type);
                } else {
                    newPrice = PriceDownloader.getPriceDataForOtherFutures(url, type);
                }

                if (newPrice == null) {
                    LogUtil.getInstance().severe("Download error! Restarting track thread...");
                    DataServiceManager manager = DataServiceManager.getInstance(type);
                    manager.removeDataTrackThread(interval);
                    manager.addDataTrackThread(interval);
                    return;
                }

                minutesData.update(newPrice, type);
                lock.lockOff();
                callback(newPrice);
            }
        }
    }

    private void callback(ExPrice newPrice) {
        if (interval != DataServiceManager.getInstance().getMinimumInterval()) {
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
