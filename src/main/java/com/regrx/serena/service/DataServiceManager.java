package com.regrx.serena.service;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.MinutesData;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataServiceManager {
    private final String type;
    private final ExecutorService dataTrackingExecutor;
    private final HashMap<IntervalEnum, DataService> dataList;
    private final HashMap<IntervalEnum, Future<?>> dataRef;
    private static DataServiceManager dataSvcMgr;


    private DataServiceManager(String type) {
        this.type = type;
        this.dataTrackingExecutor = Executors.newCachedThreadPool();
        this.dataList = new HashMap<>();
        this.dataRef = new HashMap<>();
    }

    public static DataServiceManager getInstance(String type) {
        if(dataSvcMgr == null) {
            dataSvcMgr = new DataServiceManager(type);
        }
        return dataSvcMgr;
    }

    public static DataServiceManager getInstance() {
        return dataSvcMgr;
    }

    public void addDataTrackThread(IntervalEnum interval) {
        if(dataRef.containsKey(interval)) {
            LogUtil.getInstance().warning("Already tracking on " + type + " for " + interval.getValue() + "minute(s) interval");
            return;
        }
        DataService dataService = new DataService(type, interval);
        dataList.put(interval, dataService);
        Future<?> future = this.dataTrackingExecutor.submit(dataService);
        dataRef.put(interval, future);
        LogUtil.getInstance().info("Successful add data service for " + interval + " min(s) interval");
    }

    public void removeDataTrackThread(IntervalEnum interval) {
        if(!dataRef.containsKey(interval)) {
            LogUtil.getInstance().warning("No tracking for " + interval.getValue() + "minute(s) interval");
            return;
        }
        Future<?> future = dataRef.get(interval);
        future.cancel(true);
        dataRef.remove(interval);
        dataList.remove(interval);
        LogUtil.getInstance().info("Successful remove data service for " + interval + " min(s) interval");
    }

    public void removeAll() {
        for(Future<?> f : dataRef.values()) {
            f.cancel(true);
        }
        dataRef.clear();
        dataList.clear();
        LogUtil.getInstance().info("Successful remove all data services");
    }

    public MinutesData queryData(IntervalEnum interval) {
        return dataList.get(interval).getMinutesData();
    }

    public IntervalEnum getMinimumInterval() {
        if(dataList.isEmpty()) {
            return IntervalEnum.NULL;
        } else {
            return dataList.keySet().stream().reduce((x, y) -> x.compareTo(y) < 0 ? x : y).get();
        }
    }

    public boolean isSyncLocked() {
        for(DataService service : dataList.values()) {
            if(service.getLock().isLocked()) {
                return true;
            }
        }
        return false;
    }
}
