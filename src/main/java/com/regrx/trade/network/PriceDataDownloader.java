package com.regrx.trade.network;

import com.regrx.trade.data.PriceData;
import com.regrx.trade.util.Time;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;

public class PriceDataDownloader {

    public static PriceData getPriceData(String urlString) {
        HttpURLConnection con;
        GZIPInputStream stream;
        PriceData newPrice = new PriceData();
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept-Encoding", "gzip");
            stream = new GZIPInputStream(con.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return newPrice;
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
            String originString = output.toString();
            String finalString = originString.substring(
                    StringUtils.ordinalIndexOf(originString,",",3) + 1,
                    StringUtils.ordinalIndexOf(originString,",",4));
            String timeString = originString.substring(
                    StringUtils.ordinalIndexOf(originString,",",36) + 1,
                    StringUtils.ordinalIndexOf(originString,",",38));
            String pattern = "yyyy-MM-dd,HH:mm:ss";

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                Date date = simpleDateFormat.parse(timeString);
                newPrice.setDate(Time.getClosetMinute(date));
            } catch (ParseException e) {
                e.printStackTrace();
                return newPrice;
            }
            newPrice.setPrice(Double.parseDouble(finalString));
            return newPrice;
        } catch (IOException e) {
            System.out.println("Write Error");
        }
        return newPrice;
    }

}
