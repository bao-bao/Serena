package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class SingleEMAForUp extends AbstractStrategy {

    private boolean active;
    private double lastTradePrice;
    private double profit;
    private double profitMaximum;

    public SingleEMAForUp(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Single EMA For Up");

        double[] prior = FileUtil.readEmaLog(Controller.getInstance().getType(), interval, true);
        lastTradePrice = prior[0];
        active = lastTradePrice != 0.0;
        profit = active ? prior[1] - lastTradePrice : 0.0;
        profitMaximum = prior[2];
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_SINGLE_EMA_UP, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if (EMA.getSize() == 0) {
            return decision;
        }

        Status status = Status.getInstance();
        double currentEMAForUp = EMA.getCurrentEMAByEnum(EMAEnum.SINGLE_EMA_UP);

        if (!active && price.getPrice() > currentEMAForUp) {
            active = true;
            lastTradePrice = price.getPrice();
            FileUtil.singleEmaLog(Controller.getInstance().getType(), interval, true, true, lastTradePrice, price.getTime());
            // up-side is not active, trend can only be DOWN or NULL
            if (status.getTrendEMA() == TrendType.TREND_DOWN) {
                decision.make(TradingType.EMPTY, "price higher than EMA");
                status.setTrendEMA(TrendType.TREND_BOTH);
            } else {
                decision.make(TradingType.PUT_BUYING, "price higher than EMA");
                status.setTrendEMA(TrendType.TREND_UP);
            }
            return decision;
        }

        if (active) {
            profit = price.getPrice() - lastTradePrice;
            profitMaximum = Math.max(profit, profitMaximum);

            if (profit < 0 && Math.abs(profit) >= Setting.EMA_UP_LOSS_LIMIT * lastTradePrice) {
                if(status.getTrendEMA() == TrendType.TREND_UP && status.getStatus() == TradingType.PUT_BUYING) {
                    decision.make(TradingType.EMPTY, "EMA up ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_BOTH && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.SHORT_SELLING, "EMA up ends by loss limit");
                }
                reset(price);
                return decision;
            }

            if (profitMaximum > Setting.EMA_UP_PROFIT_THRESHOLD * lastTradePrice && profit <= Setting.EMA_UP_PROFIT_LIMIT * profitMaximum) {
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
        FileUtil.emaLog(Controller.getInstance().getType(), interval, true, false, lastTradePrice, price.getTime());
        active = false;
        lastTradePrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
