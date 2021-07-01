package com.regrx.trade.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {

    private static Calendar getBaseTime(Date baseTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseTime);
        return calendar;
    }

    // n must be in [1, 2, 3, 4, 5, 6, 10, 15, 30]
    public static long getNextMillisEveryNMinutes(Date baseTime, int n) {
        Calendar calendar = getBaseTime(baseTime);
        int minute = calendar.get(Calendar.MINUTE);
        if (minute < 60 - n) {
            int add = minute%n == 0 ? n : n - minute%n;
            calendar.add(Calendar.MINUTE,add);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return calendar.getTime().getTime();
        }

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date endTime = DateUtils.addHours(calendar.getTime(), 1);
        return endTime.getTime();
    }

    // n must be in [1, 2, 3, 4, 5, 6, 10, 15, 30]
    public static long getLastMillisEveryNMinutes(Date baseTime, int n) {
        Calendar calendar = getBaseTime(baseTime);
        int minute = calendar.get(Calendar.MINUTE);
        int target = minute - minute%n;
        calendar.set(Calendar.MINUTE, target);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().getTime();
    }


    public static String getFormattedTime(Date date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    public static Date getDateFromString(String dateString) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getClosetMinute(Date date) {
        long time = date.getTime();
        long rest = time % 60000;
        if(rest > 30000) {
            return new Date(time + (60000 - rest));
        } else {
            return new Date(time - rest);
        }
    }
}
