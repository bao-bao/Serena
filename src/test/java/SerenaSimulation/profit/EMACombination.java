package SerenaSimulation.profit;


import java.util.ArrayList;

public class EMACombination implements Comparable<EMACombination> {
    public TestResult profit;
    public double[] EMA;

    public EMACombination(double[] ema) {
        EMA = ema.clone();
    }

    public static ArrayList<double[]> generateEMA(int lower, int upper, int step) {
        ArrayList<double[]> res = new ArrayList<>();
        for(int upLower = lower; upLower <= upper; upLower += step) {
            for (int upUpper = upLower + step; upUpper <= upper; upUpper += step) {
                for(int downLower = lower; downLower <= upper; downLower += step) {
                    for (int downUpper = downLower + step; downUpper <= upper; downUpper += step) {
                        res.add(new double[]{upLower, upUpper, downLower, downUpper});
                    }
                }
            }
        }
        return res;
    }

    public void setProfit(TestResult profit) {
        this.profit = profit;
    }

    @Override
    public int compareTo(EMACombination o) {
        return profit.compareTo(o.profit);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Profit: ").append(String.format("%.2f", profit.getTotalProfit())).append(", EMA: [");
        for(double val : EMA) {
            sb.append(val).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]\n");
        return sb.toString();
    }
}
