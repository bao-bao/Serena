package SerenaSimulation.profit;

public class WindowResult {
    String name;
    Double profit;
    int count;

    public WindowResult(String name, Double profit, int count) {
        this.name = name;
        this.profit = profit;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit(Double profit) {
        this.profit = profit;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
