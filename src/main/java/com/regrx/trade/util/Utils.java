package com.regrx.trade.util;

import com.regrx.trade.statistic.MovingAverage;

import java.util.LinkedList;

public class Utils {

    public static boolean TradeIntervalGreaterThan(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2, int limit) {
        int iterator = FindLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        int rightCross = iterator - 1;
        int leftCross = FindLastCross(movingAverages, ind_1, ind_2, rightCross);
        return rightCross - leftCross > limit;
    }

    public static boolean LastCrossDiffGreaterThan(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2, double limit) {
        int iterator = FindLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        double crossPoint1 = EvalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
        iterator = FindLastCross(movingAverages, ind_1, ind_2, iterator - 1);
        double crossPoint2 = EvalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
        return Math.abs(crossPoint1 - crossPoint2) > limit;
    }

    public static double EvalLastCrossPrice(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2) {
        int iterator = FindLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        return EvalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
    }

    public static int FindLastCross(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2, int from) {
        MovingAverage ma1 = movingAverages.get(from);
        MovingAverage ma2 = movingAverages.get(from - 1);
        int res = from;
        while((ma1.getMAByIndex(ind_1) - ma1.getMAByIndex(ind_2)) * (ma2.getMAByIndex(ind_1) - ma2.getMAByIndex(ind_2)) > 0) {
            res -= 1;
            ma1 = movingAverages.get(res);
            ma2 = movingAverages.get(res - 1);
        }
        return res;
    }

    public static double EvalCrossPrice(MovingAverage ma1, MovingAverage ma2, int ind_1, int ind_2) {
        double value1_1 = ma1.getMAByIndex(ind_1);
        double value1_2 = ma1.getMAByIndex(ind_2);
        double value2_1 = ma2.getMAByIndex(ind_1);
        double value2_2 = ma2.getMAByIndex(ind_2);
        if((value1_1 - value1_2) * (value2_1 - value2_2) < 0) {
            return (value1_1 + value1_2 + value2_1 + value2_2) / 4;
        }
        System.out.println("No cross exist");
        return -1;
    }
}
