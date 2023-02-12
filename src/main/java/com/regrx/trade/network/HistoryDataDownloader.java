package com.regrx.trade.network;

import com.google.gson.*;
import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.HistoryData;
import com.regrx.trade.data.MinutesData;
import com.regrx.trade.data.PriceData;
import com.regrx.trade.file.CsvWriter;
import com.regrx.trade.statistic.MovingAverage;
import com.regrx.trade.util.Time;
import com.regrx.trade.util.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HistoryDataDownloader {
    public static MinutesData getHistoryData(String type, Integer interval, int breed) {
        String urlString = "https://stock2.finance.sina.com.cn/futures/api/jsonp.php" +
                "/var=" +
                "/InnerFuturesNewService.getFewMinLine?" +
                "symbol=" + type + "&" +
                "type=" + interval.toString();
        MinutesData records = new MinutesData(interval);
        String originString = Util.downloadFromGZIPFormat(urlString, 3);
        if (originString == null || StringUtils.ordinalIndexOf(originString, "(null)", 1) != -1) {
            System.out.println("Read Error");
            System.exit(Constant.DOWNLOAD_ERROR_CODE);
            return records;
        }
        String jsonString = originString.substring(
                StringUtils.ordinalIndexOf(originString, "[", 1),
                StringUtils.ordinalIndexOf(originString, "]", 1) + 1);

        Gson gson = new Gson();
        HistoryData[] historyData = gson.fromJson(jsonString, HistoryData[].class);
        writeHistoryDataToCsv(historyData, "History_" + type + "_" + interval);

        if(Utils.isTrading(breed) && breed == Constant.STOCK) {
            historyData = Arrays.copyOf(historyData, historyData.length - 1);
        }

        String pattern = "yyyy-MM-dd HH:mm:ss";
        for (HistoryData data : historyData) {
            PriceData newData = new PriceData();
            newData.setPrice(data.getClosePrice());
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                newData.setDate(simpleDateFormat.parse(data.getDate()));
            } catch (ParseException e) {
                System.exit(Constant.PARSE_ERROR_CODE);
                e.printStackTrace();
            }
            records.update(newData, type, false);
        }
        System.out.println("Success");
        System.out.println("Last record time is " + records.getCurrentTime().toString() + "\n");
        return records;
    }

    public static void writeHistoryDataToCsv(HistoryData[] historyData, String filename) {
        List<String> lastRecords = com.regrx.trade.file.Utils.readLastLine(new File(filename + ".csv"), 1);
        String lastRecordDateStr = null;
        if(lastRecords.size() > 0) {
            lastRecordDateStr = lastRecords.get(0).split(",")[0];
        }

        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {
            for(HistoryData data : historyData) {
                if(lastRecordDateStr == null || data.getDate().compareTo(lastRecordDateStr) > 0) {
                    writer.append(data.getDate()).append(",");
                    writer.append(String.format("%.2f", data.getOpenPrice())).append(',');
                    writer.append(String.format("%.2f", data.getClosePrice())).append(',');
                    writer.append(String.format("%.2f", data.getHighestPrice())).append(',');
                    writer.append(String.format("%.2f", data.getLowestPrice())).append(',');
                    writer.append(String.valueOf(data.getVolume())).append(',');
                    writer.append(String.valueOf(data.getP())).append('\n');
                }
            }
            writer.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
