package SerenaSimulation.strategy;

import SerenaSimulation.DataServiceManagerTest;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;

import java.util.ArrayList;

public abstract class AbstractStrategy implements Comparable<AbstractStrategy> {
    protected String name;
    protected IntervalEnum interval;
    protected Integer priority;
    protected final DataServiceManagerTest dataSvcMgr = DataServiceManagerTest.getInstance();

    AbstractStrategy(IntervalEnum interval, int priority) {
        this.interval = interval;
        this.priority = priority;
    }

    @Override
    public int compareTo(AbstractStrategy s) {
        return priority.compareTo(s.priority);
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public void setPriority(Integer p) {
        priority = p;
    }

    public Integer getPriority() {
        return priority;
    }

    public IntervalEnum getInterval() {
        return interval;
    }

    public void setInterval(IntervalEnum interval) {
        this.interval = interval;
    }

    public abstract Decision execute(ExPrice price);

    // TODO: add reason para
    protected void PutBuyingUtil(double ma1, double ma2, Decision decision, TradingType currStatus, double thres) {
        if (Math.abs(ma1 - ma2) >= thres) {
            if (currStatus == TradingType.SHORT_SELLING) {
                decision.make(TradingType.PUT_BUYING, "MA cross");
            } else if (currStatus == TradingType.EMPTY) {
                decision.make(TradingType.PUT_BUYING, "empty");
            }
        } else {
            if (currStatus == TradingType.SHORT_SELLING) {
                decision.make(TradingType.EMPTY, "MA cross");
            }
        }
    }

    protected void ShortSellingUtil(double ma1, double ma2, Decision decision, TradingType currStatus, double thres) {
        if (Math.abs(ma1 - ma2) >= thres) {
            if (currStatus == TradingType.PUT_BUYING) {
                decision.make(TradingType.SHORT_SELLING, "MA cross");
            } else if (currStatus == TradingType.EMPTY) {
                decision.make(TradingType.SHORT_SELLING, "empty");
            }
        } else {
            if (currStatus == TradingType.PUT_BUYING) {
                decision.make(TradingType.EMPTY, "MA cross");
            }
        }
    }

    protected void EmptyUtil(double ma1, double ma2, Decision decision, TradingType currStatus, double thres) {
        if (Math.abs(ma1 - ma2) >= thres && currStatus != TradingType.EMPTY) {
            decision.make(TradingType.EMPTY, "MA cross");
        }
    }

    protected void PutBuyingByThreshold(double ma1, double ma2, Decision decision, TradingType currStatus) {
        PutBuyingUtil(ma1, ma2, decision, currStatus, Setting.TRADE_THRESHOLD);
    }

    protected void ShortSellingByThreshold(double ma1, double ma2, Decision decision, TradingType currStatus) {
        ShortSellingUtil(ma1, ma2, decision, currStatus, Setting.TRADE_THRESHOLD);
    }

    protected void EmptyByThreshold(double ma1, double ma2, Decision decision, TradingType currStatus) {
        EmptyUtil(ma1, ma2, decision, currStatus, Setting.TRADE_THRESHOLD);
    }

    protected void PutBuying(double ma1, double ma2, Decision decision, TradingType currStatus) {
        PutBuyingUtil(ma1, ma2, decision, currStatus, 0);
    }

    protected void ShortSelling(double ma1, double ma2, Decision decision, TradingType currStatus) {
        ShortSellingUtil(ma1, ma2, decision, currStatus, 0);
    }

    protected void Empty(double ma1, double ma2, Decision decision, TradingType currStatus) {
        EmptyUtil(ma1, ma2, decision, currStatus, 0);
    }
}
