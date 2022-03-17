package SerenaSimulation;

import SerenaSimulation.strategy.AbstractStrategy;
import SerenaSimulation.strategy.LossLimit;
import SerenaSimulation.strategy.MA520;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StrategyManagerTest {
    private final HashMap<StrategyEnum, AbstractStrategy> strategyList;
    private static StrategyManagerTest strategyMgr;

    private StrategyManagerTest() {
        this.strategyList = new HashMap<>();
    }

    public static StrategyManagerTest getInstance() {
        if(strategyMgr == null) {
            strategyMgr = new StrategyManagerTest();
        }
        return strategyMgr;
    }

    public void addStrategy(StrategyEnum strategy, IntervalEnum interval) {
        if(strategy == StrategyEnum.STRATEGY_MA_520) {
            strategyList.put(strategy, new MA520(interval));
        }
        if(strategy == StrategyEnum.STRATEGY_LOSS_LIMIT) {
            strategyList.put(strategy, new LossLimit(interval));
        }
    }

    public void removeStrategy(StrategyEnum strategy) {
        strategyList.remove(strategy);
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
        for(AbstractStrategy strategy : strategyList.values()) {
            if(currMinute % strategy.getInterval().getValue() == 0) {
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
