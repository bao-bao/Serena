package com.regrx.serena2.common.network;

import com.regrx.serena2.common.Setting;
import com.regrx.serena2.data.base.ExPrice;
import com.regrx.serena2.common.constant.ErrorType;
import com.regrx.serena2.common.utils.LogUtil;
import com.regrx.serena2.common.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceDownloader {

    public static ExPrice getPriceDataForStockFutures(String urlString, String type) {
        ExPrice newPrice = new ExPrice();
        String originString = GZIPDownloader.download(urlString, type, Setting.DOWNLOAD_RETRY_COUNT);

        if(originString == null) {
            return null;
        }

        String finalString = originString.substring(
                StringUtils.ordinalIndexOf(originString, ",", 3) + 1,
                StringUtils.ordinalIndexOf(originString, ",", 4));
        String timeString = originString.substring(
                StringUtils.ordinalIndexOf(originString, ",", 36) + 1,
                StringUtils.ordinalIndexOf(originString, ",", 38));

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Setting.STOCK_FUTURE_PRICE_DATA_TIME_PATTERN);
            Date date = simpleDateFormat.parse(timeString);
            newPrice.setTime(TimeUtil.getClosetMinuteString(date));
        } catch (ParseException e) {
            LogUtil.getInstance().severe("Error occurred when parsing date string! Check date format!");
            System.exit(ErrorType.PARSE_ERROR_CODE.getCode());
        }

        newPrice.setPrice(Double.parseDouble(finalString));
        return newPrice;
    }

    public static ExPrice getPriceDataForOtherFutures(String urlString, String type) {
        ExPrice newPrice = new ExPrice();
        String originString = GZIPDownloader.download(urlString, type, Setting.DOWNLOAD_RETRY_COUNT);

        if(originString == null) {
            return null;
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


        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Setting.OTHER_FUTURE_PRICE_DATA_TIME_PATTERN);
            Date date = simpleDateFormat.parse(dateString + " " + timeString);
            newPrice.setTime(TimeUtil.getClosetMinuteString(date));
        } catch (ParseException e) {
            LogUtil.getInstance().severe("Error occurred when parsing date string! Check date format!");
            System.exit(ErrorType.PARSE_ERROR_CODE.getCode());
        }
        newPrice.setPrice(Double.parseDouble(finalString));
        return newPrice;
    }
}
