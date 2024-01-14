package SerenaSimulation.strategy;

import SerenaSimulation.DataServiceManagerTest;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class SingleEMAForDown extends AbstractStrategy {

    private boolean hasInit;
    private boolean active;
    private double lastTradePrice;
    private double profit;
    private double profitMaximum;

    public SingleEMAForDown(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Single EMA For Down");
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
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_SINGLE_EMA_DOWN, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if(EMA.getSize() == 0) {
            return decision;
        }

        Status status = Status.getInstance();
        double currentEMAForDown = EMA.getCurrentEMAByEnum(EMAEnum.UP_LONG_TERM_EMA);

        if (!active && price.getPrice() < currentEMAForDown) {
            active = true;
            lastTradePrice = price.getPrice();
            if (status.getTrendEMA() == TrendType.TREND_UP) {
                decision.make(TradingType.EMPTY, "price lower than EMA");
            } else {
                decision.make(TradingType.SHORT_SELLING, "price lower than EMA");
            }
            status.setTrendEMA(TrendType.TREND_DOWN);
            return decision;
        }

        if (active) {
            profit = lastTradePrice - price.getPrice();
            profitMaximum = Math.max(profit, profitMaximum);

            if (profit < 0 && Math.abs(profit) > Setting.SINGLE_EMA_DOWN_LOSS_LIMIT * lastTradePrice) {
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() != TradingType.PUT_BUYING) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() != TradingType.EMPTY) {
                    decision.make(TradingType.EMPTY, "EMA down ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends by loss limit");
                    status.setTrendEMA(TrendType.TREND_UP);
                }
                reset();
                return decision;
            }

            if (profitMaximum > Setting.SINGLE_EMA_DOWN_PROFIT_THRESHOLD * lastTradePrice && profit < Setting.SINGLE_EMA_DOWN_PROFIT_LIMIT * profitMaximum) {
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() != TradingType.PUT_BUYING) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends by profit limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() != TradingType.EMPTY) {
                    decision.make(TradingType.EMPTY, "EMA down ends by profit limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends by profit limit");
                    status.setTrendEMA(TrendType.TREND_UP);
                }
                reset();
                return decision;
            }
        }
        return decision;
    }

    private void reset() {
        Status status = Status.getInstance();
        if(status.getTrendEMA() == TrendType.TREND_DOWN) {
            status.setTrendEMA(TrendType.NULL);
        }
        active = false;
        lastTradePrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
