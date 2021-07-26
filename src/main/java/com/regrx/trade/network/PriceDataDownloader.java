package com.regrx.trade.network;

import com.regrx.trade.data.PriceData;
import com.regrx.trade.util.Time;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceDataDownloader {

    public static PriceData getPriceDataForStockFutures(String urlString) {
        PriceData newPrice = new PriceData();
        String originString = Util.downloadFromGZIPFormat(urlString);
        if (originString == null) {
            return newPrice;
        }
        String finalString = originString.substring(
                StringUtils.ordinalIndexOf(originString, ",", 3) + 1,
                StringUtils.ordinalIndexOf(originString, ",", 4));
        String timeString = originString.substring(
                StringUtils.ordinalIndexOf(originString, ",", 36) + 1,
                StringUtils.ordinalIndexOf(originString, ",", 38));
        String pattern = "yyyy-MM-dd,HH:mm:ss";

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date date = simpleDateFormat.parse(timeString);
            newPrice.setDate(Time.getClosetMinute(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return newPrice;
        }
        newPrice.setPrice(Double.parseDouble(finalString));
        return newPrice;
    }

    public static PriceData getPriceDataForOtherFutures(String urlString) {
        PriceData newPrice = new PriceData();
        String originString = Util.downloadFromGZIPFormat(urlString);
        if (originString == null) {
            return newPrice;
        }
        String finalString = originString.substring(
                StringUtils.ordinalIndexOf(originString, ",", 8) + 1,
                StringUtils.ordinalIndexOf(originString, ",", 9));
        String dateString = originString.substring(
                StringUtils.ordinalIndexOf(originString, ",", 17) + 1,
                StringUtils.ordinalIndexOf(originString, ",", 18));
        String timeString = originString.substring(
                StringUtils.ordinalIndexOf(originString, ",", 1) + 1,
                StringUtils.ordinalIndexOf(originString, ",", 2));
        String pattern = "yyyy-MM-dd HHmmss";

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date date = simpleDateFormat.parse(dateString + " " + timeString);
            newPrice.setDate(Time.getClosetMinute(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return newPrice;
        }
        newPrice.setPrice(Double.parseDouble(finalString));
        return newPrice;
    }
}
