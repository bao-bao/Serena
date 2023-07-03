package com.regrx.serena2.common.utils;


import com.regrx.serena2.data.base.ExPrice;

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
        for(ExPrice price : list) {
            eAvg = ((1-alpha) * eAvg) + (alpha * price.getPrice());
            history.addFirst(eAvg);
        }
        return eAvg;
    }
}
