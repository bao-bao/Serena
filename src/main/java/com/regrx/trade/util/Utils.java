package com.regrx.trade.util;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.statistic.MovingAverage;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

public class Utils {

    public static boolean tradeIntervalGreaterThan(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2, int limit) {
        int iterator = findLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        int rightCross = iterator - 1;
        int leftCross = findLastCross(movingAverages, ind_1, ind_2, rightCross);
        return rightCross - leftCross > limit;
    }

    public static boolean lastCrossDiffGreaterThan(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2, double limit) {
        int iterator = findLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        double crossPoint1 = evalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
        iterator = findLastCross(movingAverages, ind_1, ind_2, iterator - 1);
        double crossPoint2 = evalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
        return Math.abs(crossPoint1 - crossPoint2) > limit;
    }

    public static double EvalLastCrossPrice(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2) {
        int iterator = findLastCross(movingAverages, ind_1, ind_2, movingAverages.size() - 1);
        return evalCrossPrice(movingAverages.get(iterator), movingAverages.get(iterator - 1), ind_1, ind_2);
    }

    public static int findLastCross(LinkedList<MovingAverage> movingAverages, int ind_1, int ind_2, int from) {
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

    public static double evalCrossPrice(MovingAverage ma1, MovingAverage ma2, int ind_1, int ind_2) {
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

    public static boolean fiveMinutesLeft(int breed) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if(breed == Constant.STOCK) {
            return ((hour == 14 && minute >= 54) || hour > 14); // 14:54:59 - 23:59:59
        }
        return false;
    }

    public static boolean isTrading(int breed) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if(breed == Constant.STOCK) {
            return hour >= 9 && (hour != 9 || minute >= 30) && (hour != 11 || minute <= 30)     // (9:30 - 11:30)
                    && hour != 12 && hour <= 14;                                                // (13:00 - 15:00)
        } else {
            final boolean dayTimeRange =
                    hour >= 9 && (hour != 10 || minute <= 15 || minute >= 30)   // (9:00 - 10:15)
                    && (hour != 11 || minute <= 30) && hour != 12               // (10:30 - 11:30)
                    && (hour != 13 || minute >= 30) && hour <= 14;              // (13:30 - 15:00)
            if (breed == Constant.FUTURE_NO_NIGHT) {
                return dayTimeRange;
            } else if (breed == Constant.FUTURE_NIGHT_2300) {
                return dayTimeRange || (hour >= 21 && hour <= 22);                                  // (21:00 - 23:00)
            } else if (breed == Constant.FUTURE_NIGHT_0100) {
                return dayTimeRange || (hour >= 21 || hour == 0);                                   // (21:00 - 1:00)
            } else if (breed == Constant.FUTURE_NIGHT_0230) {
                return dayTimeRange || (hour >= 21 || hour <= 1 || (hour == 2 && minute <= 30));    // (21:00 - 2:30)
            } else {
                System.out.println("Error Breed Detected");
                return false;
            }
        }
    }

    public static int getBreed(String type) {
        if(type.startsWith("IC") || type.startsWith("IF") || type.startsWith("IH")) {
            return Constant.STOCK;
        } else if (type.startsWith("AU") || type.startsWith("AG") || type.startsWith("SC")) {
            return Constant.FUTURE_NIGHT_0230;
        } else if (type.startsWith("CU") || type.startsWith("PB") || type.startsWith("AL")
                || type.startsWith("ZN") || type.startsWith("SN") || type.startsWith("NI")
                || type.startsWith("SS") || type.startsWith("BC")) {
            return Constant.FUTURE_NIGHT_0100;
        } else if (type.startsWith("SM") || type.startsWith("SF") || type.startsWith("WH")
                || type.startsWith("JR") || type.startsWith("LR") || type.startsWith("PM")
                || type.startsWith("RI") || type.startsWith("RS") || type.startsWith("PK")
                || type.startsWith("UR") || type.startsWith("CJ") || type.startsWith("AP")
                || type.startsWith("BB") || type.startsWith("FB") || type.startsWith("LH")
                || type.startsWith("JD") || type.startsWith("WR")) {
            return Constant.FUTURE_NO_NIGHT;
        } else if (type.startsWith("FG") || type.startsWith("SA") || type.startsWith("MA")
                || type.startsWith("SR") || type.startsWith("TA") || type.startsWith("RM")
                || type.startsWith("OI") || type.startsWith("CF") || type.startsWith("CY")
                || type.startsWith("PF") || type.startsWith("ZC") || type.startsWith("JM")
                || type.startsWith("CS") || type.startsWith("PP") || type.startsWith("EB")
                || type.startsWith("EG") || type.startsWith("PG") || type.startsWith("RR")
                || type.startsWith("FU") || type.startsWith("RU") || type.startsWith("BU")
                || type.startsWith("SP") || type.startsWith("RB") || type.startsWith("HC")
                || type.startsWith("LU") || type.startsWith("NR")) {
            return Constant.FUTURE_NIGHT_2300;
        }
        System.out.println("Error breed");
        return -1;
    }
}
