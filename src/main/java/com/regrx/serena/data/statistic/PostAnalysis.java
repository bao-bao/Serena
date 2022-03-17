package com.regrx.serena.data.statistic;

import com.regrx.serena.common.constant.ErrorType;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.ExPrice;

import java.util.LinkedList;

public class PostAnalysis {
    private double offsetMA5;
    private double offsetMA20;
    private double MA5MinusMA20;

    public PostAnalysis() {
        this.offsetMA5 = 0;
        this.offsetMA20 = 0;
        this.MA5MinusMA20 = 0;
    }

    public void update(LinkedList<ExPrice> prices, LinkedList<MovingAverage> mAvgs) {
        ExPrice currPrice = prices.peekFirst();
        MovingAverage currMAvgs = mAvgs.peekFirst();
        if(currPrice != null && currMAvgs != null) {
            this.offsetMA5 = currPrice.getPrice() - currMAvgs.getMA5();
            this.offsetMA20 = currPrice.getPrice() - currMAvgs.getMA20();
            this.MA5MinusMA20 = currMAvgs.getMA5() - currMAvgs.getMA20();
        } else {
            LogUtil.getInstance().severe("Error occurred during update post analysis");
            System.exit(ErrorType.PROCESSING_ERROR_CODE.getCode());

        }

    }

    public double getOffsetMA5() {
        return offsetMA5;
    }

    public double getOffsetMA20() {
        return offsetMA20;
    }

    public double getMA5MinusMA20() {
        return MA5MinusMA20;
    }
}
