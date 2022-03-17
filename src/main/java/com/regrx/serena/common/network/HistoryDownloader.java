package com.regrx.serena.common.network;

import com.regrx.serena.common.constant.ErrorType;
import com.regrx.serena.common.constant.FutureType;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.PreparationUtil;
import com.regrx.serena.data.HistoryData;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.ExPrice;

import org.apache.commons.lang3.StringUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class HistoryDownloader {

    public static MinutesData getHistoryData(String type, IntervalEnum interval, FutureType breed) {
        String urlString = "https://stock2.finance.sina.com.cn/futures/api/jsonp.php" +
                "/var%20list=" +
                "/InnerFuturesNewService.getFewMinLine?" +
                "symbol=" + type + "&" +
                "type=" + interval.getValue();
        MinutesData records = new MinutesData(interval);
        String originString = GZIPDownloader.download(urlString, type, 3);
        if (StringUtils.ordinalIndexOf(originString, "(null)", 1) != -1) {
            LogUtil.getInstance().severe("Wrong content fetched! Check url availability!");
            System.exit(ErrorType.DOWNLOAD_ERROR_CODE.getCode());
        }
        String jsonString = originString.substring(
                StringUtils.ordinalIndexOf(originString, "[", 1),
                StringUtils.ordinalIndexOf(originString, "]", 1) + 1);

        Gson gson = new Gson();
        HistoryData[] historyData = gson.fromJson(jsonString, HistoryData[].class);
        writeHistoryDataToCsv(historyData, "History_" + type + "_" + interval.getValue());

        if(PreparationUtil.isTrading(breed) && breed == FutureType.STOCK) {
            historyData = Arrays.copyOf(historyData, historyData.length - 1);
        }

        for (HistoryData data : historyData) {
            ExPrice newData = new ExPrice();
            newData.setPrice(data.getClosePrice());
            newData.setTime(data.getDate());
            records.updateWithoutWrite(newData);
        }
        LogUtil.getInstance().info("Success load history data, last record time is " + records.getNewRecordTime());
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
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename + ".csv\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());

        }
    }
}
