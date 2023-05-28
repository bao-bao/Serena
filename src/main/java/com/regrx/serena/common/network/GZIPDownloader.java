package com.regrx.serena.common.network;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class GZIPDownloader {

    private static String tryDownload(String urlString, String type) {
        HttpURLConnection con;
        GZIPInputStream stream;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept-Encoding", "gzip");
            con.addRequestProperty("Referer", "https://finance.sina.com.cn/futures/quotes/" + type + ".shtml");
            stream = new GZIPInputStream(con.getInputStream());
        } catch (Exception e) {
            return null;
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            int res = 0;
            byte[] buf = new byte[1024];
            while (res >= 0) {
                res = stream.read(buf, 0, buf.length);
                if (res > 0) {
                    output.write(buf, 0, res);
                }
            }
            return output.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String download(String urlString, String type, int retry) {
        String res;
        while(retry-- > 0) {
            res = tryDownload(urlString, type);
            if(res != null) {
                return res;
            }
            LogUtil.getInstance().warning("download error, remaining retry " + retry + " time(s)");
            try {
                Thread.sleep(Setting.DOWNLOAD_RETRY_TIME);
            } catch (Exception ignored) {}
        }

        // empty before exit
//        LogUtil.getInstance().severe("Cannot fetch price data! Check network connection!");
//        TradeUtil.forceEmpty(type);
//        System.exit(ErrorType.DOWNLOAD_ERROR_CODE.getCode());

        return null;
    }
}
