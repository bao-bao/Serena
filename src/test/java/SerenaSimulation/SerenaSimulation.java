package SerenaSimulation;

import SerenaSimulation.profit.*;
import SerenaSimulation.strategy.AbstractStrategy;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.utils.FileUtil;
import SerenaSimulation.strategy.Bollinger;
import com.regrx.serena.strategy.StrategyOption;

import java.io.*;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;

// TODO: 加几个输出值
public class SerenaSimulation {
    public static String type = "IF9999";

    public static void main(String[] args) {
//        runner();
//        simulation();
//        findRunner();
//        EMARunner();
        BollingerRunner();
    }

    public static void simulation() {
        String type = SerenaSimulation.type;

        clearTradeHistory(".", type);

        ControllerTest controller = ControllerTest.getInstance(type);

        controller.addDataTrack(IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_BOLLINGER, IntervalEnum.MIN_1);

        controller.filename = "find_percent_" + type + ".csv";
        controller.run();
        int i = 0;
    }


    public static void BollingerRunner() {
        int[] options = {
                StrategyOption.BollingerLongByDefault,          // B1
//                StrategyOption.BollingerLongByTail,             // B2
                StrategyOption.BollingerLongCoverByLose,        // LC1
                StrategyOption.BollingerLongCoverByFallback,    // LC2
//                StrategyOption.BollingerShortByDefault,         // S1
//                StrategyOption.BollingerShortByTail,            // S2
//                StrategyOption.BollingerShortCoverByLose,       // SC1
//                StrategyOption.BollingerShortCoverByFallback,   // SC2
//                StrategyOption.DefaultNST,                      // NST
        };
        int[] aggrCount = {35};
        double[] SD = {2};
        double[] B2 = {2};
        double[] LCP = {5};
        double[] LC1 = {0.8};
        double[] LC2 = {5};
        double[] S2 = {2};
        double[] SCP = {10};
        double[] SC1 = {0.5};
        double[] SC2 = {10};

        Bollinger bollinger = new Bollinger(IntervalEnum.MIN_1);
        for (int option : options) {
            bollinger = bollinger.withOption(option);
        }

        // create file folder
        StringBuilder strategyName = new StringBuilder();
        for(int option : options) {
            strategyName.append(StrategyOption.getName(option));
        }
        String path = "./" + type + "_" + strategyName + "_" + Calendar.getInstance().getTime().getTime();
        File file = new File(path);
        file.mkdir();

        double total = aggrCount.length * SD.length * B2.length * LCP.length * LC1.length * LC2.length * S2.length * SCP.length * SC1.length * SC2.length;
        System.out.println("Estimate running count is " + total + " ...");
        PriorityQueue<BollingerCombination> queue = new PriorityQueue<>(4000, Collections.reverseOrder());
        for (int cnt : aggrCount) {
            for (double z : SD) {
                for (double a : B2) {
                    for (double b : LC1) {
                        for (double c : LC2) {
                            for (double d : S2) {
                                for (double e : SC1) {
                                    for (double f : SC2) {
                                        for (double g : LCP) {
                                            for (double h : SCP) {
                                                Setting.BOLLINGER_AGGREGATE_COUNT = cnt;
                                                Setting.BOLLINGER_DEVIATION_MULTIPLIER = z;
                                                Setting.BOLLINGER_B_PRICE_REFERENCE = a;
                                                Setting.BOLLINGER_B_PROFIT_TREAT = g;
                                                Setting.BOLLINGER_B_FALLBACK = b;
                                                Setting.BOLLINGER_B_LOSE_LIMIT = c;
                                                Setting.BOLLINGER_S_PRICE_REFERENCE = d;
                                                Setting.BOLLINGER_S_PROFIT_TREAT = h;
                                                Setting.BOLLINGER_S_FALLBACK = e;
                                                Setting.BOLLINGER_S_LOSE_LIMIT = f;
                                                simulationWithStrategy(path, StrategyEnum.STRATEGY_BOLLINGER, bollinger);
                                                try {
                                                    Thread.sleep(500);
                                                    ControllerTest.stop();
                                                } catch (InterruptedException ex) {
                                                    ex.printStackTrace();
                                                }
                                                BollingerCombination newRes = new BollingerCombination(cnt, z, a, b, c, d, e, f, g, h);
                                                newRes.setProfit(ProfitCal.cal(path, type, false));
                                                queue.add(newRes);
                                                try {
                                                    Thread.sleep(500);
                                                } catch (InterruptedException ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList<BollingerCombination> resList = new ArrayList<>();
        System.out.println("\nTop Best Parameters: ");
        for (int i = 0; i < 4000; i++) {
            BollingerCombination candidate = queue.poll();
            if (candidate != null) {
                resList.add(candidate);
            }
        }
        System.out.println("Profit\tTotal Count\tWin Rate\tAPPT\tEVPT\tEVUR\tKelly\tOdds\tMax Loss\tCNT\tSD\tB2\tLCP\tLC1\tLC2\tS2\tSCP\tSC1\tSC2");

        for (BollingerCombination res : resList) {
            System.out.print(res);
        }

        String filename = path + "/" + type + "_analyze_summary";
        FileUtil.newFile(filename + ".csv");
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {
            writer.append("Profit,")
                    .append("Total Count,")
                    .append("Win Rate,")
                    .append("APPT,")
                    .append("EVPT,")
                    .append("EVUR,")
                    .append("Kelly,")
                    .append("Odds,")
                    .append("Max Loss,")
                    .append("CNT,")
                    .append("SD,")
                    .append("B2,")
                    .append("LCP,")
                    .append("LC1,")
                    .append("LC2,")
                    .append("S2,")
                    .append("SCP,")
                    .append("SC1,")
                    .append("SC2\n");
            for (BollingerCombination res : resList) {
                writer.append(res.toString());
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void simulationWithStrategy(String path, StrategyEnum name, AbstractStrategy strategy) {
        String type = SerenaSimulation.type;

        clearTradeHistory(path, type);

        ControllerTest controller = new ControllerTest(path, type);

        controller.addDataTrack(IntervalEnum.MIN_1);

        controller.addStrategyWithOption(name, strategy);

        controller.filename = path + "/find_percent_" + type + ".csv";
        controller.run();
    }


    public static void EMARunner() {
        boolean upSide = true; // 都是true为单测，都是false为两边同时测
        boolean downSide = true;
        int EMALowerBound = 100;
        int EMAUpperBound = 500;
        int step = 10;
        double[] EMA_ALPHA = {90.0, 280.0, 370.0, 390.0};
        double[] upProfitThreshold = {0.006, 0.007};     // 预期可以获得开仓时收盘价的 x% 收益 （0.5% 填写 0.005，下同）
        double[] upProfitLimit = {0.7};          // 收益达到预期收益后，回落至历史最高收益的 x% 时平仓
        double[] upLossLimit = {0.003};           // 损失超过开仓时收盘价的 x% 就平仓
        double[] downProfitThreshold = {0.015};     // 预期可以获得开仓时收盘价的 x% 收益 （0.5% 填写 0.005，下同）
        double[] downProfitLimit = {0.6};          // 收益达到预期收益后，回落至历史最高收益的 x% 时平仓
        double[] downLossLimit = {0.0075};           // 损失超过开仓时收盘价的 x% 就平仓
        // 下面代码不要动
        ArrayList<double[]> EMAs = new ArrayList<>();
        if (upSide & downSide) {
            EMAs.add(EMA_ALPHA);
        } else {
            EMAs = EMACombination.generateEMA(EMALowerBound, EMAUpperBound, step, upSide, downSide);
        }

        double oneLevelCount = (double) (EMAUpperBound - EMALowerBound) / step;
        int total;
        if (!upSide && !downSide) {
            total = (int) Math.pow((oneLevelCount * (oneLevelCount - 1) / 2 + oneLevelCount), 2);
        } else if (!(upSide & downSide)) {
            total = (int) (oneLevelCount * (oneLevelCount - 1) / 2 + oneLevelCount);
        } else {
            total = 1;
        }
        total *= upProfitThreshold.length * upProfitLimit.length * upLossLimit.length * downProfitThreshold.length * downProfitLimit.length * downLossLimit.length;
        System.out.println("Estimate running count is " + total + " ...");
        PriorityQueue<EMACombination> queue = new PriorityQueue<>(4000, Collections.reverseOrder());

        int count = 1;
        for (double[] EMA : EMAs) {
            for (double upPT : upProfitThreshold) {
                for (double upPL : upProfitLimit) {
                    for (double upLL : upLossLimit) {
                        for (double downPT : downProfitThreshold) {
                            for (double downPL : downProfitLimit) {
                                for (double downLL : downLossLimit) {
                                    System.out.println(
                                            "current running: " + count++ + "/" + total + ", " +
                                                    "EMA: [" + (int) EMA[0] + ", " + (int) EMA[1] + ", " + (int) EMA[2] + ", " + (int) EMA[3] + "], " +
                                                    "upProfitThreshold: " + upPT + ", " +
                                                    "upProfitLimit: " + upPL + ", " +
                                                    "upLossLimit: " + upLL + ", " +
                                                    "downProfitThreshold: " + downPT + ", " +
                                                    "downProfitLimit: " + downPL + ", " +
                                                    "downLossLimit: " + downLL
                                    );
                                    Setting.EMA_UP_PROFIT_THRESHOLD = upPT;
                                    Setting.EMA_UP_PROFIT_LIMIT = upPL;
                                    Setting.EMA_UP_LOSS_LIMIT = upLL;
                                    Setting.EMA_DOWN_PROFIT_THRESHOLD = downPT;
                                    Setting.EMA_DOWN_PROFIT_LIMIT = downPL;
                                    Setting.EMA_DOWN_LOSS_LIMIT = downLL;
                                    Setting.EMA_ALPHA = EMA;
                                    simulation();
                                    try {
                                        Thread.sleep(500);
                                        ControllerTest.stop();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    EMACombination newRes = new EMACombination(EMA, upPT, upPL, upLL, downPT, downPL, downLL);
                                    newRes.setProfit(ProfitCal.cal(".", type, upSide && downSide));
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
            }
        }
        ArrayList<EMACombination> resList = new ArrayList<>();
        System.out.println("\nTop Best Parameters: ");
        for (int i = 0; i < 4000; i++) {
            EMACombination candidate = queue.poll();
            if (candidate != null) {
                resList.add(candidate);
            }
        }
        System.out.println("Profit\tTotal Count\tWin Rate\tAPPT\tEVPT\tEVUR\tKelly\tOdds\tMax Loss\tEMA\tUp Profit Threshold\tUp Profit Limit\tUp Loss Limit\tDown Profit Threshold\tDown Profit Limit\tDown Loss Limit");

        for (EMACombination res : resList) {
            System.out.print(res);
        }

        String filename = type + '_' + Calendar.getInstance().getTime().getTime();
        FileUtil.newFile(filename + ".csv");
        try (FileWriter writer = new FileWriter(filename + ".csv", true)) {
            writer.append("Profit,")
                    .append("Total Count,")
                    .append("Win Rate,")
                    .append("APPT,")
                    .append("EVPT,")
                    .append("EVUR,")
                    .append("Kelly,")
                    .append("Odds,")
                    .append("Max Loss,")
                    .append("EMA,")
                    .append("Up Profit Threshold,")
                    .append("Up Profit Limit,")
                    .append("Up Loss Limit,")
                    .append("Down Profit Threshold,")
                    .append("Down Profit Limit,")
                    .append("Down Loss Limit\n");
            for (EMACombination res : resList) {
                writer.append(res.toString());
            }
            writer.flush();
        } catch (IOException e) {
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
                            newRes.setProfit(ProfitCal.cal(".", type, false));
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
        for (int i = EMALowerBound; i <= EMAUpperBound; i += step) {
            for (int j = i + step; j <= EMAUpperBound; j += step) {

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
                if (!line.equals("")) {
                    String[] stringArray = line.strip().split("  ");
                    if (stringArray.length == 3) {
                        String doubleArray = stringArray[2];
                        String[] strArray = doubleArray.substring(1, doubleArray.length() - 1).split(", ");
                        double exceedCount = 0;
                        for (String str : strArray) {
                            if (Double.parseDouble(str) < threshold) {
                                exceedCount++;
                            }
                        }
                        double percent = exceedCount / strArray.length;
                        if (percent < maxPercent) {
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

    private static void clearTradeHistory(String path, String type) {
        File tradeHistory = new File(path + "/Trade_" + type + ".log");
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
                writer.append(String.valueOf(testResult.getCLTime())).append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
