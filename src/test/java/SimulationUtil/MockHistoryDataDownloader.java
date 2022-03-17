package SimulationUtil;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.MinutesData;

public class MockHistoryDataDownloader {
    public static MinutesData getHistoryData(MinutesData priceData, IntervalEnum interval, int size) {

        MinutesData records = new MinutesData(interval);

        for(int i = 0; i < size && i < priceData.size(); i++) {
            records.updateWithoutWrite(priceData.getPrices().get(priceData.size() - i - 1));
        }

        LogUtil.getInstance().info("Success loaded " + size + " history data, last record time is " + records.getNewRecordTime());
        return records;
    }

    public static MinutesData getHistoryDataFromOneMinData(MinutesData priceData, IntervalEnum interval, int size) {

        MinutesData records = new MinutesData(interval);

        for(int i = 0; i < size && i < priceData.size(); i = i + interval.getValue()) {
            records.updateWithoutWrite(priceData.getPrices().get(size - i - 1));
        }

        LogUtil.getInstance().info("Success loaded " + size + "history data, last record time is " + records.getNewRecordTime());
        return records;
    }
}
