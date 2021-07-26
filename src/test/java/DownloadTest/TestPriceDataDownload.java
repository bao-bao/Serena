package DownloadTest;

import com.regrx.trade.data.PriceData;
import com.regrx.trade.network.PriceDataDownloader;

public class TestPriceDataDownload {
    public static void main(String[] args) {
        String url = "https://hq.sinajs.cn/list=nf_AU2112";
        PriceData priceData = PriceDataDownloader.getPriceDataForOtherFutures(url);
        System.out.println(priceData.getDate());
        System.out.println(priceData.getPrice());
    }
}
