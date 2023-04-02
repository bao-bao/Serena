package UtilTest;

import com.regrx.serena.common.network.PriceDownloader;
import com.regrx.serena.data.base.ExPrice;

public class TestDownloader {
    public static void main(String[] args) {
        String type = "IF0";
        String url = "https://hq.sinajs.cn/list=nf_" + type;
        ExPrice res = PriceDownloader.getPriceDataForStockFutures(url, type);
        System.out.println(res);
    }
}
