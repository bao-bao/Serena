package SerenaSimulation;

import SerenaSimulation.profit.ProfitCal;
import SerenaSimulation.profit.TestResult;
import com.regrx.serena.common.utils.FileUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.io.FileWriter;
import java.io.IOException;

public class TradeAnalyzer {
    public static void main(String[] args) {
        String type = "IC2212";
        String path = "test_result/IC2212_1699164314192";   // use "." to analyze real trade log
        TestResult result = ProfitCal.cal(path, type, true);
        graphicDataOutput(result, path, type);
    }

    public static void graphicDataOutput(TestResult result, String path, String type) {
        String filename = path + "/" + type + "_graphic_data";
        FileUtil.newFile(filename + ".csv");
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {
            for (Pair<String, Double> res : result.getProfitByMonth()) {
                writer.append(res.getLeft()).append(",");
            }
            writer.append("\n");
            for (Pair<String, Double> res : result.getProfitByMonth()) {
                writer.append(String.format("%.2f", res.getRight())).append(",");
            }
            writer.append("\n");
            for (Pair<String, Double> res : result.getProfitByWeek()) {
                writer.append(res.getLeft()).append(",");
            }
            writer.append("\n");
            for (Pair<String, Double> res : result.getProfitByWeek()) {
                writer.append(String.format("%.2f", res.getRight())).append(",");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
