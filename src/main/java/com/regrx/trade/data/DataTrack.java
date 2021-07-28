package com.regrx.trade.data;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.file.CsvReader;
import com.regrx.trade.network.HistoryDataDownloader;
import com.regrx.trade.network.PriceDataDownloader;
import com.regrx.trade.statistic.MovingAverage;
import com.regrx.trade.strategy.MA5MA20;
import com.regrx.trade.util.Time;
import com.regrx.trade.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.lang.Thread.sleep;

public class DataTrack {
    public int interval;
    public String type;
    public MinutesData minutesData;
    public MinutesData everyMinuteData;
    public Status status;
    public int breed;
    public boolean tradeIntervalLock;
    public int fastTradeCount;

    public DataTrack(String type, int interval) {
        this.type = type;
        this.interval = interval;
        minutesData = new MinutesData(interval);
        status = new Status();
        breed = Utils.getBreed(type);
        tradeIntervalLock = false;
        fastTradeCount = Constant.START_FAST_TRADE;
    }

    public void track() {
        System.out.println("Start tracking " + type + " for an interval of " + interval + " minute(s)");

        String url = "https://hq.sinajs.cn/list=nf_" + type;

        System.out.println("Start fetching " + interval + " minute data...");
        minutesData = HistoryDataDownloader.getHistoryData(type, interval, breed);
        status = CsvReader.readTradeHistory("Trade_" + type);

        if(interval != Constant.MIN_1) {
            System.out.println("Start fetching 1 minute data...");
            everyMinuteData = HistoryDataDownloader.getHistoryData(type, Constant.MIN_1, breed);

            // if last record is using 1 min data and is not empty, then the lock should be true.
            if(status.getInterval() == Constant.MIN_1 && status.getStatus() != Constant.EMPTY) {
                tradeIntervalLock = true;
                fastTradeCount += 1;
            }
            System.out.println("Fast trade remaining: " + fastTradeCount + " time(s)\n");
        }

        while(true) {
            if(Utils.isTrading(breed)) {
                // update price data every minute
                long current = System.currentTimeMillis();
                Date currentDate = new Date(System.currentTimeMillis());
                long nextPoint = Time.getNextMillisEveryNMinutes(currentDate, Constant.MIN_1);
                try {
                    sleep(nextPoint - current);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PriceData newPrice;
                if(breed == Constant.STOCK) {
                    newPrice = PriceDataDownloader.getPriceDataForStockFutures(url);
                } else {
                    newPrice = PriceDataDownloader.getPriceDataForOtherFutures(url);
                }

                if(interval != Constant.MIN_1) {
                    everyMinuteData.update(newPrice, type, true);

                    // time instance of last fetch
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                    calendar.setTime(everyMinuteData.getCurrentTime());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // special trade at the beginning using 1 min data

                    // has remaining time || has lock && none other interval trade exists
                    if((fastTradeCount > 0 || tradeIntervalLock) &&
                            !(status.getStatus() != Constant.EMPTY && status.getInterval() != Constant.MIN_1)) {
                        status.setInterval(Constant.MIN_1);
                        boolean success = this.trade(everyMinuteData.getMovingAverages(), status, Constant.MIN_1, url);

                        // if success traded, change lock status
                        if(success) {
                            tradeIntervalLock = !tradeIntervalLock;
                            fastTradeCount--;
                            System.out.println("Fast trade remaining: " + fastTradeCount + " time(s)\n");

                            // no lock(under empty status), try trade again
                            if(!tradeIntervalLock) {

                                // continue fast trade
                                if(fastTradeCount > 0) {
                                    try {
                                        sleep(Constant.FOLLOW_TIME * 15 * 1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    boolean nestSuccess = this.trade(everyMinuteData.getMovingAverages(), status, Constant.MIN_1, url);
                                    if (nestSuccess) {
                                        tradeIntervalLock = !tradeIntervalLock;
                                        fastTradeCount--;
                                        System.out.println("Fast trade remaining: " + fastTradeCount + " time(s)\n");
                                    }
                                }
                                // normal trade
                                else {
                                    status.setInterval(interval);
                                    this.trade(minutesData.getMovingAverages(), status, interval, url);
                                }
                            }
                        }
                    }

                    // normal trade if the minute matches the interval
                    if(minute % interval == 0) {
                        minutesData.update(newPrice, type, true);
                        if((fastTradeCount == 0 || !tradeIntervalLock)) {
                            boolean success = this.trade(minutesData.getMovingAverages(), status, interval, url);
                            if(success && status.getStatus() == Constant.EMPTY) {
                                this.trade(minutesData.getMovingAverages(), status, interval, url);
                            }
                        }
                    }
                } else {
                    minutesData.update(newPrice, type, true);
                    boolean success = this.trade(minutesData.getMovingAverages(), status, interval, url);
                    if(success && status.getStatus() == Constant.EMPTY) {
                        this.trade(minutesData.getMovingAverages(), status, interval, url);
                    }
                }

            } else {
                try {
                    sleep(30000);
                    Date currentDate = new Date(System.currentTimeMillis());
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                    calendar.setTime(currentDate);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    // reset fast trade time at 3:00 and 16:00
                    if((hour == 3 || hour == 16) && minute == 0) {
                        fastTradeCount = Constant.START_FAST_TRADE;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean trade(LinkedList<MovingAverage> ma, Status status, int interval, String url) {
        int before = status.getStatus();
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        Future<Status> future = newCachedThreadPool.submit(new MA5MA20(ma, status, url, interval, breed));
        try {
            System.out.println("Trade Status: " + future.get() + "\n");
            int after = future.get().getStatus();
            return before != after;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            newCachedThreadPool.shutdown();
        }
        System.out.println("Error when try to trade");
        return false;
    }
}
