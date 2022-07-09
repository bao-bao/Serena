package SerenaSimulation.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.constant.TrendType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;

public class ProfitLimit extends AbstractStrategy {

    public ProfitLimit(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_LOSS_LIMIT_PRIORITY);
        super.setName("Profit Limit");
    }

    @Override
    public Decision execute(ExPrice price) {
        LogUtil.getInstance().info("Executing PROFIT LIMIT...");
        Status status = Status.getInstance();
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_PROFIT_LIMIT, interval);

        double lTP = status.getLastTradePrice();
        double currP = price.getPrice();
        double lastP = dataSvcMgr.queryData(interval).getLastPrice();
        TradingType currStatus = status.getStatus();


        if (currStatus == TradingType.EMPTY) {
            if (status.getTrend() == TrendType.TREND_UP && currP - lTP > Setting.RESTORE_THRESHOLD) {
                decision.make(TradingType.PUT_BUYING, "exceed restore limit");
            } else if (status.getTrend() == TrendType.TREND_DOWN && lTP - currP > Setting.RESTORE_THRESHOLD) {
                decision.make(TradingType.SHORT_SELLING, "exceed restore limit");
            }
            return decision;

        }
        
        // current is not empty, limit the loss into a threshold
        if (currStatus == TradingType.PUT_BUYING) {
            return limitProfit(decision, currP > lTP, lastP - lTP, currP - lTP);
        } else if (currStatus == TradingType.SHORT_SELLING) {
            return limitProfit(decision, currP < lTP, lTP - lastP, lTP - currP);
        }
        return null;
    }

    private Decision limitProfit(Decision decision, boolean hasProfit, double historyProfit, double currProfit) {
        // has profit but not much, and was much
        if (hasProfit && currProfit < Setting.PROFIT_LIMIT_THRESHOLD && historyProfit > Setting.PROFIT_LIMIT_THRESHOLD) {
            decision.make(TradingType.EMPTY, "profit limit");
        }
        return decision;
    }

}
