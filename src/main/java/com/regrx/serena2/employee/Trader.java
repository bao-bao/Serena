package com.regrx.serena2.employee;

import com.regrx.serena2.common.constant.TradingType;
import com.regrx.serena2.common.utils.LogUtil;
import com.regrx.serena2.common.utils.TradeUtil;
import com.regrx.serena2.common.Setting;
import com.regrx.serena2.common.constant.IntervalEnum;
import com.regrx.serena2.common.constant.StrategyEnum;
import com.regrx.serena2.data.base.Decision;
import com.regrx.serena2.data.base.Status;
import com.regrx.serena2.service.DataServiceManager;
import com.regrx.serena2.service.StrategyManager;

import java.util.concurrent.ArrayBlockingQueue;

public class Trader implements Runnable {
    private final String type;
    private final DataServiceManager dataSvcMgr;
    private final StrategyManager strategyMgr;
    private final Status status;

    private static final ArrayBlockingQueue<Decision> decisionQueue = new ArrayBlockingQueue<>(Setting.MAX_DECISION_QUEUE_SIZE);

    public Trader(String type) {
        this.type = type;
        this.dataSvcMgr = new DataServiceManager(type);
        this.strategyMgr = new StrategyManager();
        this.status = new Status();
    }

    private void init() {

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
                if(decision == null) {
                    continue;
                }
                LogUtil.getInstance().info("Decision in this minute: " + decision);
                if(!decision.isExecute()) {
                    continue;
                }
                LogUtil.getInstance().info("Perform trade... Decision: " + decision.getTradingType() + ", Current: " + status.getStatus());
                if((decision.getTradingType() == TradingType.PUT_BUYING && status.getStatus() == TradingType.SHORT_SELLING) ||
                        (decision.getTradingType() == TradingType.SHORT_SELLING && status.getStatus() == TradingType.PUT_BUYING)) {
                    Decision emptyDecision = new Decision();
                    emptyDecision.copy(decision);
                    emptyDecision.setTradingType(TradingType.EMPTY);
                    LogUtil.tradeLog(type, emptyDecision);
                }
                LogUtil.tradeLog(type, decision);
                TradeUtil.trade(decision, type);
            }
        }
    }


    public static ArrayBlockingQueue<Decision> getDecisionQueue() {
        return decisionQueue;
    }

    public Trader trackPriceSeries(IntervalEnum interval) {
        dataSvcMgr.addDataTrackThread(interval);
        return this;
    }


    public void useStrategy(StrategyEnum strategy, IntervalEnum interval) {
        strategyMgr.addStrategy(strategy, interval);
    }

    public void leave() {
        this.strategyMgr.removeAll();
        this.dataSvcMgr.removeAll();
    }

    public String getType() {
        return type;
    }
}
