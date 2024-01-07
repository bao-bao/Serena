package SerenaSimulation.strategy;

import SerenaSimulation.DataServiceManagerTest;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class SingleEMAForUp extends AbstractStrategy {

    private boolean hasInit;
    private boolean active;
    private double lastTradePrice;
    private double profit;
    private double profitMaximum;

    public SingleEMAForUp(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Single EMA For Up");
        hasInit = false;
    }

    private void init() {
        active = false;
        lastTradePrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
        hasInit = true;
    }

    @Override
    public Decision execute(ExPrice price) {
        if(!hasInit) {
            init();
        }
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_SINGLE_EMA_UP, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if (EMA.getSize() == 0) {
            return decision;
        }

        Status status = Status.getInstance();
        double currentEMAForUp = EMA.getCurrentEMAByEnum(EMAEnum.UP_SHORT_TERM_EMA);

        if (!active && price.getPrice() > currentEMAForUp) {
            active = true;
            lastTradePrice = price.getPrice();
            if (status.getTrendEMA() == TrendType.TREND_DOWN) {
                decision.make(TradingType.EMPTY, "price higher than EMA");
            } else {
                decision.make(TradingType.PUT_BUYING, "price higher than EMA");
            }
            status.setTrendEMA(TrendType.TREND_UP);
            return decision;
        }

        if (active) {
            profit = price.getPrice() - lastTradePrice;
            profitMaximum = Math.max(profit, profitMaximum);

            if (profit < 0 && Math.abs(profit) >= Setting.EMA_UP_LOSS_LIMIT * lastTradePrice) {
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() != TradingType.SHORT_SELLING) {
                    decision.make(TradingType.SHORT_SELLING, "EMA up ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() != TradingType.EMPTY) {
                    decision.make(TradingType.EMPTY, "EMA up ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.SHORT_SELLING, "EMA up ends by profit limit");
                    status.setTrendEMA(TrendType.TREND_DOWN);
                }
                reset();
                return decision;
            }

            if (profitMaximum > Setting.EMA_UP_PROFIT_THRESHOLD * lastTradePrice && profit <= Setting.EMA_UP_PROFIT_LIMIT * profitMaximum) {
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() != TradingType.SHORT_SELLING) {
                    decision.make(TradingType.SHORT_SELLING, "EMA up ends by profit limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() != TradingType.EMPTY) {
                    decision.make(TradingType.EMPTY, "EMA up ends by profit limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.SHORT_SELLING, "EMA up ends by profit limit");
                    status.setTrendEMA(TrendType.TREND_DOWN);
                }
                reset();
                return decision;
            }
        }
        return decision;
    }

    private void reset() {
        Status status = Status.getInstance();
        if(status.getTrendEMA() == TrendType.TREND_UP) {
            status.setTrendEMA(TrendType.NULL);
        }
        active = false;
        lastTradePrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
