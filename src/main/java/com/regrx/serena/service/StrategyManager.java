package com.regrx.serena.service;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.strategy.*;

import java.util.*;

public class StrategyManager {
    private final HashMap<StrategyEnum, AbstractStrategy> strategyList;
    private final HashMap<StrategyEnum, ForceTriggerStrategy> forceTriggerStrategyList;
    private final ArrayList<AfterCheckStrategy> afterCheckStrategyList;
    private static StrategyManager strategyMgr;

    private StrategyManager() {
        this.strategyList = new HashMap<>();
        this.forceTriggerStrategyList = new HashMap<>();
        this.afterCheckStrategyList = new ArrayList<>();
    }

    public static StrategyManager getInstance() {
        if (strategyMgr == null) {
            strategyMgr = new StrategyManager();
        }
        return strategyMgr;
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
            case STRATEGY_FILL_GAP:
                strategyList.put(strategy, new FillGap(interval));
                break;
            case STRATEGY_CLOSE_ON_END:
                forceTriggerStrategyList.put(strategy, new CloseOnEnd());
                break;
            case STRATEGY_REOPEN:
                forceTriggerStrategyList.put(strategy, new Reopen());
                break;
            case STRATEGY_MA_240:
                strategyList.put(strategy, new MA240(interval));
                break;
            case STRATEGY_MA_240_520:
                strategyList.put(strategy, new MA240520(interval));
                break;
            case STRATEGY_ONLY_ONE_PER_DAY:
                afterCheckStrategyList.add(new OnlyOnePerDay());
                break;
            default:
                LogUtil.getInstance().info("Fail to add strategy " + strategy + ", unknown strategy");
                return false;
        }
        LogUtil.getInstance().info("Successful add strategy " + strategy);
        return true;
    }

    public void changeStrategy(StrategyEnum strategy, ForceTriggerStrategy newStrategy) {
        if (forceTriggerStrategyList.containsKey(strategy)) {
            LogUtil.getInstance().info("Fail to add strategy " + strategy + ", already contain");
            return;
        }
        if (newStrategy == null) {
            return;
        }
        forceTriggerStrategyList.put(strategy, newStrategy);
        LogUtil.getInstance().info("Successful add strategy " + strategy);
    }

    public void removeStrategy(StrategyEnum strategy) {
        strategyList.remove(strategy);
        forceTriggerStrategyList.remove(strategy);
        LogUtil.getInstance().info("Successful remove strategy " + strategy);
    }

    public void removeAll() {
        strategyList.clear();
        forceTriggerStrategyList.clear();
        LogUtil.getInstance().info("Successful remove all strategies");
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
        Calendar currTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int currMinute = currTime.get(Calendar.MINUTE);
        int currHour = currTime.get(Calendar.HOUR_OF_DAY);
        boolean forceOnly = ((currHour == 14 && currMinute > 56) || (currHour == 15 && currMinute == 0));

        PriorityQueue<AbstractStrategy> strategyQueue = new PriorityQueue<>();
        for (AbstractStrategy strategy : strategyList.values()) {
            if (Status.getInstance().isTrading()
                    && strategy.getPriority() < Setting.BLOCK_LOW_PRIORITY_STRATEGY
                    && currMinute % strategy.getInterval().getValue() == 0
                    && !forceOnly) {
                strategyQueue.add(strategy);
            }
        }
        for (ForceTriggerStrategy strategy : forceTriggerStrategyList.values()) {
            if (strategy.isTriggered(currHour, currMinute)) {
                strategyQueue.add(strategy);
            }
        }

        DataServiceManager dSrvMgr = DataServiceManager.getInstance();
        while(dSrvMgr.isSyncLocked()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        }

        LogUtil.getInstance().info(strategyQueue.size() + " strategies will be in use at "
                + currHour + ":" + currMinute + ": " + strategyQueueString(strategyQueue));
        Decision decision = new Decision();
        while (Status.getInstance().isTrading() && !strategyQueue.isEmpty()) {
            decision = strategyQueue.poll().execute(newPrice);
            if (decision.isExecute()) {
                break;
            }
        }
        for (AfterCheckStrategy strategy : afterCheckStrategyList) {
            decision = strategy.check(decision);
        }
        return decision;
    }

    public String strategyQueueString(PriorityQueue<AbstractStrategy> queue) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Iterator<AbstractStrategy> it = queue.iterator(); it.hasNext(); ) {
            AbstractStrategy strategy = it.next();
            sb.append(strategy.getName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
