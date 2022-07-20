package com.regrx.serena.controller;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.TradeUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.service.DataServiceManager;
import com.regrx.serena.service.StrategyManager;

import java.util.Set;
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

    private void init() {
        if(Status.getInstance().getStatus() != TradingType.EMPTY) {
            strategyMgr.changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            strategyMgr.changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }
    }

    @Override
    public void run() {
        init();
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
                        if((decision.getTradingType() == TradingType.PUT_BUYING && Status.getInstance().getStatus() == TradingType.SHORT_SELLING) ||
                           (decision.getTradingType() == TradingType.SHORT_SELLING && Status.getInstance().getStatus() == TradingType.PUT_BUYING)) {
                            Decision emptyDecision = new Decision();
                            emptyDecision.copy(decision);
                            emptyDecision.setTradingType(TradingType.EMPTY);
                            LogUtil.tradeLog(type, emptyDecision);
                        }
                        LogUtil.tradeLog(type, decision);
                        TradeUtil.trade(decision);
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
