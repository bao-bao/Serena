package DownloadTest;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.MinutesData;
import com.regrx.trade.network.HistoryDataDownloader;

public class TestHistoryDownload {
    public static void main(String[] args) {
        MinutesData minutesData = HistoryDataDownloader.getHistoryData("AU2112", 5, Constant.FUTURE_NIGHT_0230);
        System.out.println(minutesData.getLastPrice());
        System.out.println(minutesData.getCurrentTime());
    }
}
