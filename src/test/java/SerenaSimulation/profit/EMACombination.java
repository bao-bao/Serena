package SerenaSimulation.profit;


import java.util.ArrayList;

public class EMACombination implements Comparable<EMACombination> {
    public TestResult profit;
    public double[] EMA;
    public double profitThreshold;
    public double profitLimit;
    public double lossLimit;
    public double dProfitThreshold;
    public double dProfitLimit;
    public double dLossLimit;

    public EMACombination(double[] ema, double profitThreshold, double profitLimit, double lossLimit, double dProfitThreshold, double dProfitLimit, double dLossLimit) {
        EMA = ema.clone();
        this.profitThreshold = profitThreshold;
        this.profitLimit = profitLimit;
        this.lossLimit = lossLimit;
        this.dProfitThreshold = dProfitThreshold;
        this.dProfitLimit = dProfitLimit;
        this.dLossLimit = dLossLimit;
    }

    public static ArrayList<double[]> generateEMA(int lower, int upper, int step, boolean upSide, boolean downSide) {
        ArrayList<double[]> res = new ArrayList<>();
        if(!upSide && downSide) {
            for(int downLower = lower; downLower <= upper; downLower += step) {
                for (int downUpper = downLower + step; downUpper <= upper; downUpper += step) {
                    res.add(new double[]{0, 0, downLower, downUpper});
                }
            }
        } else if(upSide && !downSide) {
            for(int upLower = lower; upLower <= upper; upLower += step) {
                for (int upUpper = upLower + step; upUpper <= upper; upUpper += step) {
                        res.add(new double[]{upLower, upUpper, 0, 0});
                    }
            }
        } else if(!upSide) {
            for(int upLower = lower; upLower <= upper; upLower += step) {
                for (int upUpper = upLower + step; upUpper <= upper; upUpper += step) {
                        for(int downLower = lower; downLower <= upper; downLower += step) {
                            for (int downUpper = downLower + step; downUpper <= upper; downUpper += step) {
                                res.add(new double[]{upLower, upUpper, downLower, downUpper});
                            }
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
        double winCount = profit.getShortProfit() + profit.getPutProfit();

        sb.append(String.format("%.2f", profit.getTotalProfit())).append(",");
        sb.append(profit.getTotalCount()).append(",");
        sb.append(String.format("%.2f", (winCount / profit.getTotalCount()) * 100)).append("%,");
        sb.append(String.format("%.2f", profit.getAPPT())).append(",");
        sb.append(String.format("%.2f", profit.getEVPT())).append(",");
        sb.append(String.format("%.2f", profit.getEVUR())).append(",");
        sb.append(String.format("%.2f", profit.getKelly())).append("%,");
        sb.append(String.format("%.2f", profit.getOdds())).append(",");
        sb.append(String.format("%.2f", profit.getMaxLoss())).append(",");
        sb.append(String.format("%.2f", profit.getSharpRatio())).append(",");

        sb.append("[");
        for(double val : EMA) {
            sb.append(val).append(" ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("],");

        sb.append(String.format("%.2f", profitThreshold * 100)).append("%,");
        sb.append(String.format("%.2f", profitLimit * 100)).append("%,");
        sb.append(String.format("%.2f", lossLimit * 100)).append("%,");
        sb.append(String.format("%.2f", dProfitThreshold * 100)).append("%,");
        sb.append(String.format("%.2f", dProfitLimit * 100)).append("%,");
        sb.append(String.format("%.2f", dLossLimit * 100)).append("%");
        sb.append("\n");
        return sb.toString();
    }
}
