package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;

public class BasicEMAForUp extends AbstractStrategy {

    private boolean hasInit;
    private boolean active;
    private double lastCrossPrice;
    private double profit;
    private double profitMaximum;

    public BasicEMAForUp(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Basic EMA For Up");
        hasInit = false;
    }

    private void init(ExPrice price) {
        double[] prior = FileUtil.readEmaLog(Controller.getInstance().getType(), interval, true);
        lastCrossPrice = prior[0];
        active = lastCrossPrice != 0.0;
        profit = active ? lastCrossPrice - price.getPrice() : 0.0;
        profitMaximum = prior[1];
        hasInit = true;
    }

    @Override
    public Decision execute(ExPrice price) {
        if(!hasInit) {
            init(price);
        }
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
            FileUtil.emaLog(Controller.getInstance().getType(), interval, true, true, lastCrossPrice, price.getTime());
            if (status.getTrendEMA() == TrendType.TREND_DOWN) {
                decision.make(TradingType.EMPTY, "EMA cross up");
            } else {
                decision.make(TradingType.PUT_BUYING, "EMA cross up");
            }
            status.setTrendEMA(TrendType.TREND_UP);
            return decision;
        }

        if (active) {
            profit = price.getPrice() - lastCrossPrice;
            profitMaximum = Math.max(profit, profitMaximum);

            if (profit < 0 && Math.abs(profit) >= Setting.EMA_UP_LOSS_LIMIT * lastCrossPrice) {
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
                reset(price);
                return decision;
            }

            if (profitMaximum > Setting.EMA_UP_PROFIT_THRESHOLD * lastCrossPrice && profit <= Setting.EMA_UP_PROFIT_LIMIT * profitMaximum) {
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
        }
        FileUtil.emaLog(Controller.getInstance().getType(), interval, true, false, lastCrossPrice, price.getTime());
        active = false;
        lastCrossPrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
