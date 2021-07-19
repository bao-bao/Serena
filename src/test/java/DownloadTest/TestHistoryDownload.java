package DownloadTest;

import com.regrx.trade.data.MinutesData;
import com.regrx.trade.network.HistoryDataDownloader;

public class TestHistoryDownload {
    public static void main(String[] args) {
        MinutesData minutesData = HistoryDataDownloader.getHistoryData("IF2107", 5);
    }
}
