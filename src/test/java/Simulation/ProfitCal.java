package Simulation;


import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ProfitCal {
    public static double cal(String filename) {
        int status = 0;
        double emptyPrice, tradeInPrice = 0.0, profit = 0.0, lineCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File("Trade_" + filename + ".log"), StandardCharsets.UTF_8))) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                lineCount++;
                String[] lastHistory = line.split(" ");
                double currPrice = Double.parseDouble(lastHistory[9]);
                switch (lastHistory[lastHistory.length - 1]) {
                    case "Empty":
                        emptyPrice = currPrice;
                        profit += status == 1 ? (emptyPrice - tradeInPrice) : (tradeInPrice - emptyPrice);
                        status = 0;
                    break;
                    case "PutBuying":
                        status = 1;
                        tradeInPrice = currPrice;
                    break;
                    case "ShortSelling":
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

        System.out.println("Profit: " + profit);
        System.out.println("Final Status: " + (status == 1 ? "PutBuying" : status == 2 ? "ShortSelling" : "Empty"));

        return profit;
    }
}
