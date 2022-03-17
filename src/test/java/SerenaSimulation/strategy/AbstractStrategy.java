package SerenaSimulation.strategy;

import SerenaSimulation.DataServiceManagerTest;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;


public abstract class AbstractStrategy implements Comparable<AbstractStrategy> {
    protected String name;
    protected IntervalEnum interval;
    protected Integer priority;
    protected final DataServiceManagerTest dataSvcMgr = DataServiceManagerTest.getInstance();

    AbstractStrategy(IntervalEnum interval, int priority) {
        this.interval = interval;
        this.priority = priority;
    }

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

    public int getPriority() {
        return priority;
    }

    public IntervalEnum getInterval() {
        return interval;
    }

    public void setInterval(IntervalEnum interval) {
        this.interval = interval;
    }

    public abstract Decision execute(ExPrice price);
}
