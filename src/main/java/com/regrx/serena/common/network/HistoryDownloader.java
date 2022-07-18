package com.regrx.serena.common.network;

import com.regrx.serena.common.Setting;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static com.regrx.serena.common.utils.FileUtil.readLastLine;

public class HistoryDownloader {

    // open/highest/lowest/close(from website) -> open/close/highest/lowest(local csv data)
    public static MinutesData getHistoryData(String type, IntervalEnum interval, FutureType breed) {
        HistoryData[] historyData = fetchHistoryData(type, interval, breed, false);
        MinutesData records = new MinutesData(interval);
        for (HistoryData data : historyData) {
            ExPrice newData = new ExPrice();
            newData.setPrice(data.getClosePrice());
            newData.setTime(data.getDate());
            records.updateWithoutWrite(newData);
        }
        LogUtil.getInstance().info("Success load history data (" + interval + " min), last record time is " + records.getNewRecordTime());
        return records;
    }

    public static MinutesData getHistoryDataForSpecialInterval(String type, IntervalEnum interval, FutureType breed) {
        HistoryData[] historyData = fetchHistoryData(type, IntervalEnum.MIN_1, breed, true);
        MinutesData records = new MinutesData(interval);
        int firstRecordMinute;
        try {
            Calendar currTime = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat(Setting.TIME_PATTERN);
            currTime.setTime(dateFormat.parse(historyData[0].getDate()));
            firstRecordMinute = currTime.get(Calendar.MINUTE);
        } catch (ParseException e) {
            LogUtil.getInstance().severe("Fail to parse date string in history data array");
            return records;
        }
        for (int i = firstRecordMinute % interval.getValue(); i < historyData.length; i += interval.getValue()) {
            ExPrice newData = new ExPrice();
            newData.setPrice(historyData[i].getClosePrice());
            newData.setTime(historyData[i].getDate());
            records.updateWithoutWrite(newData);
        }
        LogUtil.getInstance().info("Success load history data (" + interval + " min), last record time is " + records.getNewRecordTime());
        return records;
    }

    private static HistoryData[] fetchHistoryData(String type, IntervalEnum interval, FutureType breed, boolean isSpecial) {
        String urlString = "https://stock2.finance.sina.com.cn/futures/api/jsonp.php" +
                "/var%20list=" +
                "/InnerFuturesNewService.getFewMinLine?" +
                "symbol=" + type + "&" +
                "type=" + interval.getValue();
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
        if(!isSpecial) {
            writeHistoryDataToCsv(historyData, "History_" + type + "_" + interval.getValue());
        }

        if(PreparationUtil.isTrading(breed) && breed == FutureType.STOCK) {
            historyData = Arrays.copyOf(historyData, historyData.length - 1);
        }
        return historyData;
    }

    private static void writeHistoryDataToCsv(HistoryData[] historyData, String filename) {
        List<String> lastRecords = readLastLine(new File(filename + ".csv"), 1);
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
