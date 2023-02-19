package SerenaSimulation.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class BasicEMAForUp extends AbstractStrategy {

    private double lastCrossPrice;
    private boolean inTrade;
    private double profit;
    private double profitMaximum;

    public BasicEMAForUp(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Basic EMA For Up");
        reset();
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_BASIC_EMA_FOR_UP, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if (EMA.getSize() == 0) {
            return decision;
        }

        double currentShortTermEMA = EMA.getCurrentEMAByEnum(EMAEnum.UP_SHORT_TERM_EMA);
        double currentLongTermEMA = EMA.getCurrentEMAByEnum(EMAEnum.UP_LONG_TERM_EMA);
        double lastShortTermEMA = EMA.getHistoryEMAByEnum(EMAEnum.UP_SHORT_TERM_EMA, 1);
        double lastLongTermEMA = EMA.getHistoryEMAByEnum(EMAEnum.UP_LONG_TERM_EMA, 1);

        if (!inTrade && currentShortTermEMA > currentLongTermEMA && lastShortTermEMA < lastLongTermEMA) {
            lastCrossPrice = price.getPrice();
            inTrade = true;
            Status.getInstance().setTrendEMA(TrendType.TREND_UP);
            decision.make(TradingType.PUT_BUYING, "EMA cross up");
            return decision;
        }

        if (inTrade && Status.getInstance().getTrendEMA() == TrendType.TREND_UP) {
            profit = price.getPrice() - lastCrossPrice;
            profitMaximum = Math.max(profit, profitMaximum);

//            if(currentShortTermEMA < currentLongTermEMA && lastShortTermEMA > lastLongTermEMA) {
//                reset();
//                decision.make(TradingType.EMPTY, "EMA cross down");
//                return decision;
//            }

            if (profit < 0 && Math.abs(profit) > Setting.EMA_LOSS_LIMIT * lastCrossPrice) {
                reset();
                decision.make(TradingType.EMPTY, "EMA loss limit");
                return decision;
            }

            if (profitMaximum > Setting.EMA_PROFIT_THRESHOLD * lastCrossPrice && profit < Setting.EMA_PROFIT_LIMIT * profitMaximum) {
                reset();
                decision.make(TradingType.EMPTY, "EMA profit limit");
                return decision;
            }
        }
        return decision;
    }

    private void reset() {
        lastCrossPrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
        inTrade = false;
        Status.getInstance().setTrendEMA(TrendType.NULL);
    }
}
