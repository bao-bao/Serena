package com.regrx.serena.common.utils;

import com.regrx.serena.common.constant.ErrorType;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.common.constant.TrendType;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;
import com.regrx.serena.data.statistic.MovingAverage;

import com.regrx.serena.service.DataServiceManager;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class FileUtil {

    public static MinutesData readMinutesDataFromCsv(String type, IntervalEnum interval, String prefix) {
        MinutesData records = new MinutesData(interval);
        String filename = prefix + "_" + type + "_" + interval.getValue() + ".csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int count = 0;

            while ((line = br.readLine()) != null && !line.equals("")) {
                String[] values = line.split(",");
                ExPrice newPrice = new ExPrice(Double.parseDouble(values[1]), values[0]);
                records.updateWithoutWrite(newPrice);
                count++;
            }
            LogUtil.getInstance().info("Success loaded " + count + " price record(s)");
            return records;
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename + "\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
        return records;
    }

    public static void readTradeHistory(String filename) {
        Status status = Status.getInstance();
        List<String> logs = readLastLine(new File(filename + ".log"), 1);
        if (logs.size() == 0) {
            return;
        }
        String[] lastHistory = logs.get(0).split(" ");
        switch (lastHistory[lastHistory.length - 1]) {
            case "Empty":
                status.setStatus(TradingType.EMPTY);
                break;
            case "Long":
                status.setStatus(TradingType.PUT_BUYING);
                break;
            case "Short":
                status.setStatus(TradingType.SHORT_SELLING);
                break;
        }

        // load interval and last trade price
        status.setInterval(IntervalEnum.fromInt(Integer.parseInt(lastHistory[7])));
        status.setLastTradePrice(Double.parseDouble(lastHistory[5]));

        LogUtil.getInstance().info("Trading status loaded as " + status);
    }

    public static List<String> readLastLine(File file, int numLastLineToRead) {
        List<String> result = new ArrayList<>();

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null && result.size() < numLastLineToRead) {
                if (!line.equals("")) {
                    result.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            //LogUtil.getInstance().warning("No trade history named " + file.getName() + ", file will be created soon");
            newFile(file.getName());
        } catch (IOException e) {
            //LogUtil.getInstance().severe("Error occurred when opening file \"" + file.getName() + "\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
        return result;

    }

    public static void newFile(String filename) {
        try (PrintWriter ignored = new PrintWriter(filename)) {
            //LogUtil.getInstance().info("new file \"" + filename + "\" compete!");
        } catch (FileNotFoundException e) {
            //LogUtil.getInstance().severe("Error occurred when creating file \"" + filename + "\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
    }

    public static void writeMinutesDataToCsv(String filename, MinutesData data) {
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {

            String timeString = data.getLastRecordTime();
            writer.append(timeString).append(',');
            writer.append(String.format("%.2f", data.getNewPrice())).append(',');

            MovingAverage movingAverage = data.getLastMAvg();
            writer.append(String.format("%.2f", movingAverage.getMA5())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA10())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA20())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA30())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA60())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA90())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA120())).append(',');
            writer.append(String.format("%.2f", movingAverage.getMA240())).append('\n');

            writer.flush();
        } catch (FileNotFoundException e) {
            newFile(filename + ".csv");
            writeMinutesDataToCsv(filename + ".csv", data);
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename + ".csv\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
    }

    public static void writeTradeHistory(String filename, String history) {
        try (FileWriter writer = new FileWriter(filename + ".log", true)) {
            writer.append(history).append('\n');
            writer.flush();
        } catch (FileNotFoundException e) {
            newFile(filename + ".log");
            writeTradeHistory(filename, history);
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename + ".log\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
    }

    public static TrendType loadLastTradeInTrend() {
        TrendType res = TrendType.NULL;
        String filename = "Trade_" + Controller.getInstance().getType() + ".log";
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(filename), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("MA cross")) {
                    String[] lastHistory = line.split(" ");
                    if (lastHistory[lastHistory.length - 1].equals(TradingType.EMPTY.toString())) {
                        line = reader.readLine();
                        if (line != null) {
                            lastHistory = line.split(" ");
                            if (lastHistory[lastHistory.length - 1].equals(TradingType.PUT_BUYING.toString())) {
                                res = TrendType.TREND_DOWN;
                            } else if (lastHistory[lastHistory.length - 1].equals(TradingType.SHORT_SELLING.toString())) {
                                res = TrendType.TREND_UP;
                            }
                        }
                    } else {
                        if (lastHistory[lastHistory.length - 1].equals(TradingType.PUT_BUYING.toString())) {
                            res = TrendType.TREND_UP;
                        } else if (lastHistory[lastHistory.length - 1].equals(TradingType.SHORT_SELLING.toString())) {
                            res = TrendType.TREND_DOWN;
                        }
                    }
                    break;
                } else if (line.contains("empty")) {
                    String[] lastHistory = line.split(" ");
                    if (lastHistory[lastHistory.length - 1].equals(TradingType.PUT_BUYING.toString())) {
                        res = TrendType.TREND_UP;
                    } else if (lastHistory[lastHistory.length - 1].equals(TradingType.SHORT_SELLING.toString())) {
                        res = TrendType.TREND_DOWN;
                    }
                    break;
                }
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename + "\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
        return res;
    }

    public static void emaLog(String type, IntervalEnum interval, boolean isForUp, boolean isActive, double price, String time) {
        String filename = "EMA_" + type + "_" + interval.getValue() + ".log";
        try (FileWriter writer = new FileWriter(filename, true)) {
            writer.append(time).append("--");
            writer.append(isForUp ? "UP" : "DOWN").append("--");
            writer.append(Boolean.toString(isActive)).append("--");
            writer.append(String.format("%.2f", price)).append('\n');
            writer.flush();
        } catch (FileNotFoundException e) {
            newFile(filename);
            emaLog(type, interval, isForUp, isActive, price, time);
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename);
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
    }

    public static double[] readEmaLog(String type, IntervalEnum interval, boolean isForUp) {
        double lastPrice = 0.0;
        double profitMax = 0.0;
        double currPrice = 0.0;
        long crossTime = Calendar.getInstance().getTime().getTime();

        String filename = "EMA_" + type + "_" + interval.getValue() + ".log";
        List<String> revLog = readLastLine(new File(filename), Integer.MAX_VALUE);
        for (String line : revLog) {
            String[] parts = line.split("--");
            if (isForUp && Objects.equals(parts[1], "UP")) {
                if (Boolean.parseBoolean(parts[2])) {
                    lastPrice = Double.parseDouble(parts[3]);
                    crossTime = TimeUtil.getDateFromString(parts[0]).getTime();
                }
                break;
            }
            if (!isForUp && Objects.equals(parts[1], "DOWN")) {
                if (Boolean.parseBoolean(parts[2])) {
                    lastPrice = Double.parseDouble(parts[3]);
                    crossTime = TimeUtil.getDateFromString(parts[0]).getTime();
                }
                break;
            }
        }
        if (lastPrice == 0.0) {
            return new double[]{lastPrice, 0.0, 0.0};
        }

        for(ExPrice price : DataServiceManager.getInstance().queryData(interval).getPrices()) {
            if(TimeUtil.getDateFromString(price.getTime()).getTime() > crossTime) {
                currPrice = price.getPrice();
                if(isForUp) {
                    profitMax = Math.max(profitMax, price.getPrice() - lastPrice);
                } else {
                    profitMax = Math.max(profitMax, lastPrice - price.getPrice());
                }
            }
        }

        Status status = Status.getInstance();
        if(isForUp && status.getTrendEMA() == TrendType.NULL) {
            status.setTrendEMA(TrendType.TREND_UP);
        } else if(isForUp && status.getTrendEMA() == TrendType.TREND_DOWN) {
            status.setTrendEMA(TrendType.TREND_BOTH);
        } else if(!isForUp && status.getTrendEMA() == TrendType.NULL) {
            status.setTrendEMA(TrendType.TREND_DOWN);
        } else if(!isForUp && status.getTrendEMA() == TrendType.TREND_UP) {
            status.setTrendEMA(TrendType.TREND_BOTH);
        }

        return new double[]{lastPrice, currPrice, profitMax};
    }
}
