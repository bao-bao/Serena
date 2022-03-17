package com.regrx.serena.common.utils;


import com.regrx.serena.data.base.ExPrice;

import java.util.List;

public class Calculator {
    public static double avg(List<ExPrice> list) {
        return list.stream().reduce(new ExPrice(0.0), ExPrice::add).getPrice() / list.size();
    }
}
