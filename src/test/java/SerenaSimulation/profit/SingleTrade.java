package SerenaSimulation.profit;

public class SingleTrade implements Comparable<SingleTrade> {
    public String openTime;
    public String closeTime;
    public String closeDate;
    public String tradeType;
    public String openReason;
    public String closeReason;
    public Double profit;

    SingleTrade(String openTime) {
        this.openTime = openTime;
        closeTime = "";
        tradeType = "";
        profit = 0.0;
    }

    @Override
    public int compareTo(SingleTrade o) {
        return closeTime.compareTo(o.closeTime);
    }

    public void setCloseDate(String closeDate) {
        this.closeDate = closeDate;
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

    public void setOpenReason(String openReason) {
        this.openReason = openReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }
}
