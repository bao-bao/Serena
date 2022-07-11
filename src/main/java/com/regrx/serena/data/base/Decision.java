package com.regrx.serena.data.base;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;

public class Decision {
    private boolean execute;
    private TradingType tradingType;
    private StrategyEnum source;
    private IntervalEnum interval;
    private String reason;
    private ExPrice price;

    public Decision() {
        this.execute = false;
        this.tradingType = TradingType.NO_ACTION;
        this.source = StrategyEnum.STRATEGY_NULL;
        this.interval = IntervalEnum.NULL;
        this.reason = "";
        this.price = new ExPrice();
    }

    public Decision(ExPrice price, StrategyEnum source, IntervalEnum interval) {
        this.tradingType = TradingType.NO_ACTION;
        this.source = source;
        this.interval = interval;
        this.reason = "";
        this.price = price;
    }

    public void copy(Decision origin) {
        this.setExecute(origin.execute);
        this.setTradingType(origin.tradingType);
        this.setPrice(origin.price);
        this.setSource(origin.source);
        this.setReason(origin.getReason());
        this.setInterval(origin.getInterval());
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public TradingType getTradingType() {
        return tradingType;
    }

    public void setTradingType(TradingType tradingType) {
        this.tradingType = tradingType;
    }

    public StrategyEnum getSource() {
        return source;
    }

    public void setSource(StrategyEnum source) {
        this.source = source;
    }

    public IntervalEnum getInterval() {
        return interval;
    }

    public void setInterval(IntervalEnum interval) {
        this.interval = interval;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ExPrice getPrice() {
        return price;
    }

    public void setPrice(ExPrice price) {
        this.price = price;
    }

    public void make(TradingType trade, String reason) {
        this.execute = true;
        this.tradingType = trade;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "[" + execute + ", " + tradingType + ", " + source + ", " + reason + ", " + price + "]";
    }
}
