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
    private double lastCrossPrice;
    private double profit;
    private double profitMaximum;

    public BasicEMAForDown(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_BASIC_EMA_PRIORITY);
        super.setName("Basic EMA For Down");
    }

    private void init() {
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

        if (status.getStatus() != TradingType.SHORT_SELLING && currentShortTermEMA < currentLongTermEMA && lastShortTermEMA > lastLongTermEMA) {
            lastCrossPrice = price.getPrice();
            status.setStatus(TradingType.SHORT_SELLING);
            status.setTrendEMA(TrendType.TREND_DOWN);
            decision.make(TradingType.SHORT_SELLING, "EMA cross down");
            return decision;
        }

        if (status.getStatus() == TradingType.SHORT_SELLING  && status.getTrendEMA() == TrendType.TREND_DOWN) {
            profit = lastCrossPrice - price.getPrice();
            profitMaximum = Math.max(profit, profitMaximum);

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
        Status status = Status.getInstance();
        lastCrossPrice = 0.0;
        profit = 0.0;
        profitMaximum = 0.0;
        status.setStatus(TradingType.EMPTY);
        status.setTrendEMA(TrendType.NULL);
    }
}
