package SerenaSimulation;

import SerenaSimulation.profit.ProfitCal;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
public class SerenaSimulation {

    public static void main(String[] args) {
        runner();
//        simulation();
    }


    public static void runner() {
        double[] lossLimitPara = {9.0, 11.0, 13.0, 15.0, 17.0, 19.0, 21.0, 23.0, 25.0, 27.0, 30.0};
        double[] profitLimitPara = { 9.0, 11.0, 13.0, 15.0, 17.0, 19.0, 21.0, 23.0, 25.0, 27.0, 30.0};
        double[] restorePara = {9.0, 11.0, 13.0, 15.0, 17.0, 19.0, 21.0, 23.0, 25.0, 27.0, 30.0};
        double[] MA520Para = {0.0, 0.3, 0.5, 0.8, 1.0, 1.5, 2.0, 2.5};
//        double[] lossLimitPara = {15.0, 17.0};
//        double[] profitLimitPara = {13.0};
//        double[] restorePara = {17.0};
//        double[] MA520Para = {0.5};

        double profit;
        double best = 0.0;
        double[] bestPara = {0, 0, 0, 0};
        for(double lossLimit  : lossLimitPara) {
            for (double profitLimit : profitLimitPara) {
                for (double restore : restorePara) {
                    for (double ma520 : MA520Para) {
                        Setting.LOSS_LIMIT_THRESHOLD = lossLimit;
                        Setting.PROFIT_LIMIT_THRESHOLD = profitLimit;
                        Setting.RESTORE_THRESHOLD = restore;
                        Setting.TRADE_THRESHOLD = ma520;
                        simulation();
                        try {
                            Thread.sleep(500);
                            ControllerTest.stop();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        profit = ProfitCal.cal("IC2212");
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(best < profit) {
                            best = profit;
                            bestPara[0] = lossLimit;
                            bestPara[1] = profitLimit;
                            bestPara[2] = restore;
                            bestPara[3] = ma520;
                        }
                    }
                }
            }
        }
        System.out.println("Best Profit: " + String.format("%.2f", best));
        System.out.print("Best Parameters: [");
        for(int i = 0; i < 3; i++) {
            System.out.print(bestPara[i] + ", ");
        }
        System.out.println(bestPara[3] + "]");

    }

    public static void simulation() {
        String type = "IC2212";

        clearTradeHistory(type);

        ControllerTest controller = ControllerTest.getInstance(type);

//        controller.addDataTrack(IntervalEnum.MIN_1);
//        controller.addDataTrack(IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_LOSS_LIMIT, IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_PROFIT_LIMIT, IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_MA_520, IntervalEnum.MIN_5);
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
}
