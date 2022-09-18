package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.MovingAverage;

public class MA240 extends AbstractStrategy {

    public MA240(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_MA_240_PRIORITY);
        super.setName("MA 240");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing MA 240...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_240, interval);

        MovingAverage currentMA = dataSvcMgr.queryData(interval).getNewMAvgs();
        MovingAverage lastMA = dataSvcMgr.queryData(interval).getLastMAvgs();
        double cMA5 = currentMA.getMA5();
        double cMA240 = currentMA.getMA240();
        double lMA5 = lastMA.getMA5();
        double lMA240 = lastMA.getMA240();

        TradingType currStatus = Status.getInstance().getStatus();

        if(cMA5 > cMA240 && lMA5 <= lMA240) {
            PutBuying(cMA5, cMA240, decision, currStatus);
        }
        if(cMA5 < cMA240 && lMA5 >= lMA240) {
            ShortSelling(cMA5, cMA240, decision, currStatus);
        }
        return decision;
    }
}
