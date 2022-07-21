package SerenaSimulation;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.MinutesData;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;

import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;

public class DataServiceTest implements Runnable {
    private final String type;
    private final IntervalEnum interval;
    private final MinutesData minutesData;
    private final MinutesData minutesData_2;
    private final MinutesData minutesData_3;
    private final MinutesData minutesData_5;

    public DataServiceTest(String type, IntervalEnum interval) {
        super();
        this.type = type;
        this.interval = interval;
        this.minutesData = new MinutesData(interval);
        this.minutesData_2 = new MinutesData(interval);
        this.minutesData_3 = new MinutesData(interval);
        this.minutesData_5 = new MinutesData(interval);
    }

    @Override
    public void run() {
        LogUtil.getInstance().info(type + ": Start fetching " + interval.getValue() + " minute(s) data...");
        BufferedReader reader;
        ExPrice newPrice;
        try {
            reader = new BufferedReader(new FileReader("History_" + type + '_' + interval + ".csv"));
            String line;
            int count = 1;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                newPrice = new ExPrice(Double.parseDouble(data[2]), data[0]); // close price
                minutesData.updateWithoutWrite(newPrice);
                if(interval == IntervalEnum.MIN_1 && count % 5 == 0) {
                    minutesData_5.updateWithoutWrite(newPrice);
                }
                if(interval == IntervalEnum.MIN_1 && count % 2 == 0) {
                    minutesData_2.updateWithoutWrite(newPrice);
                }
                if(interval == IntervalEnum.MIN_1 && count % 3 == 0) {
                    minutesData_3.updateWithoutWrite(newPrice);
                }
                count++;
                callback(newPrice);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ControllerTest.getInstance(type).setSignal(false);
            ArrayBlockingQueue<Decision> queue;
            synchronized (queue = ControllerTest.getDecisionQueue()) {
                queue.add(new Decision());
                queue.notify();
            }
        }
    }

    private void callback(ExPrice newPrice) {
        if(interval != DataServiceManagerTest.getInstance().getMinimumInterval()) {
            return;
        }

        LogUtil.getInstance().info("Making trade decision on point " + newPrice + "...");
        Decision decision = StrategyManagerTest.getInstance().execute(newPrice);
        LogUtil.getInstance().info("Decision making complete!");

        ArrayBlockingQueue<Decision> queue;
        synchronized (queue = ControllerTest.getDecisionQueue()) {
            while (queue.size() == Setting.MAX_DECISION_QUEUE_SIZE && ControllerTest.getInstance("SZ").getSignal()) {
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
        }
    }

    public MinutesData getMinutesData() {
        return minutesData;
    }

    public MinutesData getMinutesData_2() {
        return minutesData_2;
    }

    public MinutesData getMinutesData_3() {
        return minutesData_3;
    }

    public MinutesData getMinutesData_5() {
        return minutesData_5;
    }
}
