package SerenaSimulation;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.TradeUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.Status;

import java.util.concurrent.ArrayBlockingQueue;

public class ControllerTest implements Runnable {
    private boolean signal;
    private final String type;
    private final DataServiceManagerTest dataSvcMgr;
    private final StrategyManagerTest strategyMgr;
    private static ControllerTest controller;

    private static final ArrayBlockingQueue<Decision> decisionQueue = new ArrayBlockingQueue<>(Setting.MAX_DECISION_QUEUE_SIZE);


    private ControllerTest(String type) {
        this.signal = true;
        this.type = type;
        this.dataSvcMgr = DataServiceManagerTest.getInstance(type);
        this.strategyMgr = StrategyManagerTest.getInstance();
        FileUtil.readTradeHistory("Trade_" + type);
    }

    public static ControllerTest getInstance(String type) {
        if(controller == null) {
            controller = new ControllerTest(type);
        }
        return controller;
    }

    public static void stop() {
        controller.dataSvcMgr.removeAll();
        controller.strategyMgr.removeAll();
        Status.reset();
        controller = null;
    }

    @Override
    public void run() {
        controller.addDataTrack(IntervalEnum.MIN_1);
        while (signal) {
            synchronized (decisionQueue) {
                while(decisionQueue.isEmpty() && signal) {
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
    }

    public void setSignal(boolean signal) {
        this.signal = signal;
    }

    public boolean getSignal() {
        return signal;
    }
}
