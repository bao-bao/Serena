package com.regrx.serena2.strategy;

import com.regrx.serena2.common.Setting;
import com.regrx.serena2.common.constant.*;
import com.regrx.serena2.common.utils.FileUtil;
import com.regrx.serena2.employee.Trader;
import com.regrx.serena2.data.base.Decision;
import com.regrx.serena2.data.base.ExPrice;
import com.regrx.serena2.data.base.Status;
import com.regrx.serena2.data.statistic.ExpMovingAverage;
import com.regrx.serena2.strategy.AbstractStrategy;

public class BasicEMAForUp extends AbstractStrategy {

    private boolean active;
    private double lastCrossPrice;
    private double profit;
    private double profitMaximum;

    private String type;

    public BasicEMAForUp(IntervalEnum interval, String type) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Basic EMA For Up");

        double[] prior = FileUtil.readEmaLog(type, interval, true);
        lastCrossPrice = prior[0];
        active = lastCrossPrice != 0.0;
        profit = active ? prior[1] - lastCrossPrice : 0.0;
        profitMaximum = prior[2];
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_BASIC_EMA_FOR_UP, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if (EMA.getSize() == 0) {
            return decision;
        }

        Status status = Status.getInstance();
        double currentShortTermEMA = EMA.getCurrentEMAByEnum(EMAEnum.UP_SHORT_TERM_EMA);
        double currentLongTermEMA = EMA.getCurrentEMAByEnum(EMAEnum.UP_LONG_TERM_EMA);
        double lastShortTermEMA = EMA.getHistoryEMAByEnum(EMAEnum.UP_SHORT_TERM_EMA, 1);
        double lastLongTermEMA = EMA.getHistoryEMAByEnum(EMAEnum.UP_LONG_TERM_EMA, 1);

        if (!active && currentShortTermEMA > currentLongTermEMA && lastShortTermEMA < lastLongTermEMA) {
            active = true;
            lastCrossPrice = price.getPrice();
            FileUtil.emaLog(type, interval, true, true, lastCrossPrice, price.getTime());
            // up-side is not active, trend can only be DOWN or NULL
            if (status.getTrendEMA() == TrendType.TREND_DOWN) {
                decision.make(TradingType.EMPTY, "EMA cross up");
                status.setTrendEMA(TrendType.TREND_BOTH);
            } else {
                decision.make(TradingType.PUT_BUYING, "EMA cross up");
                status.setTrendEMA(TrendType.TREND_UP);
            }
            return decision;
        }

        if (active) {
            profit = price.getPrice() - lastCrossPrice;
            profitMaximum = Math.max(profit, profitMaximum);

            // up-side is active, trend can only be UP or BOTH
            if (profit < 0 && Math.abs(profit) >= Setting.EMA_UP_LOSS_LIMIT * lastCrossPrice) {
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() == TradingType.PUT_BUYING) {
                    decision.make(TradingType.EMPTY, "EMA up ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_BOTH && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.SHORT_SELLING, "EMA up ends by loss limit");
                }
                reset(price);
                return decision;
            }

            if (profitMaximum > Setting.EMA_UP_PROFIT_THRESHOLD * lastCrossPrice && profit <= Setting.EMA_UP_PROFIT_LIMIT * profitMaximum) {
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() == TradingType.PUT_BUYING) {
                    decision.make(TradingType.EMPTY, "EMA up ends by profit limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_BOTH && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.SHORT_SELLING, "EMA up ends by profit limit");
                    status.setTrendEMA(TrendType.TREND_DOWN);
                }
                reset(price);
                return decision;
            }
        }
        return decision;
    }

    private void reset(ExPrice price) {
        Status status = Status.getInstance();
        if(status.getTrendEMA() == TrendType.TREND_UP) {
            status.setTrendEMA(TrendType.NULL);
        } else if (status.getTrendEMA() == TrendType.TREND_BOTH) {
            status.setTrendEMA(TrendType.TREND_DOWN);
        }
        FileUtil.emaLog(type, interval, true, false, lastCrossPrice, price.getTime());
        active = false;
        lastCrossPrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
