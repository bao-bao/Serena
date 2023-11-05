package SerenaSimulation.profit;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class TestResult implements Comparable<TestResult> {
    Double totalProfit;
    int pLoss;
    int pProfit;
    int sLoss;
    int sProfit;
    String CLTime;

    double APPT;
    double EVPT;
    double Odds;
    double Kelly;
    double EVUR;
    double StdDev;
    double SharpRatio;
    double maxLoss;

    ArrayList<Pair<String, Double>> profitByMonth;
    ArrayList<Pair<String, Double>> profitByWeek;
    double varianceByMonth;
    double varianceByWeek;

    public TestResult() {
        profitByMonth = new ArrayList<>();
        profitByWeek = new ArrayList<>();
    }

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

    public String getCLTime() {
        return CLTime;
    }

    public void setCLTime(String CLTime) {
        this.CLTime = CLTime;
    }

    public double getAPPT() {
        return APPT;
    }

    public void setAPPT(double APPT) {
        this.APPT = APPT;
    }

    public double getEVPT() {
        return EVPT;
    }

    public void setEVPT(double EVPT) {
        this.EVPT = EVPT;
    }

    public double getOdds() {
        return Odds;
    }

    public void setOdds(double odds) {
        Odds = odds;
    }

    public double getKelly() {
        return Kelly;
    }

    public void setKelly(double kelly) {
        Kelly = kelly;
    }

    public double getEVUR() {
        return EVUR;
    }

    public void setEVUR(double EVUR) {
        this.EVUR = EVUR;
    }

    public double getMaxLoss() {
        return maxLoss;
    }

    public void setMaxLoss(double maxLoss) {
        this.maxLoss = maxLoss;
    }

    public double getStdDev() {
        return StdDev;
    }

    public void setStdDev(double stdDev) {
        StdDev = stdDev;
    }

    public double getSharpRatio() {
        return SharpRatio;
    }

    public void setSharpRatio(double sharpRatio) {
        SharpRatio = sharpRatio;
    }

    public ArrayList<Pair<String, Double>> getProfitByMonth() {
        return profitByMonth;
    }

    public ArrayList<Pair<String, Double>> getProfitByWeek() {
        return profitByWeek;
    }

    public double getVarianceByMonth() {
        return varianceByMonth;
    }

    public void setVarianceByMonth(double varianceByMonth) {
        this.varianceByMonth = varianceByMonth;
    }

    public double getVarianceByWeek() {
        return varianceByWeek;
    }

    public void setVarianceByWeek(double varianceByWeek) {
        this.varianceByWeek = varianceByWeek;
    }

    @Override
    public String toString() {
        return "\t" + pProfit + "\t\t"+ pLoss + "\t\t" + sProfit + "\t\t" + sLoss + "\t\t" + (pProfit + pLoss + sProfit + sLoss) + "\t\t" + CLTime;
    }

    @Override
    public int compareTo(TestResult o) {
        return totalProfit.compareTo(o.totalProfit);
    }
}
