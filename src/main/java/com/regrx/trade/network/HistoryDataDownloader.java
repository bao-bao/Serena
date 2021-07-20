package com.regrx.trade.network;

import com.google.gson.*;
import com.regrx.trade.data.HistoryData;
import com.regrx.trade.data.MinutesData;
import com.regrx.trade.data.PriceData;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HistoryDataDownloader {

    public static MinutesData getHistoryData(String type, Integer interval) {
        String urlString = "https://stock2.finance.sina.com.cn/futures/api/jsonp.php" +
                "/var%20list=" +
                "/InnerFuturesNewService.getFewMinLine?" +
                "symbol=" + type + "&" +
                "type=" + interval.toString();
        MinutesData records = new MinutesData(interval);
        String originString = Util.downloadFromGZIPFormat(urlString);
        if (originString == null || StringUtils.ordinalIndexOf(originString, "(null)", 1) != -1) {
            System.out.println("Read Error");
            return records;
        }
        String jsonString = originString.substring(
                StringUtils.ordinalIndexOf(originString, "[", 1),
                StringUtils.ordinalIndexOf(originString, "]", 1) + 1);

        Gson gson = new Gson();
        HistoryData[] historyData = gson.fromJson(jsonString, HistoryData[].class);

        String pattern = "yyyy-MM-dd HH:mm:ss";
        for (HistoryData data : historyData) {
            PriceData newData = new PriceData();
            newData.setPrice(data.getClosePrice());
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                newData.setDate(simpleDateFormat.parse(data.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            records.update(newData, false);
        }
        System.out.println("Read Success");
        System.out.println("Last record time is " + records.getCurrentTime().toString());
        return records;
    }
}
