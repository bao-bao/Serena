package com.regrx.serena.data.statistic;

import com.regrx.serena.common.constant.MAEnum;
import com.regrx.serena.common.utils.Calculator;
import com.regrx.serena.data.base.ExPrice;

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

    public Double getMAByIndex(MAEnum index) {
        switch (index) {
            case MA5: return MA5;
            case MA10: return MA10;
            case MA20: return MA20;
            case MA30: return MA30;
            case MA60: return MA60;
            case MA90: return MA90;
            case MA120: return MA120;
            case MA250: return MA250;
        }
        return null;
    }

    public MovingAverage(LinkedList<ExPrice> prices) {
        this.MA5 = 0.0;
        this.MA10 = 0.0;
        this.MA20 = 0.0;
        this.MA30 = 0.0;
        this.MA60 = 0.0;
        this.MA90 = 0.0;
        this.MA120 = 0.0;
        this.MA250 = 0.0;

        if(prices.size() >= 250) {
            this.MA250 = Calculator.avg(prices.subList(0, 250));
        }
        if(prices.size() >= 120) {
            this.MA120 = Calculator.avg(prices.subList(0, 120));
        }
        if(prices.size() >= 90) {
            this.MA90 = Calculator.avg(prices.subList(0, 90));
        }
        if(prices.size() >= 60) {
            this.MA60 = Calculator.avg(prices.subList(0, 60));
        }
        if(prices.size() >= 30) {
            this.MA30 = Calculator.avg(prices.subList(0, 30));
        }
        if(prices.size() >= 20) {
            this.MA20 = Calculator.avg(prices.subList(0, 20));
        }
        if(prices.size() >= 10) {
            this.MA10 = Calculator.avg(prices.subList(0, 10));
        }
        if(prices.size() >= 5) {
            this.MA5 = Calculator.avg(prices.subList(0, 5));
        }
    }

    @Deprecated
    public static boolean tradeIntervalGreaterThan(LinkedList<MovingAverage> movingAverages, MAEnum ind_1, MAEnum ind_2, int limit) {
        int iterator = findLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        int rightCross = iterator - 1;
        int leftCross = findLastCross(movingAverages, ind_1, ind_2, rightCross);
        return rightCross - leftCross > limit;
    }

    @Deprecated
    public static boolean lastCrossDiffGreaterThan(LinkedList<MovingAverage> movingAverages, MAEnum ind_1, MAEnum ind_2, double limit) {
        int iterator = findLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        double crossPoint1 = evalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
        iterator = findLastCross(movingAverages, ind_1, ind_2, iterator - 1);
        double crossPoint2 = evalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
        return Math.abs(crossPoint1 - crossPoint2) > limit;
    }

    public static double evalLastCrossPrice(LinkedList<MovingAverage> movingAverages, MAEnum ind_1, MAEnum ind_2) {
        int iterator = findLastCross(movingAverages, ind_1, ind_2, 0);
        if(iterator == 0) {
            return 0;
        }
        return evalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator + 1), ind_1, ind_2);
    }

    public static int findLastCross(LinkedList<MovingAverage> movingAverages, MAEnum ind_1, MAEnum ind_2, int from) {
        if(from == movingAverages.size() - 1 || movingAverages.size() < 2) {
            return 0;
        }
        MovingAverage ma1 = movingAverages.get(0);
        MovingAverage ma2 = movingAverages.get(1);
        int res = 0;
        while(res < movingAverages.size() - 2 && (ma1.getMAByIndex(ind_1) - ma1.getMAByIndex(ind_2)) * (ma2.getMAByIndex(ind_1) - ma2.getMAByIndex(ind_2)) > 0) {
            res++;
            ma1 = movingAverages.get(res);
            ma2 = movingAverages.get(res + 1);
        }
        return res;
    }

    public static double evalCrossPrice(MovingAverage ma1, MovingAverage ma2, MAEnum ind_1, MAEnum ind_2) {
        double value1_1 = ma1.getMAByIndex(ind_1);
        double value1_2 = ma1.getMAByIndex(ind_2);
        double value2_1 = ma2.getMAByIndex(ind_1);
        double value2_2 = ma2.getMAByIndex(ind_2);
        if((value1_1 - value1_2) * (value2_1 - value2_2) < 0) {
            return (value1_1 + value1_2 + value2_1 + value2_2) / 4;
        }
        return 0;
    }

}
