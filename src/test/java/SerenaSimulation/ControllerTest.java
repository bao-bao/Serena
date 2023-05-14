package SerenaSimulation;

import SerenaSimulation.strategy.FindProfitMaxPercent;
import SerenaSimulation.strategy.FindProfitMaxPercentReverse;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.ErrorType;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.TradeUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.Status;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class ControllerTest implements Runnable {
    private boolean signal;
    private final String type;
    private final DataServiceManagerTest dataSvcMgr;
    private final StrategyManagerTest strategyMgr;
    private static ControllerTest controller;
    public String filename;

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

    public static ControllerTest getInstance() {
        return controller;
    }

    public static void stop() {
        controller.dataSvcMgr.removeAll();
        controller.strategyMgr.removeAll();
        Status.reset();
        controller = null;
    }

    private void init() {
        if(Status.getInstance().getStatus() != TradingType.EMPTY) {
            controller.strategyMgr.changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            controller.strategyMgr.changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }
    }

    @Override
    public void run() {
        controller.addDataTrack(IntervalEnum.MIN_1);
        init();
        while (signal) {
            synchronized (decisionQueue) {
                while(decisionQueue.isEmpty() && signal) {
                    try {
                        decisionQueue.notify();
                        decisionQueue.wait();
                    } catch (InterruptedException ignored) {}
                }
                Decision decision = decisionQueue.poll();
                if(decision != null) {
                    //LogUtil.getInstance().info("Decision in this minute: " + decision);
                    if(decision.isExecute()) {
                        //LogUtil.getInstance().info("Perform trade...");
                        if((decision.getTradingType() == TradingType.PUT_BUYING && Status.getInstance().getStatus() == TradingType.SHORT_SELLING) ||
                                (decision.getTradingType() == TradingType.SHORT_SELLING && Status.getInstance().getStatus() == TradingType.PUT_BUYING)) {
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
        }
        if(this.strategyMgr.containsStrategy(StrategyEnum.STRATEGY_FIND_MAX_PERCENT)) {
            ArrayList<Double> data = ((FindProfitMaxPercent)(this.strategyMgr.strategyList.get(StrategyEnum.STRATEGY_FIND_MAX_PERCENT))).getRes();
            WriteFindPercent(filename, data);
        }
        if(this.strategyMgr.containsStrategy(StrategyEnum.STRATEGY_FIND_MAX_PERCENT_REVERSE)) {
            ArrayList<Double> data = ((FindProfitMaxPercentReverse)(this.strategyMgr.strategyList.get(StrategyEnum.STRATEGY_FIND_MAX_PERCENT_REVERSE))).getRes();
            WriteFindPercent(filename, data);
        }
    }

    public static void WriteFindPercent(String filename, ArrayList<Double> data) {
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.append(data.toString()).append("\n");
        } catch (FileNotFoundException e) {
            FileUtil.newFile(filename);
            WriteFindPercent(filename, data);
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename);
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
    }

    public static ArrayBlockingQueue<Decision> getDecisionQueue() {
        return decisionQueue;
    }

    public void addDataTrack(IntervalEnum interval) {
        dataSvcMgr.addDataTrackThread(interval);
        //LogUtil.getInstance().info("Successful add data service for " + interval + " min(s) interval");
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

    public String getType() {
        return type;
    }
}
