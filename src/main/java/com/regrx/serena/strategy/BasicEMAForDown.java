package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.*;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.ExpMovingAverage;
import com.regrx.serena.service.DataServiceManager;

public class BasicEMAForDown extends AbstractStrategy {

    private boolean hasInit;
    private boolean active;
    private double lastCrossPrice;
    private double profit;
    private double profitMaximum;

    public BasicEMAForDown(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Basic EMA For Down");
    }

    private void init() {
        active = Status.getInstance().getStatus() == TradingType.SHORT_SELLING;
        lastCrossPrice = DataServiceManager.getInstance().queryData(interval).getLastEMACrossPrice(EMAEnum.DOWN_SHORT_TERM_EMA, EMAEnum.DOWN_LONG_TERM_EMA);
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

            if (profit < 0 && Math.abs(profit) > Setting.EMA_DOWN_LOSS_LIMIT * lastCrossPrice) {
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

            if (profitMaximum > Setting.EMA_DOWN_PROFIT_THRESHOLD * lastCrossPrice && profit < Setting.EMA_DOWN_PROFIT_LIMIT * profitMaximum) {
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
        lastCrossPrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
    }
}
