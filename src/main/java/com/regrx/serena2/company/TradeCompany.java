package com.regrx.serena2.company;

import com.regrx.serena2.common.constant.IntervalEnum;
import com.regrx.serena2.common.constant.StrategyEnum;
import com.regrx.serena2.employee.Financer;
import com.regrx.serena2.employee.Trader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class TradeCompany {
    private final HashMap<String, Trader> tradeDep;
    private final Financer financer;


    public TradeCompany() {
        this.tradeDep = new HashMap<>();
        this.financer = new Financer();
        this.financer.run();
    }

    public void registerTrader(String type, Trader newbie) {
        this.tradeDep.put(type, newbie);
        newbie.run();
    }

    public void fireTrader(String type) {
        if(tradeDep.containsKey(type)) {
            Trader fired = tradeDep.get(type);
            fired.leave();
            tradeDep.remove(type);
        }
    }

}
