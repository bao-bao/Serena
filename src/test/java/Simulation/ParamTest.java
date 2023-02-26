package Simulation;

import SerenaSimulation.profit.ProfitCal;
import SerenaSimulation.profit.TestResult;
import com.regrx.trade.constant.Constant;

import java.io.File;
import java.io.IOException;

// Best History: record-01/05/2022-01/14/2022(1921) name-IF2202, p-15, l-9 , r-8 , profit-56, trade-78
// Best History: record-01/17/2022-02/08/2022(2703) name-IF2202, p-13, l-12, r-10, profit-66, trade-142

public class ParamTest {
    public static void main(String[] args) throws InterruptedException, IOException {

        int[] profitLimit = new int[]{8, 12, 13, 14, 15, 16};
        int[] lossLimit = new int[]{8, 9, 10, 11, 12, 13};
        int[] restore = new int[]{6, 7, 8, 9, 10, 11};
        TestResult[][][] profits = new TestResult[6][6][6];
        String filename = "History_IF2202_1";

        for(int p = 0; p < profitLimit.length; p++) {
            for(int l = 0; l < lossLimit.length; l++) {
                for(int r = 0; r < restore.length; r++) {
                    File file = new File("Trade_" + filename + ".log");
                    Constant.PROFIT_LIMIT_THRESHOLD = profitLimit[p];
                    Constant.LOSS_LIMIT_THRESHOLD = lossLimit[l];
                    Constant.RESTORE_THRESHOLD = restore[r];
                    if(!file.exists() || file.delete()) {
                        MockDataTrack dataTrack = new MockDataTrack(filename, Constant.MIN_5);
                        dataTrack.track();
                        profits[p][l][r] = ProfitCal.cal(filename, true);
                    } else {
                        System.out.println("Error at: " + p + " " + l + " " + r);
                    }
                }
            }
        }

        double maxProfit = 0.0;
        int maxP = 0, maxL = 0, maxR = 0;
        for(int p = 0; p < profitLimit.length; p++) {
            for(int l = 0; l < lossLimit.length; l++) {
                for(int r = 0; r < restore.length; r++) {
                    if(profits[p][l][r].getTotalProfit() > maxProfit) {
                        maxProfit = profits[p][l][r].getTotalProfit();
                        maxP = p;
                        maxL = l;
                        maxR = r;
                    }
                }
            }
        }
        System.out.println("Max Profit: " + maxProfit);
        System.out.println("Parameters: ProfitLimit = " + maxP + ", LossLimit = " + maxL + ", Restore = " + maxR);
    }
}
