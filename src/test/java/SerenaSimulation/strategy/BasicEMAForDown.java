package SerenaSimulation.strategy;

import SerenaSimulation.DataServiceManagerTest;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class BasicEMAForDown extends AbstractStrategy {

    private boolean hasInit;
    private boolean active;
    private double lastCrossPrice;
    private double profit;
    private double profitMaximum;

    public BasicEMAForDown(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Basic EMA For Down");
        hasInit = false;
    }

    private void init() {
        active = false;
        lastCrossPrice = DataServiceManagerTest.getInstance().queryData(interval).getLastEMACrossPrice(EMAEnum.DOWN_SHORT_TERM_EMA, EMAEnum.DOWN_LONG_TERM_EMA);
        profit = 0.0;
        profitMaximum = 0.0;
        hasInit = true;
    }

    @Override
    public Decision execute(ExPrice price) {
        if(!hasInit) {
            init();
        }
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_BASIC_EMA_FOR_DOWN, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if(EMA.getSize() == 0) {
            return decision;
        }

        Status status = Status.getInstance();
        double currentShortTermEMA = EMA.getCurrentEMAByEnum(EMAEnum.DOWN_SHORT_TERM_EMA);
        double currentLongTermEMA = EMA.getCurrentEMAByEnum(EMAEnum.DOWN_LONG_TERM_EMA);
        double lastShortTermEMA = EMA.getHistoryEMAByEnum(EMAEnum.DOWN_SHORT_TERM_EMA, 1);
        double lastLongTermEMA = EMA.getHistoryEMAByEnum(EMAEnum.DOWN_LONG_TERM_EMA, 1);

        if (!active && currentShortTermEMA < currentLongTermEMA && lastShortTermEMA > lastLongTermEMA) {
            active = true;
            lastCrossPrice = price.getPrice();
            if (status.getTrendEMA() == TrendType.TREND_UP) {
                decision.make(TradingType.EMPTY, "EMA cross down");
            } else {
                decision.make(TradingType.SHORT_SELLING, "EMA cross down");
            }
            status.setTrendEMA(TrendType.TREND_DOWN);
            return decision;
        }

        if (active) {
            profit = lastCrossPrice - price.getPrice();
            profitMaximum = Math.max(profit, profitMaximum);

            if (profit < 0 && Math.abs(profit) > Setting.EMA_LOSS_LIMIT * lastCrossPrice) {
                reset();
                if(status.getTrendEMA() == TrendType.TREND_UP) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends, loss limit");
                } else {
                    decision.make(TradingType.EMPTY, "EMA down ends, loss limit");
                }
                return decision;
            }

            if (profitMaximum > Setting.EMA_PROFIT_THRESHOLD * lastCrossPrice && profit < Setting.EMA_PROFIT_LIMIT * profitMaximum) {
                reset();
                if(status.getTrendEMA() == TrendType.TREND_UP) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends, profit limit");
                } else {
                    decision.make(TradingType.EMPTY, "EMA down ends, profit limit");
                }
                return decision;
            }
        }
        return decision;
    }

    private void reset() {
        active = false;
        lastCrossPrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
