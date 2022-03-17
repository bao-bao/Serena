package com.regrx.serena.common.controller;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.TradeUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.service.DataServiceManager;
import com.regrx.serena.service.StrategyManager;

import java.util.concurrent.ArrayBlockingQueue;

public class Controller implements Runnable {
    private final String type;
    private final DataServiceManager dataSvcMgr;
    private final StrategyManager strategyMgr;
    private static Controller controller;

    private static final ArrayBlockingQueue<Decision> decisionQueue = new ArrayBlockingQueue<>(Setting.MAX_DECISION_QUEUE_SIZE);

    private Controller(String type) {
        this.type = type;
        this.dataSvcMgr = DataServiceManager.getInstance(type);
        this.dataSvcMgr.addDataTrackThread(IntervalEnum.MIN_1);
        this.strategyMgr = StrategyManager.getInstance();
        FileUtil.readTradeHistory("Trade_" + type);
    }

    public static Controller getInstance(String type) {
        if(controller == null) {
            controller = new Controller(type);
        }
        return controller;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (decisionQueue) {
                while(decisionQueue.isEmpty()) {
                    try {
                        decisionQueue.notify();
                        decisionQueue.wait();
                    } catch (InterruptedException ignored) {}
                }
                Decision decision = decisionQueue.poll();
                if(decision != null && decision.isExecute()) {
                    LogUtil.getInstance().info("Perform trade based on decision: " + decision);
                    TradeUtil.trade(decision);
                    LogUtil.tradeLog(type, decision);
                }
            }
        }
    }

    public static ArrayBlockingQueue<Decision> getDecisionQueue() {
        return decisionQueue;
    }

    public void addDataTrack(IntervalEnum interval) {
        dataSvcMgr.addDataTrackThread(interval);
        LogUtil.getInstance().info("Successful add data service for " + interval + " min(s) interval");
    }

    public void addStrategy(StrategyEnum strategy, IntervalEnum interval) {
        strategyMgr.addStrategy(strategy, interval);
        LogUtil.getInstance().info("Successful add strategy " + strategy);
    }
}
