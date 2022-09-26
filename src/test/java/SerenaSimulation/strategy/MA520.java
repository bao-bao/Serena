package SerenaSimulation.strategy;

import SerenaSimulation.StrategyManagerTest;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.constant.TrendType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.MovingAverage;

public class MA520 extends AbstractStrategy {

    private TrendType lastTradeInTrend;

    public MA520(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_MA_520_PRIORITY);
        super.setName("MA 520");
        this.lastTradeInTrend = TrendType.NULL;
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing MA 520...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_520, interval);

        MovingAverage currentMA = dataSvcMgr.queryData(interval).getNewMAvgs();
        MovingAverage lastMA = dataSvcMgr.queryData(interval).getLastMAvgs();
        double cMA5 = currentMA.getMA5();
        double cMA20 = currentMA.getMA20();
        double lMA5 = lastMA.getMA5();
        double lMA20 = lastMA.getMA20();
        if(cMA20 == 0) {
            return decision;
        }

        TradingType currStatus = Status.getInstance().getStatus();

        if((cMA5 - cMA20) * (lMA5 - lMA20) < 0) {
            StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }

        if(cMA5 > cMA20 && lMA5 <= lMA20) {
            PutBuyingByThreshold(cMA5, cMA20, decision, currStatus);
        }

        else if(cMA5 < cMA20 && lMA5 >= lMA20) {
            ShortSellingByThreshold(cMA5, cMA20, decision, currStatus);
        }
        else {
            if(cMA5 > cMA20 && lMA5 > lMA20 && currStatus != TradingType.PUT_BUYING && lastTradeInTrend != TrendType.TREND_UP) {
                PutBuyingByThreshold(cMA5, cMA20, decision, currStatus);
            } else if(cMA5 < cMA20 && lMA5 < lMA20 && currStatus != TradingType.SHORT_SELLING && lastTradeInTrend != TrendType.TREND_DOWN) {
                ShortSellingByThreshold(cMA5, cMA20, decision, currStatus);
            }
        }
        if(decision.isExecute()) {
            TrendType currTradeInTrend = cMA5 >= cMA20 ? TrendType.TREND_UP : TrendType.TREND_DOWN;
            Status.getInstance().setTrend(currTradeInTrend);
            if(currTradeInTrend != lastTradeInTrend && decision.getTradingType() == TradingType.EMPTY && Setting.MA_PRIMARY) {
                StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.DEFAULT_LOSS_LIMIT_PRIORITY);
                StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.DEFAULT_PROFIT_LIMIT_PRIORITY);
            }
            if(decision.getTradingType() != TradingType.EMPTY) {
                StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
                StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
//                lastTradeInTrend = currTradeInTrend;
            }
            lastTradeInTrend = currTradeInTrend;        // sometime will not open
        }
        return decision;
    }
}
