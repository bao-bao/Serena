package SerenaSimulation.strategy;

import SerenaSimulation.DataServiceManagerTest;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;

import java.util.LinkedList;
import java.util.ListIterator;


public class DonchianGolden extends AbstractStrategy {
    private boolean upActive; // need log
    private boolean downActive; // need log
    private boolean isUp; // need log
    private boolean upTrend; // need log
    private boolean downTrend; // need log
    private boolean enableUpPL; // need log
    private boolean enableDownPL; // need log
    private double oldBase;
    private double oldTops;
    private double tradeBase; // need log
    private double tradeTops; // need log
    private double continousLoss; // need log
    private int continousLossLimit; // need log

    public DonchianGolden(IntervalEnum interval) {
        super(interval, Setting.DEFAULT_PRIORITY);
        super.setName("DonchianGolden");
        upActive = false;
        downActive = false;
        isUp = false;
        upTrend = false;
        downTrend = false;
        enableUpPL = false;
        enableDownPL = false;
        oldBase = 0;
        oldTops = 0;
        tradeBase = 0;
        tradeTops = 0;
        continousLoss = 0;
        continousLossLimit = 0;
    }

    @Override
    public Decision execute(ExPrice price) {
        Decision decision = new Decision(price, StrategyEnum.STRATEGY_DONCHIAN_GOLDEN, interval);
        LinkedList<ExPrice> prices = DataServiceManagerTest.getInstance().queryData(interval).getPrices();
        if (prices.isEmpty() || prices.size() < Setting.DONCHIAN_GOLDEN_AGGREGATE_COUNT) {
            return decision;
        }

        double base = Double.MAX_VALUE;
        double tops = 0;
        ListIterator<ExPrice> iterator = prices.listIterator(0);
        for (int i = 0; i < Setting.DONCHIAN_GOLDEN_AGGREGATE_COUNT; i++) {
            ExPrice historyPrice = iterator.next();
            base = Double.min(base, historyPrice.getLowest());
            tops = Double.max(tops, historyPrice.getHighest());
        }
        if (oldBase == 0 || oldTops == 0) {
            oldBase = base;
            oldTops = tops;
            return decision;
        }
        if (price.getLowest() == base && base < oldBase) {
            upTrend = true;
            downTrend = false;
        }
        if (price.getHighest() == tops && tops > oldTops) {
            downTrend = true;
            upTrend = false;
        }
        oldBase = base;
        oldTops = tops;

        double L1Threshold = base + ((tops - base) * Setting.DONCHIAN_GOLDEN_PUT_THRESHOLD);
        double S1Threshold = base + ((tops - base) * Setting.DONCHIAN_GOLDEN_SHORT_THRESHOLD);
        double LC1Threshold = base + ((tops - base) * Setting.DONCHIAN_GOLDEN_PUT_EMPTY_THRESHOLD);
        double SC1Threshold = base + ((tops - base) * Setting.DONCHIAN_GOLDEN_SHORT_EMPTY_THRESHOLD);

        if (!upActive && upTrend && price.getPrice() > L1Threshold && continousLossLimit < Setting.DONCHIAN_GOLDEN_CONTINOUS_LOSS_LIMIT) {
            if (!isUp) {
                continousLossLimit = 0;
            }
            upActive = true;
            isUp = true;
            upTrend = false;
            tradeBase = base;
            if (downActive) {
                downActive = false;
                enableDownPL = false;
                tradeTops = 0;
            }
            decision.make(TradingType.PUT_BUYING, "L1");
            return decision;
        }
        if (!downActive && downTrend && price.getPrice() < S1Threshold && continousLossLimit > -Setting.DONCHIAN_GOLDEN_CONTINOUS_LOSS_LIMIT) {
            if (isUp) {
                continousLoss = 0;
                continousLossLimit = 0;
            }
            downActive = true;
            isUp = false;
            downTrend = false;
            tradeTops = tops;
            if (upActive) {
                upActive = false;
                enableUpPL = false;
                tradeBase = 0;
            }
            decision.make(TradingType.SHORT_SELLING, "S1");
            return decision;
        }

        if (upActive && price.getPrice() > LC1Threshold) {
            enableUpPL = true;
        }
        if (enableUpPL && price.getPrice() < LC1Threshold) {
            donchianGoldenReset();
            continousLossLimit += 1;
            decision.make(TradingType.EMPTY, "LC1");
            return decision;
        }
        if (isUp && tradeBase > 0 && price.getPrice() < tradeBase) {
            donchianGoldenReset();
            continousLossLimit += 1;
            decision.make(TradingType.EMPTY, "LC2");
            return decision;
        }
        if (downActive && price.getPrice() < SC1Threshold) {
            enableDownPL = true;
        }
        if (enableDownPL && price.getPrice() > SC1Threshold) {
            donchianGoldenReset();
            continousLossLimit -= 1;
            decision.make(TradingType.EMPTY, "SC1");
            return decision;
        }
        if (!isUp && tradeTops > 0 && price.getPrice() > tradeTops) {
            donchianGoldenReset();
            continousLossLimit -= 1;
            decision.make(TradingType.EMPTY, "SC2");
            return decision;
        }
        Status status = Status.getInstance();
        if (status.getStatus() != TradingType.EMPTY && status.getStatus() != TradingType.NO_ACTION) {
            double sign = status.getStatus() == TradingType.PUT_BUYING ? 1 : -1;
            double profit = sign * (price.getPrice() - status.getLastTradePrice());
            if (profit < -Setting.DONCHIAN_GOLDEN_LOSS_THRESHOLD) {
                donchianGoldenReset();
                continousLossLimit += sign;
                decision.make(TradingType.EMPTY, "C");
                return decision;
            }
        }

        return decision;
    }

    private void donchianGoldenReset() {
        upActive = false;
        downActive = false;
        enableUpPL = false;
        enableDownPL = false;
        tradeBase = 0.0;
        tradeTops = 0.0;
    }
}
