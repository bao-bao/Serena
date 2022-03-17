package com.regrx.trade.file;

import com.regrx.trade.data.MinutesData;
import com.regrx.trade.statistic.MovingAverage;
import com.regrx.trade.util.Time;

import java.io.*;

public class CsvWriter {

    public static void newFile(String filename) {
        try (PrintWriter writer = new PrintWriter(filename)) {
            System.out.println("new file \"" + filename + "\" compete!");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void write(String filename, MinutesData data) {
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {

            String formattedDate = Time.getFormattedTime(data.getCurrentTime());
            writer.append(formattedDate).append(',');
            writer.append(data.getLastPrice().toString()).append(',');

            MovingAverage movingAverage = data.getLastMovingAverage();
            writer.append(String.format("%.2f", movingAverage.getMA5())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA10())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA20())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA30())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA60())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA90())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA120())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA250())).append('\n');

            writer.flush();
        } catch (FileNotFoundException e) {
            CsvWriter.newFile(filename + ".csv");
            write(filename + ".csv", data);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void writeTradeHistory(String filename, String history) {
        try (FileWriter writer = new FileWriter(filename + ".log", true)) {
            writer.append(history).append('\n');
            writer.flush();
        } catch (FileNotFoundException e) {
            CsvWriter.newFile(filename + ".log");
            writeTradeHistory(filename, history);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
