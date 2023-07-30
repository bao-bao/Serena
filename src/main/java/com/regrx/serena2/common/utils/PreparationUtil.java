package com.regrx.serena2.common.utils;

import com.regrx.serena2.common.constant.ErrorType;
import com.regrx.serena2.common.constant.FutureType;

import java.util.Calendar;
import java.util.TimeZone;

public class PreparationUtil {

    public static boolean fiveMinutesLeft(FutureType breed, int hour, int minute) {

        boolean dayTime = ((hour == 14 && minute >= 55) || hour == 15);     // 14:55:00 - 15:59:59

        if(breed == FutureType.STOCK || breed == FutureType.FUTURE_NO_NIGHT) {
            return dayTime;
        } else if (breed == FutureType.FUTURE_NIGHT_2300) {
            return dayTime || ((hour == 22 && minute >= 55) || hour == 23); // daytime || 22:54:00 - 23:59:59
        } else if (breed == FutureType.FUTURE_NIGHT_0100) {
            return dayTime || ((hour == 0 && minute >= 55) || hour == 1);   // daytime || 00:54:00 - 01:59:59
        } else if (breed == FutureType.FUTURE_NIGHT_0230) {
            return dayTime || ((hour == 2 && minute >= 25));                // daytime || 02:24:00 - 02:59:59
        }
        return false;
    }

    public static boolean isTrading(FutureType breed) {
        if(breed == FutureType.ERROR_BREED) {
            LogUtil.getInstance().severe("NO such breed index " + breed);
            System.exit(ErrorType.BREED_ERROR_CODE.getCode());
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY) {
            return false;
        }

        if(breed == FutureType.STOCK) {
            return hour >= 9 && (hour != 9 || minute >= 30) && (hour != 11 || minute < 30)     // (9:30 - 11:30)
                    && hour != 12 && hour <= 14;                                                // (13:00 - 15:00)
        } else {
            final boolean dayTimeRange =
                    hour >= 9 && (hour != 10 || minute < 15 || minute >= 30)   // (9:00 - 10:15)
                            && (hour != 11 || minute < 30) && hour != 12               // (10:30 - 11:30)
                            && (hour != 13 || minute >= 30) && hour <= 14;              // (13:30 - 15:00)
            if (breed == FutureType.FUTURE_NO_NIGHT) {
                return dayTimeRange;
            } else if (breed == FutureType.FUTURE_NIGHT_2300) {
                return dayTimeRange || (hour >= 21 && hour <= 22);                                  // (21:00 - 23:00)
            } else if (breed == FutureType.FUTURE_NIGHT_0100) {
                return dayTimeRange || (hour >= 21 || hour == 0);                                   // (21:00 - 1:00)
            } else if (breed == FutureType.FUTURE_NIGHT_0230) {
                return dayTimeRange || (hour >= 21 || hour <= 1 || (hour == 2 && minute < 30));    // (21:00 - 2:30)
            } else {
                return false;
            }
        }
    }

    public static FutureType getBreed(String type) {
        if(type.startsWith("IC") || type.startsWith("IF") || type.startsWith("IH") || type.startsWith("IM")) {
            return FutureType.STOCK;
        } else if (type.startsWith("AU") || type.startsWith("AG") || type.startsWith("SC")) {
            return FutureType.FUTURE_NIGHT_0230;
        } else if (type.startsWith("CU") || type.startsWith("PB") || type.startsWith("AL")
                || type.startsWith("ZN") || type.startsWith("SN") || type.startsWith("NI")
                || type.startsWith("SS") || type.startsWith("BC")) {
            return FutureType.FUTURE_NIGHT_0100;
        } else if (type.startsWith("SM") || type.startsWith("SF") || type.startsWith("WH")
                || type.startsWith("JR") || type.startsWith("LR") || type.startsWith("PM")
                || type.startsWith("RI") || type.startsWith("RS") || type.startsWith("PK")
                || type.startsWith("UR") || type.startsWith("CJ") || type.startsWith("AP")
                || type.startsWith("BB") || type.startsWith("FB") || type.startsWith("LH")
                || type.startsWith("JD") || type.startsWith("WR")) {
            return FutureType.FUTURE_NO_NIGHT;
        } else if (type.startsWith("FG") || type.startsWith("SA") || type.startsWith("MA")
                || type.startsWith("SR") || type.startsWith("TA") || type.startsWith("RM")
                || type.startsWith("OI") || type.startsWith("CF") || type.startsWith("CY")
                || type.startsWith("PF") || type.startsWith("ZC") || type.startsWith("JM")
                || type.startsWith("CS") || type.startsWith("PP") || type.startsWith("EB")
                || type.startsWith("EG") || type.startsWith("PG") || type.startsWith("RR")
                || type.startsWith("FU") || type.startsWith("RU") || type.startsWith("BU")
                || type.startsWith("SP") || type.startsWith("RB") || type.startsWith("HC")
                || type.startsWith("LU") || type.startsWith("NR") || type.startsWith("SI")) {
            return FutureType.FUTURE_NIGHT_2300;
        }
        LogUtil.getInstance().severe("NO such breed called " + type);
        System.exit(ErrorType.BREED_ERROR_CODE.getCode());
        return FutureType.ERROR_BREED;
    }
}
