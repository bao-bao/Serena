package com.regrx.serena.strategy;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.service.DataServiceManager;

public abstract class AbstractStrategy implements Comparable<AbstractStrategy> {
    protected String name;
    protected IntervalEnum interval;
    protected Integer priority;
    protected final DataServiceManager dataSvcMgr = DataServiceManager.getInstance();

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
}
