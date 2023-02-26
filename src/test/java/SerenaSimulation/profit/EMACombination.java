package SerenaSimulation.profit;


import java.util.ArrayList;

public class EMACombination implements Comparable<EMACombination> {
    public TestResult profit;
    public double[] EMA;
    public double profitThreshold;
    public double profitLimit;
    public double lossLimit;

    public EMACombination(double[] ema, double profitThreshold, double profitLimit, double lossLimit) {
        EMA = ema.clone();
        this.profitThreshold = profitThreshold;
        this.profitLimit = profitLimit;
        this.lossLimit = lossLimit;
    }

    public static ArrayList<double[]> generateEMA(int lower, int upper, int step, boolean upSide, boolean downSide) {
        ArrayList<double[]> res = new ArrayList<>();
        if(downSide && !upSide) {
            for(int downLower = lower; downLower <= upper; downLower += step) {
                for (int downUpper = downLower + step; downUpper <= upper; downUpper += step) {
                    res.add(new double[]{0, 0, downLower, downUpper});
                }
            }
        } else if(upSide) {
            for(int upLower = lower; upLower <= upper; upLower += step) {
                for (int upUpper = upLower + step; upUpper <= upper; upUpper += step) {
                    if(downSide) {
                        for(int downLower = lower; downLower <= upper; downLower += step) {
                            for (int downUpper = downLower + step; downUpper <= upper; downUpper += step) {
                                res.add(new double[]{upLower, upUpper, downLower, downUpper});
                            }
                        }
                    } else {
                        res.add(new double[]{upLower, upUpper, 0, 0});
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
        sb.append("Profit: ").append(String.format("%.2f", profit.getTotalProfit()));

        sb.append(", Total Count: ").append(profit.getTotalCount());
        double winCount = profit.getShortProfit() + profit.getPutProfit();
        sb.append(", Win Rate: ").append(String.format("%.2f", (winCount / profit.getTotalCount()) * 100)).append("%");

        sb.append(", EMA: [");
        for(double val : EMA) {
            sb.append(val).append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");

        sb.append(", Profit Threshold: ").append(String.format("%.2f", profitThreshold * 100)).append("%");
        sb.append(", Profit Limit: ").append(String.format("%.2f", profitLimit * 100)).append("%");
        sb.append(", Loss Limit: ").append(String.format("%.2f", lossLimit * 100)).append("%");
        sb.append("\n");
        return sb.toString();
    }
}
