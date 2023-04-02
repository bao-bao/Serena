package com.regrx.serena.data.statistic;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.EMAEnum;
import com.regrx.serena.common.utils.Calculator;
import com.regrx.serena.data.base.ExPrice;

import java.util.*;

public class ExpMovingAverage {
    private static class ExpMovingAverageBasic {
        double alpha;
        double currentEMA;
        LinkedList<Double> historyEMA;

        ExpMovingAverageBasic(double alpha) {
            this.alpha = alpha;
            currentEMA = 0;
            historyEMA = new LinkedList<>();
        }

        ExpMovingAverageBasic(List<ExPrice> list, double alpha) {
            List<ExPrice> reversed = new ArrayList<>(list);
            historyEMA = new LinkedList<>();
            Collections.reverse(reversed);
            currentEMA = Calculator.expAvg(reversed, historyEMA, alpha);
        }

        void update(ExPrice newPrice) {
            currentEMA = ((1-alpha) * currentEMA) + (alpha * newPrice.getPrice());
            if(historyEMA.size() >= Setting.MAX_LENGTH) {
                historyEMA.removeLast();
            }
            historyEMA.addFirst(currentEMA);
        }

        public double getCurrentEMA() {
            if(historyEMA.size() < (2 / alpha - 1)) {
               return 0.0;
            }
            if (Setting.USE_INJECT_HISTORY) {
                return currentEMA;
            } else {
                return currentEMA / (1 - Math.pow((1-alpha), historyEMA.size() + 1)); // calibration
            }
        }

        public double getHistoryEMA(int past) {
            if(past < historyEMA.size() && historyEMA.size() >= (2 / alpha - 1)) {
                return historyEMA.get(past);
            } else {
                return 0.0;
            }
        }

        public double getAlpha() {
            return alpha;
        }

        public int getSize() {
            return historyEMA.size();
        }
    }


    private final List<ExpMovingAverageBasic> EMA;

    public ExpMovingAverage() {
        EMA = new ArrayList<>();
        for (double alpha : Setting.EMA_ALPHA) {
            EMA.add(new ExpMovingAverageBasic(2 / (alpha + 1)));
        }
    }

    public void update(ExPrice newPrice) {
        for(ExpMovingAverageBasic ema : EMA) {
            ema.update(newPrice);
        }
    }

    public void addEMA(List<ExPrice> prices, double alphas) {
        EMA.add(new ExpMovingAverageBasic(prices, alphas));
    }

    public int getSize() {
        if (EMA.size() == 0) {
            return 0;
        }
        return EMA.get(0).getSize();
    }

    public double getCurrentEMAByEnum(EMAEnum index) {
        if (index == EMAEnum.EMA_NULL) {
            return 0.0;
        }
        return EMA.get(index.getValue()).getCurrentEMA();
    }

    public double getHistoryEMAByEnum(EMAEnum index, int past) {
        if (index == EMAEnum.EMA_NULL) {
            return 0.0;
        }
        return  EMA.get(index.getValue()).getHistoryEMA(past);
    }

    public ArrayList<Double> getAllCurrentEMA() {
        ArrayList<Double> res = new ArrayList<>();
        for (ExpMovingAverageBasic basic : EMA) {
            res.add(basic.getCurrentEMA());
        }
        return res;
    }

    public ArrayList<Double> getAllPastEMA(int past) {
        ArrayList<Double> res = new ArrayList<>();
        for (ExpMovingAverageBasic basic : EMA) {
            res.add(basic.getHistoryEMA(past));
        }
        return res;
    }

    public int findLastCrossIndex(EMAEnum ema1, EMAEnum ema2) {
        int res = 0;
        double historyOnLine1 = getHistoryEMAByEnum(ema1, res);
        double historyOnLine2 = getHistoryEMAByEnum(ema2, res + 1);
        while(res < EMA.get(0).getSize() - 2 && ((historyOnLine1 - historyOnLine2) * (historyOnLine2 - historyOnLine1) > 0)) {
            res++;
            historyOnLine1 = historyOnLine2;
            historyOnLine2 = getHistoryEMAByEnum(ema2, res + 1);
        }
        if (((historyOnLine1 - historyOnLine2) * (historyOnLine2 - historyOnLine1) > 0)) {
            return -1;
        }
        return res;
    }
}