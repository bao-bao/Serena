package SerenaSimulation;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.MinutesData;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataServiceManagerTest {
    private final String type;
    private final ExecutorService dataTrackingExecutor;
    private final HashMap<IntervalEnum, DataServiceTest> dataList;
    private final HashMap<IntervalEnum, Future<?>> dataRef;
    private static DataServiceManagerTest dataSvcMgr;


    private DataServiceManagerTest(String type) {
        this.type = type;
        this.dataTrackingExecutor = Executors.newCachedThreadPool();
        this.dataList = new HashMap<>();
        this.dataRef = new HashMap<>();
    }

    public static DataServiceManagerTest getInstance(String type) {
        if(dataSvcMgr == null) {
            dataSvcMgr = new DataServiceManagerTest(type);
        }
        return dataSvcMgr;
    }

    public static DataServiceManagerTest getInstance() {
        return dataSvcMgr;
    }

    public void addDataTrackThread(IntervalEnum interval) {
        if(dataRef.containsKey(interval)) {
            LogUtil.getInstance().warning("Already tracking on " + type + " for " + interval.getValue() + "minute(s) interval");
            return;
        }
        DataServiceTest dataService = new DataServiceTest(type, interval);
        dataList.put(interval, dataService);
        Future<?> future = this.dataTrackingExecutor.submit(dataService);
        dataRef.put(interval, future);
    }

    public void removeDataTrackThread(IntervalEnum interval) {
        if(!dataRef.containsKey(interval)) {
            LogUtil.getInstance().warning("No tracking for " + interval.getValue() + "minute(s) interval");
            return;
        }
        Future<?> future = dataRef.get(interval);
        future.cancel(true);
        dataRef.remove(interval);
        dataList.remove(interval);
    }

    public MinutesData queryData(IntervalEnum interval) {
        DataServiceTest dst = dataList.get(IntervalEnum.MIN_1);
        if (interval == IntervalEnum.MIN_5) {
            return dst.getMinutesData_5();
        }
        return dst.getMinutesData_1();
    }
}
