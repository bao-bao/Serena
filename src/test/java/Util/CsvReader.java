package Util;

import com.regrx.trade.data.PriceData;
import com.regrx.trade.util.Time;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class CsvReader {
    public static LinkedList<PriceData> readPriceFromCsv(String type, int interval) {
        LinkedList<PriceData> priceData = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("Minute_" + type + "_" + String.valueOf(interval) + ".csv"))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                PriceData newPrice = new PriceData(Double.parseDouble(values[1]), Time.getDateFromString(values[0].trim()));
                priceData.add(newPrice);
                count++;
            }
            System.out.println("Success, " + count + " price record(s) loaded.");
            return priceData;
        } catch (FileNotFoundException e) {
            System.out.println("No such file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return priceData;
    }

    public static LinkedList<PriceData> readPriceFromCsv(String filename) {
        LinkedList<PriceData> priceData = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename + ".csv"))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                PriceData newPrice = new PriceData(Double.parseDouble(values[1]), Time.getDateFromString(values[0].trim()));
                priceData.add(newPrice);
                count++;
            }
            System.out.println("Success, " + count + " price record(s) loaded.");
            return priceData;
        } catch (FileNotFoundException e) {
            System.out.println("No such file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return priceData;
    }
}
