package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.MAEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.MovingAverage;
import com.regrx.serena.service.StrategyManager;


public class FillGap extends AbstractStrategy {

    public FillGap(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_FILL_GAP_PRIORITY);
        super.setName("Fill Gap");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing Fill Gap...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_520, interval);

        MinutesData data = dataSvcMgr.queryData(interval);
        MovingAverage MAs = data.getNewMAvgs();
        double currentMA = data.getNewMAvgs().getMAByIndex(MAEnum.fromInt(Setting.FILL_GAP_BY_MA));
        double currentPrice = data.getNewPrice();

        if(Status.getInstance().getStatus() != TradingType.EMPTY || currentMA == 0) {
            return decision;
        }

        if(MAs.getMA5() > MAs.getMA20() && currentPrice - currentMA > Setting.FILL_GAP_THRESHOLD) {
            decision.make(TradingType.PUT_BUYING, "exceed MA");
            StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }

        if(MAs.getMA5() < MAs.getMA20() && currentMA - currentPrice > Setting.FILL_GAP_THRESHOLD) {
            decision.make(TradingType.SHORT_SELLING, "exceed MA");
            StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            StrategyManager.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }
        return decision;
    }
}
