package SerenaSimulation.profit;

public class ParaCombination implements Comparable<ParaCombination> {
    public Double profit;
//    public int profitCount;
//    public int lossCount;
    public double[] paraArray;

    public ParaCombination() {
        profit = 0.0;
        paraArray = new double[4];
    }

    public void setProfit(Double profit) {
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
        sb.append(String.format("%.2f", profit)).append("\t");
        sb.append("[ ");
        for (double para : paraArray) {
            sb.append(String.format("%.1f", para)).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length() - 1).append("]\t");
//        sb.append("profit Count: ").append(profitCount);
//        sb.append("loss Count: ").append(lossCount);
        return sb.toString();
    }

    @Override
    public int compareTo(ParaCombination o) {
        return profit.compareTo(o.profit);
    }
}
