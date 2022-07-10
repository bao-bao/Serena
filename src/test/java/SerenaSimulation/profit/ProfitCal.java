package SerenaSimulation.profit;


import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.PriorityQueue;

public class ProfitCal {
    public static double cal(String filename) {
        int status = 0;
        double emptyPrice, tradeInPrice = 0.0, profit = 0.0, lineCount = 0;
        int posiCount = 0, negeCount = 0;
        PriorityQueue<SingleTrade> worstTrade = new PriorityQueue<>();
        PriorityQueue<SingleTrade> bestTrade = new PriorityQueue<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("Trade_" + filename + ".log"), StandardCharsets.UTF_8))) {
            String line = "";
            SingleTrade st = new SingleTrade("");
            while ((line = reader.readLine()) != null) {
                lineCount++;
                String[] lastHistory = line.split(" ");
                String reason = line.split(",")[1].substring(7);
                double currPrice = Double.parseDouble(lastHistory[5]);
                switch (lastHistory[lastHistory.length - 1]) {
                    case "Empty":
                        emptyPrice = currPrice;
                        st.setCloseTime(lastHistory[2] + " " + lastHistory[3]);
                        st.setProfit(status == 1 ? (emptyPrice - tradeInPrice) : (tradeInPrice - emptyPrice));
                        st.setCloseReason(reason);
//                        if (st.profit >= 0) {
//                            posiCount++;
//                            bestTrade.add(st);
//                        } else {
//                            negeCount++;
//                            worstTrade.add(st);
//                        }
                        posiCount++;
                        bestTrade.add(st);
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
        System.out.println("Profit Count: " + posiCount + "\t" + "Loss Count: " + negeCount);

        int putLoss = 0, shortLoss = 0, putProfit = 0, shortProfit = 0;

//        System.out.println("\nWorst Top 5: ");
//        for (int i = 0; i < negeCount; i++) {
//            SingleTrade worst = worstTrade.poll();
//            if (worst != null) {
//                System.out.println(worst.openTime + "," + worst.closeTime + "," + worst.tradeType + "," + String.format("%.2f", worst.profit));
//                if(worst.tradeType.equals("Short")) {
//                    shortLoss++;
//                } else {
//                    putLoss++;
//                }
//            }
//        }
//        System.out.println("\nBest Top 5: ");
//        for (int i = 0; i < posiCount; i++) {
//            SingleTrade best = bestTrade.poll();
//            if (best != null) {
//                System.out.println(best.openTime + "," + best.closeTime + "," + best.tradeType + "," + String.format("%.2f", best.profit));
//                if(best.tradeType.equals("Short")) {
//                    shortProfit++;
//                } else {
//                    putProfit++;
//                }
//            }
//        }

        for (int i = 0; i < posiCount; i++) {
            SingleTrade best = bestTrade.poll();
            if (best != null) {
                System.out.println(best.openTime + "," + best.closeTime + "," +
                        best.tradeType + "," + String.format("%.2f", best.profit) +
                        ", openReason: " + best.openReason + ", closeReason: " + best.closeReason);
                if(best.tradeType.equals("Short")) {
                    shortProfit++;
                } else {
                    putProfit++;
                }
            }
        }

        System.out.println("\t\tPut\tShort");
        System.out.println("Loss\t" + putLoss + "\t" + shortLoss);
        System.out.println("Profit\t" + putProfit + "\t" + shortProfit);

        return profit;
    }
}
