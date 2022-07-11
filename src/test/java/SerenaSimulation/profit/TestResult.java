package SerenaSimulation.profit;

public class TestResult implements Comparable<TestResult> {
    Double totalProfit;
    int pLoss;
    int pProfit;
    int sLoss;
    int sProfit;

    public double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public int getPutLoss() {
        return pLoss;
    }

    public void setPutLoss(int pLoss) {
        this.pLoss = pLoss;
    }

    public int getPutProfit() {
        return pProfit;
    }

    public void setPutProfit(int pProfit) {
        this.pProfit = pProfit;
    }

    public int getShortLoss() {
        return sLoss;
    }

    public void setShortLoss(int sLoss) {
        this.sLoss = sLoss;
    }

    public int getShortProfit() {
        return sProfit;
    }

    public void setShortProfit(int sProfit) {
        this.sProfit = sProfit;
    }

    public int getTotalCount() {
        return pProfit + pLoss + sProfit + sLoss;
    }

    @Override
    public String toString() {
        return "\t" + pProfit + "\t\t"+ pLoss + "\t\t" + sProfit + "\t\t" + sLoss + "\t\t" + (pProfit + pLoss + sProfit + sLoss);
    }

    @Override
    public int compareTo(TestResult o) {
        return totalProfit.compareTo(o.totalProfit);
    }
}
