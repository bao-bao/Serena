package SerenaSimulation.profit;


import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.PriorityQueue;

public class ProfitCal {
    public static TestResult cal(String filename) {
        TestResult testResult = new TestResult();
        int status = 0;
        double emptyPrice, tradeInPrice = 0.0, profit = 0.0, lineCount = 0;
        int putProfitCount = 0, putLossCount = 0, shortProfitCount = 0, shortLossCount = 0, count = 0;
        PriorityQueue<SingleTrade> trades = new PriorityQueue<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("Trade_" + filename + ".log"), StandardCharsets.UTF_8))) {
            String line;
            SingleTrade st = new SingleTrade("");
            while ((line = reader.readLine()) != null) {
                lineCount++;
                String[] lastHistory = line.split(" ");
                String reason = line.split(",")[1].substring(9);
                double currPrice = Double.parseDouble(lastHistory[5]);
                switch (lastHistory[lastHistory.length - 1]) {
                    case "Empty":
                        emptyPrice = currPrice;
                        st.setCloseTime(lastHistory[2] + " " + lastHistory[3]);
                        st.setProfit(status == 1 ? (emptyPrice - tradeInPrice) : (tradeInPrice - emptyPrice));
                        st.setCloseReason(reason);
                        count++;
                        if(status == 1 && st.profit >= 0) {
                            putProfitCount++;
                        } else if(status == 1 && st.profit < 0) {
                            putLossCount++;
                        } else if(status == 2 && st.profit >= 0) {
                            shortProfitCount++;
                        } else {
                            shortLossCount++;
                        }
                        trades.add(st);
                        profit += st.profit;
                        status = 0;
                        break;
                    case "PutBuying":
                        st = new SingleTrade(lastHistory[2] + " " + lastHistory[3]);
                        st.setTradeType("Put");
                        st.setOpenReason(reason);
                        status = 1;
                        tradeInPrice = currPrice;
                        break;
                    case "ShortSelling":
                        st = new SingleTrade(lastHistory[2] + " " + lastHistory[3]);
                        st.setTradeType("Short");
                        st.setOpenReason(reason);
                        status = 2;
                        tradeInPrice = currPrice;
                        break;
                }

            }
        } catch (FileNotFoundException e) {
            System.out.println("No such file.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        profit = profit - (lineCount / 2 * 1.1);

        System.out.println("Profit: " + String.format("%.2f", profit));
        System.out.println("Final Status: " + (status == 1 ? "PutBuying" : status == 2 ? "ShortSelling" : "Empty"));
        System.out.println("Profit Count: " + (putProfitCount + shortProfitCount) + "\t" + "Loss Count: " + (putLossCount + shortLossCount));

        for (int i = 0; i < count; i++) {
            SingleTrade trade = trades.poll();
            if (trade != null) {
                System.out.println(trade.openTime + "," + trade.closeTime + "," +
                        trade.tradeType + "," + String.format("%.2f", trade.profit) +
                        ", openReason: " + trade.openReason + ", closeReason: " + trade.closeReason);
            }
        }

        System.out.println("\t\tPut\tShort");
        System.out.println("Loss\t" + putLossCount + "\t" + shortLossCount);
        System.out.println("Profit\t" + putProfitCount + "\t" + shortProfitCount);

        testResult.setTotalProfit(profit);
        testResult.setPutProfit(putProfitCount);
        testResult.setShortProfit(shortProfitCount);
        testResult.setPutLoss(putLossCount);
        testResult.setShortLoss(shortLossCount);
        return testResult;
    }
}
