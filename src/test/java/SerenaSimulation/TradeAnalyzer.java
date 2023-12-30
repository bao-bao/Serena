package SerenaSimulation;

import SerenaSimulation.profit.ProfitCal;
import SerenaSimulation.profit.TestResult;
import SerenaSimulation.profit.WindowResult;
import com.regrx.serena.common.utils.FileUtil;

import java.io.FileWriter;
import java.io.IOException;

public class TradeAnalyzer {
    public static void main(String[] args) {
        String type = "IF99991";
        String path = "test_result/IF99991_123";   // use "." to analyze real trade log
        TestResult result = ProfitCal.cal(path, type, true);
        graphicDataOutput(result, path, type);
    }

    public static void graphicDataOutput(TestResult result, String path, String type) {
        String filename = path + "/" + type + "_graphic_data";
        FileUtil.newFile(filename + ".csv");
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {
            for (WindowResult res : result.getProfitByMonth()) {
                writer.append(res.getName()).append(",");
            }
            writer.append("\n");
            for (WindowResult res : result.getProfitByMonth()) {
                writer.append(String.format("%.2f", res.getProfit())).append(",");
            }
            writer.append("\n");
            for (WindowResult res : result.getProfitByMonth()) {
                writer.append(String.format("%d", res.getCount())).append(",");
            }
            writer.append("\n");
            for (WindowResult res : result.getProfitByWeek()) {
                writer.append(res.getName()).append(",");
            }
            writer.append("\n");
            for (WindowResult res : result.getProfitByWeek()) {
                writer.append(String.format("%.2f", res.getProfit())).append(",");
            }
            writer.append("\n");
            for (WindowResult res : result.getProfitByWeek()) {
                writer.append(String.format("%d", res.getCount())).append(",");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
