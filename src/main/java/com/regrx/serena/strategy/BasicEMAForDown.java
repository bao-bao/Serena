package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class BasicEMAForDown extends AbstractStrategy {

    private boolean active;
    private double lastCrossPrice;
    private double profit;
    private double profitMaximum;

    public BasicEMAForDown(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Basic EMA For Down");

        double[] prior = FileUtil.readEmaLog(Controller.getInstance().getType(), interval, false);
        lastCrossPrice = prior[0];
        active = lastCrossPrice != 0.0;
        profit = active ? lastCrossPrice - prior[1] : 0.0;
        profitMaximum = prior[2];
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_BASIC_EMA_FOR_DOWN, interval);
        ExpMovingAverage EMA = dataSvcMgr.queryData(interval).getExpMAvgs();
        if (EMA.getSize() == 0) {
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
            FileUtil.emaLog(Controller.getInstance().getType(), interval, false, true, lastCrossPrice, price.getTime());
            // down-side is not active, trend can only be UP or NULL
            if (status.getTrendEMA() == TrendType.TREND_UP) {
                decision.make(TradingType.EMPTY, "EMA cross down");
                status.setTrendEMA(TrendType.TREND_BOTH);
            } else {
                decision.make(TradingType.SHORT_SELLING, "EMA cross down");
                status.setTrendEMA(TrendType.TREND_DOWN);
            }
            return decision;
        }

        if (active) {
            profit = lastCrossPrice - price.getPrice();
            profitMaximum = Math.max(profit, profitMaximum);

            // down-side is active, trend can only be DOWN or BOTH
            if (profit < 0 && Math.abs(profit) > Setting.EMA_DOWN_LOSS_LIMIT * lastCrossPrice) {
                if(status.getTrendEMA() == TrendType.TREND_DOWN && status.getStatus() == TradingType.SHORT_SELLING) {
                    decision.make(TradingType.EMPTY, "EMA down ends by loss limit");
                }
                if(status.getTrendEMA() == TrendType.TREND_BOTH && status.getStatus() == TradingType.EMPTY) {
                    decision.make(TradingType.PUT_BUYING, "EMA down ends by loss limit");
                }
                reset(price);
                return decision;
            }

            if (profitMaximum > Setting.EMA_DOWN_PROFIT_THRESHOLD * lastCrossPrice && profit < Setting.EMA_DOWN_PROFIT_LIMIT * profitMaximum) {
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
        FileUtil.emaLog(Controller.getInstance().getType(), interval, false, false, lastCrossPrice, price.getTime());
        active = false;
        lastCrossPrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
