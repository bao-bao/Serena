package com.regrx.serena.controller;

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
        FileUtil.readTradeHistory("Trade_" + type);
        this.dataSvcMgr = DataServiceManager.getInstance(type);
        this.strategyMgr = StrategyManager.getInstance();
    }

    public static Controller getInstance(String type) {
        if(controller == null) {
            controller = new Controller(type);
        }
        return controller;
    }

    public static Controller getInstance() {
        return controller;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (decisionQueue) {
                while(decisionQueue.isEmpty()) {
                    try {
                        decisionQueue.notify();
                        LogUtil.getInstance().info("waiting for next decision...");
                        decisionQueue.wait();
                    } catch (InterruptedException ignored) {}
                }
                Decision decision = decisionQueue.poll();
                if(decision != null) {
                    LogUtil.getInstance().info("Decision in this minute: " + decision);
                    if(decision.isExecute()) {
                        LogUtil.getInstance().info("Perform trade...");
                        TradeUtil.trade(decision);
                        LogUtil.tradeLog(type, decision);
                    }
                }
            }
        }
    }

    public static ArrayBlockingQueue<Decision> getDecisionQueue() {
        return decisionQueue;
    }

    public void addDataTrack(IntervalEnum interval) {
        dataSvcMgr.addDataTrackThread(interval);
    }


    public void addStrategy(StrategyEnum strategy, IntervalEnum interval) {
        strategyMgr.addStrategy(strategy, interval);
    }

    public String getType() {
        return type;
    }
}
