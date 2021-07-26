package com.regrx.trade.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

public class Util {
    public static String downloadFromGZIPFormat(String urlString) {
        HttpURLConnection con;
        GZIPInputStream stream;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept-Encoding", "gzip");
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
        }
        return null;
    }
}
