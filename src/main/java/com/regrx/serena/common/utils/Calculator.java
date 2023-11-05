package com.regrx.serena.common.utils;


import com.regrx.serena.data.base.ExPrice;

import java.util.LinkedList;
import java.util.List;

public class Calculator {
    public static double avg(List<ExPrice> list) {
        return list.stream().reduce(new ExPrice(0.0), ExPrice::add).getPrice() / list.size();
    }

    public static double expAvg(List<ExPrice> list, LinkedList<Double> history, double alpha) {
        if (alpha > 1 || alpha <= 0) {
            LogUtil.getInstance().warning("Alpha should be located in (0, 1], using normal avg instead...");
            return avg(list);
        }
        double eAvg = 0;
        for (ExPrice price : list) {
            eAvg = ((1 - alpha) * eAvg) + (alpha * price.getPrice());
            history.addFirst(eAvg);
        }
        return eAvg;
    }

    public static double avgUtil(List<Double> list) {
        return list.stream().reduce(0.0, Double::sum) / list.size();
    }

    public static double squareDeviation(List<Double> list) {
        if (list.isEmpty() || list.size() == 1) {
            return 0;
        }
        double avg = avgUtil(list);
        double squareDeviation = 0;
        for (Double val : list) {
            squareDeviation += Math.pow(val - avg, 2);
        }
        squareDeviation = squareDeviation / (list.size() - 1);
        return squareDeviation;
    }

    public static double standardDeviation(List<Double> list) {
        return Math.sqrt(squareDeviation(list));
    }
}
