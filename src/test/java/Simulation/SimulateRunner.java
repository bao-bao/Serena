package Simulation;

import SerenaSimulation.profit.ProfitCal;
import com.regrx.trade.constant.Constant;

import java.io.File;

public class SimulateRunner {
    public static void main(String[] args) {

        String filename = "History_IF2202_1";
        File file = new File("Trade_" + filename + ".log");
        if(!file.exists() || file.delete()) {
            MockDataTrack dataTrack = new MockDataTrack(filename, Constant.MIN_5);
            dataTrack.track();
            ProfitCal.cal(filename);
        }
    }
}
