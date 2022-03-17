package com.regrx.trade.file;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.MinutesData;
import com.regrx.trade.data.PriceData;
import com.regrx.trade.data.Status;
import com.regrx.trade.util.Time;

import java.io.*;
import java.util.List;


public class CsvReader {
    public static MinutesData readFromCsv(String type, int interval) {
        MinutesData records = new MinutesData(interval);
        try (BufferedReader br = new BufferedReader(new FileReader("Minute_" + type + "_" + interval + ".csv"))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                PriceData newPrice = new PriceData(Double.parseDouble(values[1]), Time.getDateFromString(values[0]));
                records.update(newPrice, type, false);
                count++;
            }
            System.out.println("Success, " + count + " price record(s) loaded.");
            return records;
        } catch (FileNotFoundException e) {
            System.out.println("No such file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static Status readTradeHistory(String filename) {
        Status status = new Status();
        List<String> logs = Utils.readLastLine(new File(filename + ".log"), 1);
        if(logs.size() == 0) {
            return status;
        }
        String[] lastHistory = logs.get(0).split(" ");
        switch (lastHistory[lastHistory.length - 1]) {
            case "Empty": status.setStatus(Constant.EMPTY); break;
            case "Buying": status.setStatus(Constant.PUT_BUYING); break;
            case "Selling": status.setStatus(Constant.SHORT_SELLING); break;
            case "Both": status.setStatus(Constant.BOTH); break;
        }

        // load interval and last trade price
        status.setInterval(Integer.parseInt(lastHistory[11]));
        status.setLastTradePrice(Double.parseDouble(lastHistory[9]));

        System.out.println("Success, trading status loaded as " + status + "\n");
        return status;
    }
}
