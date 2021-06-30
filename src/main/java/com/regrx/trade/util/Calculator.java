package com.regrx.trade.util;


import java.util.List;

public class Calculator {
    public static double avg(List<Double> list) {
        return list.stream().reduce(0.0, Double::sum) / list.size();
    }
}
