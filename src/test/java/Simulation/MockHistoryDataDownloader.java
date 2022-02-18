package Simulation;

import com.google.gson.Gson;
import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.HistoryData;
import com.regrx.trade.data.MinutesData;
import com.regrx.trade.data.PriceData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class MockHistoryDataDownloader {
    public static MinutesData getHistoryData(String filename, int interval, int size) {

        MinutesData records = new MinutesData(interval);
        LinkedList<PriceData> priceData = Util.CsvReader.readPriceFromCsv(filename);

        for(int i = 0; i < size; i = i + interval) {
            records.update(priceData.get(i), filename, false);
        }

        System.out.println("Success");
        System.out.println("Last record time is " + records.getCurrentTime().toString() + "\n");
        return records;
    }
}
