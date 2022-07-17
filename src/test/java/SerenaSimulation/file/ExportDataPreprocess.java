package SerenaSimulation.file;

import com.regrx.serena.common.constant.ErrorType;
import com.regrx.serena.common.utils.LogUtil;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ExportDataPreprocess {

    public static void main(String[] args) {
        String originalFile = "testdata\\stock\\IC2209.csv";
        String type = "IC2209";
        int interval = 1;
        minuteData(originalFile, "History_" + type + "_" + interval + ".csv", interval);
//        dailyData(originalFile, "History_" + type + "_day.csv");
    }

    // for close/highest/lowest/open input
    public static void dailyData(String filename, String newFilename) {
        ArrayList<String> lines = new ArrayList<>();
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(filename), StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(newFilename, true)) {
            for(String line : lines) {
                String[] data = line.split(",");
                writer.append(data[0]).append(",");
                writer.append(String.format("%.2f", Double.valueOf(data[6]))).append(',');
                writer.append(String.format("%.2f", Double.valueOf(data[3]))).append(',');
                writer.append(String.format("%.2f", Double.valueOf(data[4]))).append(',');
                writer.append(String.format("%.2f", Double.valueOf(data[5]))).append(',');
                writer.append(data[10]).append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename + ".csv\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
    }

    // for open/highest/lowest/close input
    public static void minuteData(String filename, String newFilename, int interval) {
        ArrayList<String> lines = new ArrayList<>();
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
//                if(interval == 1 && count++ % 121 == 0) continue; // skip the 1st and 122nd record daily.
                if(interval == 1 && count++ % 241 == 0) continue; // skip 1st daily
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String currTime = "09:30:00";
        try (FileWriter writer = new FileWriter(newFilename, true)) {
            for(String line : lines) {
                String[] data = line.split(",");
                currTime = getTimeString(currTime, interval);
                writer.append(data[0].split("\"")[1]).append(" ").append(currTime).append(",");
                writer.append(String.format("%.2f", Double.valueOf(data[2]))).append(',');
                writer.append(String.format("%.2f", Double.valueOf(data[5]))).append(',');
                writer.append(String.format("%.2f", Double.valueOf(data[3]))).append(',');
                writer.append(String.format("%.2f", Double.valueOf(data[4]))).append(',');
                writer.append(data[data.length - 1]).append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            LogUtil.getInstance().severe("Error occurred when opening file \"" + filename + ".csv\"");
            System.exit(ErrorType.IO_ERROR_CODE.getCode());
        }
    }

    public static String getTimeString(String prev, int interval) {
        if(prev.equals("15:00:00")) {
            return "09:3" + interval + ":00";
        }
        if(prev.equals("11:30:00")) {
            return "13:0" + interval + ":00";
        }
        String[] arr = prev.split(":");
        String hour = arr[0];
        String minute = arr[1];
        int nextMin = (Integer.parseInt(minute) + interval) % 60;
        int nextHour = nextMin == 0 ? Integer.parseInt(hour) + 1 : Integer.parseInt(hour);
        StringBuilder sb = new StringBuilder();
        if(nextHour < 10) {
            sb.append("0");
        }
        sb.append(nextHour).append(":");
        if(nextMin < 10) {
            sb.append("0");
        }
        sb.append(nextMin).append(":").append("00");
        return sb.toString();
    }
}
