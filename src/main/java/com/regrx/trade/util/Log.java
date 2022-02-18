package com.regrx.trade.util;

import com.regrx.trade.data.PriceData;
import com.regrx.trade.file.CsvWriter;

public class Log {
    private static String tradeLog(PriceData priceData, int interval, String reason) {
        return "at " + priceData.getDate() +
                " for " + priceData.getPrice() +
                " under " + interval + " minute data" +
                ", Reason: " + reason +
                ", Current: ";
    }

    public static void emptyLog(PriceData priceData, int interval, String reason, String type) {
        CsvWriter.writeTradeHistory("Trade_" + type, "Close " + tradeLog(priceData, interval, reason) + "Empty");
    }

    public static void putBuyingLog(PriceData priceData, int interval, String reason, String type) {
        CsvWriter.writeTradeHistory("Trade_" + type, "PutBuying " + tradeLog(priceData, interval, reason) + "PutBuying");
    }

    public static void shortSellingLog(PriceData priceData, int interval, String reason, String type) {
        CsvWriter.writeTradeHistory("Trade_" + type, "ShortSelling " + tradeLog(priceData, interval, reason) + "ShortSelling");
    }
}
