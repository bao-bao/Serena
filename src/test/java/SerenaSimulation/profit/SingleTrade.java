package SerenaSimulation.profit;

public class SingleTrade implements Comparable<SingleTrade> {
    public String openTime;
    public String closeTime;
    public String tradeType;
    public Double profit;

    SingleTrade(String openTime) {
        this.openTime = openTime;
        closeTime = "";
        tradeType = "";
        profit = 0.0;
    }

    @Override
    public int compareTo(SingleTrade o) {
        return profit.compareTo(o.profit);
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }
}
