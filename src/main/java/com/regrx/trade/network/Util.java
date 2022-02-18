package com.regrx.trade.network;

import com.regrx.trade.control.KeySprite;
import com.regrx.trade.data.Status;
import com.regrx.trade.strategy.MA5MA20;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

public class Util {
    private static String downloadFromGZIPFormat(String urlString) {
        HttpURLConnection con;
        GZIPInputStream stream;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept-Encoding", "gzip");
            con.addRequestProperty("Referer", "https://finance.sina.com.cn/futures/quotes/IF2202.shtml");
            stream = new GZIPInputStream(con.getInputStream());
        } catch (Exception e) {
            System.out.println("download Error");
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
            System.out.println("download Error");
            return null;
        }
    }

    public static String downloadFromGZIPFormat(String urlString, int count) {
        String res;
        while(count-- > 0) {
            res = Util.downloadFromGZIPFormat(urlString);
            if(res != null) {
                return res;
            }
        }

        // empty before exit
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        Future<Void> future = newCachedThreadPool.submit(new KeySprite("E"));
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            newCachedThreadPool.shutdown();
        }
        return null;
    }
}
