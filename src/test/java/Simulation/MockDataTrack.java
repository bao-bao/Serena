package Simulation;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.MinutesData;
import com.regrx.trade.data.PriceData;
import com.regrx.trade.data.Status;
import com.regrx.trade.file.CsvReader;
import com.regrx.trade.statistic.MovingAverage;
import com.regrx.trade.strategy.LossLimit;
import com.regrx.trade.strategy.MA5MA20;
import com.regrx.trade.util.Utils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MockDataTrack {
    public int interval;
    public String type;
    public MinutesData minutesData;
    public MinutesData everyMinuteData;
    public Status status;
    public int breed;
    public boolean tradeIntervalLock;
    public int fastTradeCount;
    String mockDataFile;
    public LinkedList<PriceData> priceData;
    int next;

    public MockDataTrack(String type, int interval) {
        this.type = type;
        this.interval = interval;
        this.mockDataFile = type;
        minutesData = new MinutesData(interval);
        status = new Status();
        breed = Utils.getBreed(type);
        tradeIntervalLock = false;
        fastTradeCount = Constant.START_FAST_TRADE;
        priceData = Util.CsvReader.readPriceFromCsv(mockDataFile);
        next = 20 * interval;
    }

    public void track() {
        System.out.println("Start tracking " + type + " for an interval of " + interval + " minute(s)");

        System.out.println("Start fetching " + interval + " minute data...");
        minutesData = MockHistoryDataDownloader.getHistoryData(mockDataFile, interval, 20);
        status = CsvReader.readTradeHistory("Trade_" + type);
        status.updateTrend(minutesData.getLastMovingAverage());

        if(interval != Constant.MIN_1) {
            System.out.println("Start fetching 1 minute data...");
            everyMinuteData = MockHistoryDataDownloader.getHistoryData(mockDataFile, interval, next);

            // if last record is using 1 min data and is not empty, then the lock should be true.
            if(status.getInterval() == Constant.MIN_1 && status.getStatus() != Constant.EMPTY) {
                tradeIntervalLock = true;
                fastTradeCount += 1;
            }
            System.out.println("Fast trade remaining: " + fastTradeCount + " time(s)\n");
        }

        while(next < priceData.size()) {
                // update price data every minute
                PriceData newPrice = priceData.get(next++);
                everyMinuteData.update(newPrice, type, true);

                // loss limit first
                if(lossLimit(everyMinuteData.getPrices(), status, interval, newPrice)) {
                    continue;
                }

                if(interval != Constant.MIN_1) {

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
                        boolean success = this.trade(everyMinuteData.getMovingAverages(), status, Constant.MIN_1, newPrice);

                        // if success traded, change lock status
                        if(success) {
                            tradeIntervalLock = !tradeIntervalLock;
                            fastTradeCount--;
                            System.out.println("Fast trade remaining: " + fastTradeCount + " time(s)\n");

                            // no lock(under empty status), try trade again
                            if(!tradeIntervalLock) {
                                // continue fast trade
                                if(fastTradeCount > 0) {
                                    boolean nestSuccess = this.trade(everyMinuteData.getMovingAverages(), status, Constant.MIN_1, newPrice);
                                    if (nestSuccess) {
                                        tradeIntervalLock = !tradeIntervalLock;
                                        fastTradeCount--;
                                        System.out.println("Fast trade remaining: " + fastTradeCount + " time(s)\n");
                                    }
                                }
                                // normal trade
                                else {
                                    status.setInterval(interval);
                                    this.trade(minutesData.getMovingAverages(), status, interval, newPrice);
                                }
                            }
                        }
                    }

                    // normal trade if the minute matches the interval
                    if(minute % interval == 0) {
                        minutesData.update(newPrice, type, true);
                        status.updateTrend(minutesData.getLastMovingAverage());
                        if(fastTradeCount != 0) {
                            System.out.println("Fast trade remaining: " + fastTradeCount + " time(s)\n");
                        }

                        if((fastTradeCount == 0 || !tradeIntervalLock)) {
                            boolean success = this.trade(minutesData.getMovingAverages(), status, interval, newPrice);
                            if(success && status.getStatus() == Constant.EMPTY) {
                                this.trade(minutesData.getMovingAverages(), status, interval, newPrice);
                            }
                        }
                    }
                } else {
                    minutesData.update(newPrice, type, true);
                    status.updateTrend(minutesData.getLastMovingAverage());
                    boolean success = this.trade(minutesData.getMovingAverages(), status, interval, newPrice);
                    if(success && status.getStatus() == Constant.EMPTY) {
                        this.trade(minutesData.getMovingAverages(), status, interval, newPrice);
                    }
                }
        }
    }

    private boolean trade(LinkedList<MovingAverage> ma, Status status, int interval, PriceData newPrice) {
        int before = status.getStatus();
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        Future<Status> future = newCachedThreadPool.submit(new MA5MA20(ma, status, type, interval, breed, newPrice));
        return execute(before, newCachedThreadPool, future);
    }

    private boolean lossLimit(LinkedList<Double> prices, Status status, int interval, PriceData newPrice) {
        int before = status.getStatus();
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        Future<Status> future = newCachedThreadPool.submit(new LossLimit(prices, status, type, interval, newPrice));
        return execute(before, newCachedThreadPool, future);
    }

    private boolean execute(int before, ExecutorService newCachedThreadPool, Future<Status> future) {
        try {
//            System.out.println("Trade Status: " + future.get() + "\n");
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
