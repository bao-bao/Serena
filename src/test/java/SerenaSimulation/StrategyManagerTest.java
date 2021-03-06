package SerenaSimulation;

import SerenaSimulation.strategy.*;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StrategyManagerTest {
    private final HashMap<StrategyEnum, AbstractStrategy> strategyList;
    private final HashMap<StrategyEnum, ForceTriggerStrategy> forceTriggerStrategyList;
    private static StrategyManagerTest strategyMgr;

    private StrategyManagerTest() {
        this.strategyList = new HashMap<>();
        this.forceTriggerStrategyList = new HashMap<>();
    }

    public static StrategyManagerTest getInstance() {
        if(strategyMgr == null) {
            strategyMgr = new StrategyManagerTest();
        }
        return strategyMgr;
    }

    public boolean containsStrategy(StrategyEnum strategy) {
        return strategyList.containsKey(strategy) || forceTriggerStrategyList.containsKey(strategy);
    }

    public boolean addStrategy(StrategyEnum strategy, IntervalEnum interval) {
        if (strategyList.containsKey(strategy) || forceTriggerStrategyList.containsKey(strategy)) {
            LogUtil.getInstance().info("Fail to add strategy " + strategy + ", already contain");
            return false;
        }
        switch (strategy) {
            case STRATEGY_MA_520:
                strategyList.put(strategy, new MA520(interval));
                break;
            case STRATEGY_LOSS_LIMIT:
                strategyList.put(strategy, new LossLimit(interval));
                break;
            case STRATEGY_PROFIT_LIMIT:
                strategyList.put(strategy, new ProfitLimit(interval));
                break;
            case STRATEGY_CLOSE_ON_END:
                forceTriggerStrategyList.put(strategy, new CloseOnEnd());
                break;
            case STRATEGY_REOPEN:
                forceTriggerStrategyList.put(strategy, new Reopen());
                break;
            default:
                LogUtil.getInstance().info("Fail to add strategy " + strategy + ", unknown strategy");
                return false;
        }
        LogUtil.getInstance().info("Successful add strategy " + strategy);
        return true;
    }

    public boolean addStrategy(StrategyEnum strategy, ForceTriggerStrategy newStrategy) {
        if (forceTriggerStrategyList.containsKey(strategy)) {
            LogUtil.getInstance().info("Fail to add strategy " + strategy + ", already contain");
            return false;
        }
        if (newStrategy == null) {
            return false;
        }
        forceTriggerStrategyList.put(strategy, newStrategy);
        LogUtil.getInstance().info("Successful add strategy " + strategy);
        return true;
    }

    public void removeStrategy(StrategyEnum strategy) {
        strategyList.remove(strategy);
        forceTriggerStrategyList.remove(strategy);
    }

    public void removeAll() {
        strategyList.clear();
        forceTriggerStrategyList.clear();
    }

    public void changePriority(StrategyEnum strategy, int priority) {
        if (strategyList.containsKey(strategy)) {
            AbstractStrategy abStrategy = strategyList.get(strategy);
            abStrategy.setPriority(priority);
            strategyList.put(strategy, abStrategy);
        }
        if (forceTriggerStrategyList.containsKey(strategy)) {
            ForceTriggerStrategy abStrategy = forceTriggerStrategyList.get(strategy);
            abStrategy.setPriority(priority);
            forceTriggerStrategyList.put(strategy, abStrategy);
        }
    }

    public Decision execute(ExPrice newPrice) {
        Calendar currTime = Calendar.getInstance();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Setting.TIME_PATTERN);
            currTime.setTime(dateFormat.parse(newPrice.getTime()));
        } catch (ParseException ignored) {}
        int currMinute = currTime.get(Calendar.MINUTE);
        int currHour = currTime.get(Calendar.HOUR_OF_DAY);

        PriorityQueue<AbstractStrategy> strategyQueue = new PriorityQueue<>();
        if(!((currHour == 14 && currMinute > 56) || (currHour == 15 && currMinute == 0) || (currHour == 11 && currMinute == 30))) {
            for (AbstractStrategy strategy : strategyList.values()) {
                if (Status.getInstance().isTrading()
                        && strategy.getPriority() < Setting.BLOCK_LOW_PRIORITY_STRATEGY
                        && currMinute % strategy.getInterval().getValue() == 0) {
                    strategyQueue.add(strategy);
                }
            }
        }

        for (ForceTriggerStrategy strategy : forceTriggerStrategyList.values()) {
            if (strategy.isTriggered(currHour, currMinute)) {
                strategyQueue.add(strategy);
            }
        }

        LogUtil.getInstance().info(strategyQueue.size() + " strategies will be in use at " + newPrice.getTime() + " : " + strategyQueueString(strategyQueue));
        Decision decision = new Decision();
        while(!strategyQueue.isEmpty()) {
            decision = strategyQueue.poll().execute(newPrice);
            if(decision.isExecute()) {
                break;
            }
        }
        return decision;
    }

    public String strategyQueueString(PriorityQueue<AbstractStrategy> queue) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Iterator<AbstractStrategy> it = queue.iterator(); it.hasNext(); ) {
            AbstractStrategy strategy = it.next();
            sb.append(strategy.getName());
            if(it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

}
