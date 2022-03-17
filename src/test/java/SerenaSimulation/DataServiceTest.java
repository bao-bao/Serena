package SerenaSimulation;

import SimulationUtil.MockHistoryDataDownloader;
import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.FutureType;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.controller.Controller;
import com.regrx.serena.common.network.HistoryDownloader;
import com.regrx.serena.common.network.PriceDownloader;
import com.regrx.serena.common.utils.FileUtil;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.common.utils.PreparationUtil;
import com.regrx.serena.common.utils.TimeUtil;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.service.StrategyManager;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

public class DataServiceTest implements Runnable {
    private final String type;
    private final IntervalEnum interval;
    private MinutesData minutesData_1;
    private MinutesData minutesData_5;

    public DataServiceTest(String type, IntervalEnum interval) {
        super();
        this.type = type;
        this.interval = interval;
        this.minutesData_1 = new MinutesData(IntervalEnum.MIN_1);
        this.minutesData_5 = new MinutesData(IntervalEnum.MIN_5);
    }

    @Override
    public void run() {

        LogUtil.getInstance().info(type + ": Start test fetching " + interval.getValue() + " minute(s) data...");
        MinutesData priceData = FileUtil.readMinutesDataFromCsv(type, interval, "History");
        minutesData_1 = MockHistoryDataDownloader.getHistoryData(priceData, interval, 20);
        minutesData_5 = MockHistoryDataDownloader.getHistoryData(priceData, interval, 4);
        int nextInd = priceData.size() - 20 - 1;
        int count = 0;

        while (nextInd >= 0) {

            ExPrice newPrice = priceData.getPrices().get(nextInd);
            minutesData_1.updateWithoutWrite(newPrice);
            count++;
            if(count % 5 == 0) {
                minutesData_5.updateWithoutWrite(newPrice);
            }
            nextInd--;
            callback(newPrice);
        }
    }

    private void callback(ExPrice newPrice) {
        if (interval != IntervalEnum.MIN_1) {
            return;
        }
        LogUtil.getInstance().info("Making trade decision on point " + newPrice + "...");
        Decision decision = StrategyManagerTest.getInstance().execute(newPrice);
        LogUtil.getInstance().info("Decision making complete!");

        ArrayBlockingQueue<Decision> queue;
        synchronized (queue = ControllerTest.getDecisionQueue()) {
            while (queue.size() == Setting.MAX_DECISION_QUEUE_SIZE) {
                try {
                    queue.notify();
                    queue.wait();
                } catch (InterruptedException ignored) {
                }
            }
            queue.add(decision);
            try {
                queue.notify();
                queue.wait();
            } catch (InterruptedException ignored) {
            }
            LogUtil.getInstance().info("Decision added into queue, waiting for consuming...");
        }
    }

    public MinutesData getMinutesData_1() {
        return minutesData_1;
    }


    public MinutesData getMinutesData_5() {
        return minutesData_5;
    }
}
