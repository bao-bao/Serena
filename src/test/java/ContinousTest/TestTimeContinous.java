package ContinousTest;

import com.regrx.trade.data.PriceData;

import java.util.Date;
import java.util.LinkedList;

public class TestTimeContinous {

    public static void main(String[] args) {
        LinkedList<PriceData> data = Util.CsvReader.readPriceFromCsv("IF2201", 1);
        LinkedList<Integer> segments = new LinkedList<>();

        Date currDate, lastDate = null;
        int count = 0;
        for (PriceData pd: data) {
            currDate = pd.getDate();
            if(currDate.getHours() == 9 && currDate.getMinutes() == 29) {
                currDate.setMinutes(30);
            }
            if(lastDate != null) {
                if((lastDate.getHours() == 11 && lastDate.getMinutes() == 30 && currDate.getHours() == 13 && (currDate.getMinutes() == 0 || currDate.getMinutes() == 1))
                        || (lastDate.getHours() == 15 && lastDate.getMinutes() == 0 && currDate.getHours() == 9 && (currDate.getMinutes() == 30 || currDate.getMinutes() == 31))
                        || (currDate.getTime() - lastDate.getTime() == 60000)) {
                    count++;
                } else {
                    segments.add(count);
                    count = 1;
                }
            }
            lastDate = currDate;
        }
        segments.add(count);

        System.out.println("Segment Number: " + segments.size());

        for (int s : segments) {
            System.out.print(s + " ");
        }
    }
}
