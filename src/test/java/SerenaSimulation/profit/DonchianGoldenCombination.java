package SerenaSimulation.profit;

public class DonchianGoldenCombination implements Comparable<DonchianGoldenCombination> {
    TestResult profit;
    int aggrCount;
    double L1;
    double LC1;
    double S1;
    double SC1;


    public DonchianGoldenCombination(int cnt, double a, double b, double c, double d) {
        aggrCount = cnt;
        L1 = a;
        LC1 = b;
        S1 = c;
        SC1 = d;
    }

    public void setProfit(TestResult profit) {
        this.profit = profit;
    }

    @Override
    public int compareTo(DonchianGoldenCombination o) {
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
        sb.append(String.format("%.2f", profit.getStdDev())).append(",");
        sb.append(String.format("%.2f", profit.getSharpRatio())).append(",");

        sb.append(String.format("%d", aggrCount)).append(",");
        sb.append(String.format("%.2f", L1)).append(",");
        sb.append(String.format("%.2f", LC1)).append(",");
        sb.append(String.format("%.2f", S1)).append(",");
        sb.append(String.format("%.2f", SC1)).append(",");
        sb.append("\n");
        return sb.toString();
    }
}
