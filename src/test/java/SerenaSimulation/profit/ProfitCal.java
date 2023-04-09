package SerenaSimulation.profit;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.PriorityQueue;

public class ProfitCal {
    public static TestResult cal(String filename, boolean outputDetail) {
        TestResult testResult = new TestResult();
        int status = 0;
        double emptyPrice, tradeInPrice = 0.0, profit = 0.0, lineCount = 0;
        int putProfitCount = 0, putLossCount = 0, shortProfitCount = 0, shortLossCount = 0, count = 0;

        int profitCount = 0, lossCount = 0;
        double totalProfit = 0.0, totalLoss = 0.0;

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
                        if (status == 1 && st.profit >= 0) {
                            putProfitCount++;
                            profitCount++;
                        } else if (status == 1 && st.profit < 0) {
                            putLossCount++;
                            lossCount++;
                        } else if (status == 2 && st.profit >= 0) {
                            shortProfitCount++;
                            profitCount++;
                        } else {
                            shortLossCount++;
                            lossCount++;
                        }
                        if (st.profit >= 0) {

                            totalProfit += st.profit;
                        } else {
                            totalLoss += -1 * st.profit;
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

        profit = profit - (lineCount / 2 * 3.0);

        System.out.println("Profit: " + String.format("%.2f", profit));

        if (outputDetail) {
            System.out.println("Final Status: " + (status == 1 ? "PutBuying" : status == 2 ? "ShortSelling" : "Empty"));
            System.out.println("Profit Count: " + (putProfitCount + shortProfitCount) + "\t" + "Loss Count: " + (putLossCount + shortLossCount));

            ArrayList<Double> profitList = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                SingleTrade trade = trades.poll();
                if (trade != null) {
                    profitList.add(trade.profit);
                    System.out.println(trade.openTime + "," + trade.closeTime + "," +
                            trade.tradeType + "," + String.format("%.2f", trade.profit) +
                            ", openReason: " + trade.openReason + ", closeReason: " + trade.closeReason);
                }
            }

            System.out.println("\n\t\tPut\tShort");
            System.out.println("Loss\t" + putLossCount + "\t" + shortLossCount);
            System.out.println("Profit\t" + putProfitCount + "\t" + shortProfitCount);

            double averageProfit = (totalProfit / profitCount);
            double averageLoss = (totalLoss / lossCount);
            System.out.println();
            System.out.println("APPT: " + String.format("%.2f", averageProfit - averageLoss));

            int totalCount = profitCount + lossCount;
            double winRate = (double) profitCount / (double) totalCount;
            double lossRate = (double) lossCount / (double) totalCount;
            System.out.println("EVPT: " + String.format("%.2f", (winRate * averageProfit) - (lossRate * averageLoss)));

            double odds = averageProfit / averageLoss;
            System.out.println("Odds: " + String.format("%.2f", odds));
            System.out.println("Kelly: " + String.format("%.2f", (((winRate * (odds + 1)) - 1) / odds) * 100) + "%");

            double risk = averageLoss;
            System.out.println("EVUR: " + String.format("%.2f", (winRate * averageProfit * odds / risk) - (lossRate * averageLoss / risk)));

            double maxLoss = findMaxLoss(profitList);
            System.out.println("Max Loss: " +  String.format("%.2f", maxLoss));
        }
        testResult.setTotalProfit(profit);
        testResult.setPutProfit(putProfitCount);
        testResult.setShortProfit(shortProfitCount);
        testResult.setPutLoss(putLossCount);
        testResult.setShortLoss(shortLossCount);
        return testResult;
    }

    private static double findMaxLoss(ArrayList<Double> list) {
        double[][] dp = new double[list.size()][list.size()];
        double res = 0.0;
        for (int i = 0; i < dp.length; i++) {
            dp[0][i] = list.get(i);
        }
        for (int i = 1; i < dp.length; i++) {
            for (int j = i; j < dp.length; j++) {
                dp[i][j] = dp[i - 1][j - 1] + list.get(j);
                res = Math.min(res, dp[i][j]);
            }
        }
        return res;
    }
}
