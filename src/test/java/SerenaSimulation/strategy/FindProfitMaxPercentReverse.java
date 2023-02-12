package SerenaSimulation.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;

import java.util.ArrayList;

public class FindProfitMaxPercentReverse extends AbstractStrategy  {

    ArrayList<Double> res;
    double maxPrice;
    double lastCross;
    boolean trend;

    public FindProfitMaxPercentReverse(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_FORCE_TRIGGER_PRIORITY);
        super.setName("Find");
        res = new ArrayList<>();
        maxPrice = 0.0;
        lastCross = 0.0;
        trend = false;
    }


    @Override
    public Decision execute(ExPrice price) {

        Decision decision = new Decision(price, StrategyEnum.STRATEGY_MA_240, interval);
        ArrayList<Double> currentEMA = dataSvcMgr.queryData(interval).getNewEMAvg();
        ArrayList<Double> lastEMA = dataSvcMgr.queryData(interval).getLastEMAvg();
        if (lastEMA.size() == 0) {
            return decision;
        }
        if (!trend && currentEMA.get(0) < currentEMA.get(1) && lastEMA.get(0) > lastEMA.get(1)) {
            maxPrice = price.getPrice();
            lastCross = price.getPrice();
            trend = true;
        }

        if (trend) {
            maxPrice = Math.min(price.getPrice(), maxPrice);
        }
        if (trend && (currentEMA.get(0) > currentEMA.get(1) && lastEMA.get(0) < lastEMA.get(1))) {
            res.add((lastCross - maxPrice) / lastCross);
            lastCross = 0.0;
            maxPrice = 0.0;
            trend = false;
        }
        return decision;
    }

    public ArrayList<Double> getRes() {
        return res;
    }
}
