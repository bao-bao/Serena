package SerenaSimulation.strategy;

import SerenaSimulation.StrategyManagerTest;
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


public class FillGap extends AbstractStrategy {

    public FillGap(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_FILL_GAP_PRIORITY);
        super.setName("Fill Gap");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing Fill Gap...");
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_FILL_GAP, interval);


        MinutesData data = dataSvcMgr.queryData(interval);
        MovingAverage MAs = data.getNewMAvg();
        double ref = data.getLastCrossPrice(MAEnum.MA5, MAEnum.MA20);
//        double ref = data.getNewMAvg().getMAByIndex(MAEnum.fromInt(Setting.FILL_GAP_BY_MA));
        double currentPrice = data.getNewPrice();

        if(Status.getInstance().getStatus() != TradingType.EMPTY || ref == 0) {
            return decision;
        }

        if(MAs.getMA5() > MAs.getMA20() && currentPrice - ref > Setting.FILL_GAP_THRESHOLD) {
            decision.make(TradingType.PUT_BUYING, "exceed reference");
            StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }

        if(MAs.getMA5() < MAs.getMA20() && ref - currentPrice > Setting.FILL_GAP_THRESHOLD) {
            decision.make(TradingType.SHORT_SELLING, "exceed reference");
            StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_LOSS_LIMIT, Setting.HIGH_LOSS_LIMIT_PRIORITY);
            StrategyManagerTest.getInstance().changePriority(StrategyEnum.STRATEGY_PROFIT_LIMIT, Setting.HIGH_PROFIT_LIMIT_PRIORITY);
        }
        return decision;
    }
}
