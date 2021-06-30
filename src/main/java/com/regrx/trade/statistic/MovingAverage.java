package com.regrx.trade.statistic;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.util.Calculator;

import java.util.LinkedList;

public class MovingAverage {
    private Double MA5;
    private Double MA10;
    private Double MA20;
    private Double MA30;
    private Double MA60;
    private Double MA90;
    private Double MA120;
    private Double MA250;

    public MovingAverage() {
        this.MA5 = 0.0;
        this.MA10 = 0.0;
        this.MA20 = 0.0;
        this.MA30 = 0.0;
        this.MA60 = 0.0;
        this.MA90 = 0.0;
        this.MA120 = 0.0;
        this.MA250 = 0.0;
    }

    public Double getMA5() {
        return MA5;
    }

    public void setMA5(Double MA5) {
        this.MA5 = MA5;
    }

    public Double getMA10() {
        return MA10;
    }

    public void setMA10(Double MA10) {
        this.MA10 = MA10;
    }

    public Double getMA20() {
        return MA20;
    }

    public void setMA20(Double MA20) {
        this.MA20 = MA20;
    }

    public Double getMA30() {
        return MA30;
    }

    public void setMA30(Double MA30) {
        this.MA30 = MA30;
    }

    public Double getMA60() {
        return MA60;
    }

    public void setMA60(Double MA60) {
        this.MA60 = MA60;
    }

    public Double getMA90() {
        return MA90;
    }

    public void setMA90(Double MA90) {
        this.MA90 = MA90;
    }

    public Double getMA120() {
        return MA120;
    }

    public void setMA120(Double MA120) {
        this.MA120 = MA120;
    }

    public Double getMA250() {
        return MA250;
    }

    public void setMA250(Double MA250) {
        this.MA250 = MA250;
    }

    public Double getMAByIndex(int index) {
        switch (index) {
            case Constant.MA5: return MA5;
            case Constant.MA10: return MA10;
            case Constant.MA20: return MA20;
            case Constant.MA30: return MA30;
            case Constant.MA60: return MA60;
            case Constant.MA90: return MA90;
            case Constant.MA120: return MA120;
            case Constant.MA250: return MA250;
        }
        return null;
    }

    public MovingAverage(LinkedList<Double> prices) {
        this.MA5 = 0.0;
        this.MA10 = 0.0;
        this.MA20 = 0.0;
        this.MA30 = 0.0;
        this.MA60 = 0.0;
        this.MA90 = 0.0;
        this.MA120 = 0.0;
        this.MA250 = 0.0;

        int size = prices.size();
        if(prices.size() >= 250) {
            this.MA250 = Calculator.avg(prices.subList(size - 250, size));
        }
        if(prices.size() >= 120) {
            this.MA120 = Calculator.avg(prices.subList(size - 120, size));
        }
        if(prices.size() >= 90) {
            this.MA90 = Calculator.avg(prices.subList(size - 90, size));
        }
        if(prices.size() >= 60) {
            this.MA60 = Calculator.avg(prices.subList(size - 60, size));
        }
        if(prices.size() >= 30) {
            this.MA30 = Calculator.avg(prices.subList(size - 30, size));
        }
        if(prices.size() >= 20) {
            this.MA20 = Calculator.avg(prices.subList(size - 20, size));
        }
        if(prices.size() >= 10) {
            this.MA10 = Calculator.avg(prices.subList(size - 10, size));
        }
        if(prices.size() >= 5) {
            this.MA5 = Calculator.avg(prices.subList(size - 5, size));
        }
    }
}
