package SerenaSimulation.profit;

import com.regrx.serena.common.Setting;

public class BollingerCombination implements Comparable<BollingerCombination> {
    TestResult profit;
    double SD;
    double B2;
    double LC1;
    double LC2;
    double S2;
    double SC1;
    double SC2;


    public BollingerCombination(double z, double a, double b, double c, double d, double e, double f) {
        SD = z;
        B2 = a;
        LC1 = b;
        LC2 = c;
        S2 = d;
        SC1 = e;
        SC2 = f;
    }

    public void setProfit(TestResult profit) {
        this.profit = profit;
    }

    @Override
    public int compareTo(BollingerCombination o) {
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

        sb.append(String.format("%.2f", SD)).append(",");
        sb.append(String.format("%.2f", B2)).append(",");
        sb.append(String.format("%.2f", LC1)).append(",");
        sb.append(String.format("%.2f", LC2)).append(",");
        sb.append(String.format("%.2f", S2)).append(",");
        sb.append(String.format("%.2f", SC1)).append(",");
        sb.append(String.format("%.2f", SC2)).append(",");
        sb.append("\n");
        return sb.toString();
    }
}
