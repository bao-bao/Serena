package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class SingleEMAForDown extends AbstractStrategy {

    private boolean active;
    private double lastTradePrice;
    private double profit;
    private double profitMaximum;

    public SingleEMAForDown(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Single EMA For Down");

        double[] prior = FileUtil.readSingleEmaLog(Controller.getInstance().getType(), interval, false);
        lastTradePrice = prior[0];
        active = lastTradePrice != 0.0;
        profit = active ? lastTradePrice - prior[1] : 0.0;
        profitMaximum = prior[2];
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_SINGLE_EMA_DOWN, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if(EMA.getSize() == 0) {
            return decision;
        }

        Status status = Status.getInstance();
        double currentEMAForDown = EMA.getCurrentEMAByEnum(EMAEnum.SINGLE_EMA_DOWN);

        if (!active && price.getPrice() < currentEMAForDown) {
            active = true;
            lastTradePrice = price.getPrice();
            FileUtil.singleEmaLog(Controller.getInstance().getType(), interval, false, true, lastTradePrice, price.getTime());
            // down-side is not active, trend can only be UP or NULL
            if (status.getTrendEMA() == TrendType.TREND_UP) {
                decision.make(TradingType.EMPTY, "price lower than EMA");
                status.setTrendEMA(TrendType.TREND_BOTH);
            } else {
                decision.make(TradingType.SHORT_SELLING, "price lower than EMA");
                status.setTrendEMA(TrendType.TREND_DOWN);
            }
            return decision;
        }

        if (active) {
            profit = lastTradePrice - price.getPrice();
            profitMaximum = Math.max(profit, profitMaximum);

            if (profit < 0 && Math.abs(profit) > Setting.EMA_DOWN_LOSS_LIMIT * lastTradePrice) {
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() == TradingType.SHORT_SELLING) {
                    decision.make(TradingType.EMPTY, "EMA down ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_BOTH && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends by loss limit");
                }
                reset(price);
                return decision;
            }

            if (profitMaximum > Setting.EMA_DOWN_PROFIT_THRESHOLD * lastTradePrice && profit < Setting.EMA_DOWN_PROFIT_LIMIT * profitMaximum) {
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() == TradingType.SHORT_SELLING) {
                    decision.make(TradingType.EMPTY, "EMA down ends by profit limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_BOTH && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends by profit limit");
                }
                reset(price);
                return decision;
            }
        }
        return decision;
    }

    private void reset(ExPrice price) {
        Status status = Status.getInstance();
        if(status.getTrendEMA() == TrendType.TREND_DOWN) {
            status.setTrendEMA(TrendType.NULL);
        } else if (status.getTrendEMA() == TrendType.TREND_BOTH) {
            status.setTrendEMA(TrendType.TREND_UP);
        }
        FileUtil.singleEmaLog(Controller.getInstance().getType(), interval, false, false, lastTradePrice, price.getTime());
        active = false;
        lastTradePrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
