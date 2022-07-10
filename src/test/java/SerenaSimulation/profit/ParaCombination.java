package SerenaSimulation.profit;

public class ParaCombination implements Comparable<ParaCombination> {
    public TestResult profit;
    public double[] paraArray;

    public ParaCombination() {
        paraArray = new double[4];
    }

    public void setProfit(TestResult profit) {
        this.profit = profit;
    }

    public void setParaArray(double loss, double profit, double restore, double ma) {
        paraArray[0] = loss;
        paraArray[1] = profit;
        paraArray[2] = restore;
        paraArray[3] = ma;
    }

//    public void setProfitCount(int profitCount) {
//        this.profitCount = profitCount;
//    }
//
//    public void setLossCount(int lossCount) {
//        this.lossCount = lossCount;
//    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%.2f", profit.getTotalProfit())).append("\t\t\t");
        sb.append("[ ");
        for (double para : paraArray) {
            sb.append(String.format("%.1f", para)).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1).append("]\t");
        sb.append(profit);
        return sb.toString();
    }

    @Override
    public int compareTo(ParaCombination o) {
        return profit.compareTo(o.profit);
    }
}
