package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.constant.TrendType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;

public class LossLimit extends AbstractStrategy {

    public LossLimit(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_LOSS_LIMIT_PRIORITY);
        super.setName("Loss Limit");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing LOSS LIMIT...");
        Status status = Status.getInstance();
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_LOSS_LIMIT, interval);

        double lTP = status.getLastTradePrice();
        double currP = price.getPrice();
        TradingType currStatus = status.getStatus();

        if (currStatus == TradingType.EMPTY) {
            if (dataSvcMgr.queryData(interval).getTrend() == TrendType.TREND_UP && currP - lTP > Setting.RESTORE_THRESHOLD) {
                decision.make(TradingType.PUT_BUYING, "exceed restore limit");
                return decision;
            } else if (dataSvcMgr.queryData(interval).getTrend() == TrendType.TREND_DOWN && lTP - currP > Setting.RESTORE_THRESHOLD) {
                decision.make(TradingType.SHORT_SELLING, "exceed restore limit");
                return decision;
            } else {
                return decision;
            }
        }

        // current is not empty, limit the loss into a threshold
        if (currStatus == TradingType.PUT_BUYING) {
            return limitLoss(decision, currP > lTP, currP - lTP);
        } else if (currStatus == TradingType.SHORT_SELLING) {
            return limitLoss(decision, currP < lTP, lTP - currP);
        }
        return decision;
    }


    private Decision limitLoss(Decision decision, boolean hasProfit, double currProfit) {
        if (!hasProfit && Math.abs(currProfit) > Setting.LOSS_LIMIT_THRESHOLD) {         // deficit much
            decision.make(TradingType.EMPTY, "loss limit");
            return decision;
        }
        return decision;
    }
}