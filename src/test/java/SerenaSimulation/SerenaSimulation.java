package SerenaSimulation;

import SerenaSimulation.profit.ParaCombination;
import SerenaSimulation.profit.ProfitCal;
import SerenaSimulation.profit.TestResult;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.PriorityQueue;

public class SerenaSimulation {
    public static String type = "IC2212";

    public static void main(String[] args) {
        runner();
//        simulation();
    }


    // IH2212 2205-2207: [15, 13, 17, 0.5] 250.83
    // IC2212 2205-2207: [17, 25, 23, 0]   930.65
    // IF2212 2205-2207: [23, 15, 19, 0]   428.35
    public static void runner() {
//        double[] lossLimitPara = {11.0, 13.0, 15.0, 17.0, 19.0, 21.0, 23.0};
//        double[] profitLimitPara = {11.0, 13.0, 15.0, 17.0, 19.0, 21.0, 23.0};
//        double[] restorePara = {11.0, 13.0, 15.0, 17.0, 19.0, 21.0, 23.0};
//        double[] MA520Para = {0.0, 0.3, 0.5, 0.8, 1.0};
        double[] lossLimitPara = {23.0};
        double[] profitLimitPara = {35.0};
        double[] restorePara = {5.0};
        double[] MA520Para = {0.0};
        double[] fillPara = {10.0};

        PriorityQueue<ParaCombination> queue = new PriorityQueue<>(10, Collections.reverseOrder());
        for (double lossLimit : lossLimitPara) {
            for (double profitLimit : profitLimitPara) {
                for (double restore : restorePara) {
                    for (double ma520 : MA520Para) {
                        for (double fill : fillPara) {
                            Setting.LOSS_LIMIT_THRESHOLD = lossLimit;
                            Setting.PROFIT_LIMIT_THRESHOLD = profitLimit;
                            Setting.RESTORE_THRESHOLD = restore;
                            Setting.TRADE_THRESHOLD = ma520;
                            Setting.FILL_GAP_THRESHOLD = fill;
                            simulation();
                            try {
                                Thread.sleep(500);
                                ControllerTest.stop();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            ParaCombination newRes = new ParaCombination();
                            newRes.setProfit(ProfitCal.cal(type));
                            newRes.setParaArray(lossLimit, profitLimit, restore, ma520, fill);
                            queue.add(newRes);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        ArrayList<ParaCombination> resList = new ArrayList<>();
        System.out.println("\nTop 10 Best Parameters: ");
        System.out.println("Total Profit\tParameters\t\t\t\t\t\t\tPut P\tPut L\tShort P\tShort L\tTotal\tCont. Loss\tOccurred");
        for (int i = 0; i < 10; i++) {
            ParaCombination candidate = queue.poll();
            if (candidate != null) {
                resList.add(candidate);
            }
        }
        for (ParaCombination res : resList) {
            System.out.println(res);
        }
        outputToCsv(resList, type + '_' + Calendar.getInstance().getTime().getTime());
    }

    public static void simulation() {
        String type = SerenaSimulation.type;

        clearTradeHistory(type);

        ControllerTest controller = ControllerTest.getInstance(type);

//        controller.addDataTrack(IntervalEnum.MIN_1);
//        controller.addDataTrack(IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_LOSS_LIMIT, IntervalEnum.MIN_2);
//        controller.addStrategy(StrategyEnum.STRATEGY_PROFIT_LIMIT, IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_MA_520, IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_FILL_GAP, IntervalEnum.MIN_2);
//        controller.addStrategy(StrategyEnum.STRATEGY_CLOSE_ON_END, IntervalEnum.NULL);

        controller.run();
    }

    private static void clearTradeHistory(String type) {
        File tradeHistory = new File("Trade_" + type + ".log");
        try {
            Files.deleteIfExists(tradeHistory.toPath());
        } catch (IOException ignored) {
        }
    }

    private static void outputToCsv(ArrayList<ParaCombination> resList, String filename) {
        FileUtil.newFile(filename + ".csv");
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {
            writer.append("Total Profit, LL Thres, PL Thres, Restore, MA, Put P, Put L, Short P, Short L, Total").append('\n');
            for (ParaCombination res : resList) {
                TestResult testResult = res.profit;
                writer.append(String.format("%.2f", testResult.getTotalProfit())).append(',');
                for (double para : res.paraArray) {
                    writer.append(String.format("%.2f", para)).append(',');
                }
                writer.append(String.valueOf(testResult.getPutProfit())).append(',');
                writer.append(String.valueOf(testResult.getPutLoss())).append(',');
                writer.append(String.valueOf(testResult.getShortProfit())).append(',');
                writer.append(String.valueOf(testResult.getShortLoss())).append(',');
                writer.append(String.valueOf(testResult.getTotalCount())).append(',');
                writer.append(String.valueOf(testResult.getContinousLoss())).append(',');
                writer.append(String.valueOf(testResult.getCLTime())).append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
