package SerenaSimulation;

import SerenaSimulation.profit.EMACombination;
import SerenaSimulation.profit.ParaCombination;
import SerenaSimulation.profit.ProfitCal;
import SerenaSimulation.profit.TestResult;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.FileUtil;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.PriorityQueue;

public class SerenaSimulation {
    public static String type = "IM1234";

    public static void main(String[] args) {
//        runner();
//        simulation();
//        findRunner();
        EMARunner();
    }

    public static void simulation() {
        String type = SerenaSimulation.type;

        clearTradeHistory(type);

        ControllerTest controller = ControllerTest.getInstance(type);

        controller.addDataTrack(IntervalEnum.MIN_1);
//        controller.addDataTrack(IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_BASIC_EMA_FOR_UP, IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_BASIC_EMA_FOR_DOWN, IntervalEnum.MIN_1);
//        controller.addStrategy(StrategyEnum.STRATEGY_FIND_MAX_PERCENT, IntervalEnum.MIN_1);
//        controller.addStrategy(StrategyEnum.STRATEGY_FIND_MAX_PERCENT_REVERSE, IntervalEnum.MIN_1);
//        controller.addStrategy(StrategyEnum.STRATEGY_LOSS_LIMIT, IntervalEnum.MIN_2);
//        controller.addStrategy(StrategyEnum.STRATEGY_PROFIT_LIMIT, IntervalEnum.MIN_1);
//        controller.addStrategy(StrategyEnum.STRATEGY_MA_520, IntervalEnum.MIN_5);
//        controller.addStrategy(StrategyEnum.STRATEGY_EMA_520, IntervalEnum.MIN_1);
//        controller.addStrategy(StrategyEnum.STRATEGY_FILL_GAP, IntervalEnum.MIN_2);
//        controller.addStrategy(StrategyEnum.STRATEGY_CLOSE_ON_END, IntervalEnum.NULL);
//        controller.addStrategy(StrategyEnum.STRATEGY_ONLY_ONE_PER_DAY, IntervalEnum.NULL);

        controller.filename = "find_percent_" + type + ".csv";
        controller.run();
    }

    public static void EMARunner() {
        boolean upSide = true;
        boolean downSide = true;
        int EMALowerBound = 400;
        int EMAUpperBound = 500;
        int step = 100;
        double[] EMA_ALPHA = {10, 12, 10, 12};
        double[] profitThreshold = {0.008};     // 预期可以获得开仓时收盘价的 x% 收益 （0.5% 填写 0.005，下同）
        double[] profitLimit = {0.7};          // 收益达到预期收益后，回落至历史最高收益的 x% 时平仓
        double[] lossLimit = {0.005};           // 损失超过开仓时收盘价的 x% 就平仓
        // 下面代码不要动
        ArrayList<double[]> EMAs = new ArrayList<>();
        if (upSide & downSide) {
            EMAs.add(EMA_ALPHA);
        } else {
            EMAs = EMACombination.generateEMA(EMALowerBound, EMAUpperBound, step, upSide, downSide);
        }

        PriorityQueue<EMACombination> queue = new PriorityQueue<>(50, Collections.reverseOrder());
        for(double[] EMA : EMAs) {
            for (double pThres : profitThreshold) {
                for(double pLimit : profitLimit) {
                    for (double lLimit : lossLimit) {
                        Setting.EMA_PROFIT_THRESHOLD = pThres;
                        Setting.EMA_PROFIT_LIMIT = pLimit;
                        Setting.EMA_LOSS_LIMIT = lLimit;
                        Setting.EMA_ALPHA = EMA;
                        simulation();
                        try {
                            Thread.sleep(500);
                            ControllerTest.stop();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        EMACombination newRes = new EMACombination(EMA, pThres, pLimit, lLimit);
                        newRes.setProfit(ProfitCal.cal(type));
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
        ArrayList<EMACombination> resList = new ArrayList<>();
        System.out.println("\nTop 50 Best Parameters: ");
        for (int i = 0; i < 50; i++) {
            EMACombination candidate = queue.poll();
            if (candidate != null) {
                resList.add(candidate);
            }
        }
        for (EMACombination res : resList) {
            System.out.println(res);
        }

        String filename = type + '_' + Calendar.getInstance().getTime().getTime();
        FileUtil.newFile(filename + ".csv");
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {
            for (EMACombination res : resList) {
                writer.append(res.toString());
            }
            writer.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }
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

        PriorityQueue<ParaCombination> queue = new PriorityQueue<>(50, Collections.reverseOrder());
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
        System.out.println("\nTop 50 Best Parameters: ");
        System.out.println("Total Profit\tParameters\t\t\t\t\t\t\tPut P\tPut L\tShort P\tShort L\tTotal\tCont. Loss\tOccurred");
        for (int i = 0; i < 50; i++) {
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

    public static void findRunner() {
        int EMALowerBound = 100;
        int EMAUpperBound = 110;
        int step = 10;
        double threshold = -0.002;

        String filename = "find_percent_" + type + ".csv";
        FileUtil.newFile(filename);
        for(int i = EMALowerBound; i <= EMAUpperBound; i += step) {
            for(int j = i + step; j <= EMAUpperBound; j += step) {

                try (FileWriter writer = new FileWriter(filename, true)) {
                    writer.append(Integer.toString(i)).append("  ").append(Integer.toString(j)).append("  ");
                } catch (FileNotFoundException ignored) {

                } catch (IOException e) {
                    e.printStackTrace();
                }

                Setting.EMA_ALPHA[0] = i;
                Setting.EMA_ALPHA[1] = j;
                simulation();
                try {
                    Thread.sleep(500);
                    ControllerTest.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        parseFindRunner(EMALowerBound, EMAUpperBound, step, threshold);
    }

    public static void parseFindRunner(int lower, int upper, int step, double threshold) {
        double maxPercent = 1.0;
        int maxLine = 0;


        String filename = "find_percent_" + type + ".csv";
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                count++;
                if(!line.equals("")) {
                    String[] stringArray = line.strip().split("  ");
                    if(stringArray.length == 3) {
                        String doubleArray = stringArray[2];
                        String[] strArray = doubleArray.substring(1, doubleArray.length()-1).split(", ");
                        double exceedCount = 0;
                        for(String str : strArray) {
                            if(Double.parseDouble(str) < threshold) {
                                exceedCount++;
                            }
                        }
                        double percent = exceedCount / strArray.length;
                        if(percent < maxPercent) {
                            maxPercent = percent;
                            maxLine = count;
                        }
                    }

                }
            }
            System.out.println("Min Percent is " + String.format("%.2f", maxPercent * 100) + "%, Line is " + maxLine);
        } catch (IOException ignored) {
        }
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
