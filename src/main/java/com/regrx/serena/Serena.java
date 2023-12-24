package com.regrx.serena;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.strategy.LoginAccount;
import com.regrx.serena.strategy.LoginAccountV2;

public class Serena {
    public static void main(String[] args) {
        //new Thread(new LoginAccount(9, 0)).start();
        new Thread(new LoginAccountV2(14, 48)).start();

        Controller controller = Controller.getInstance("IF0");

        controller.addDataTrack(IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_BASIC_EMA_FOR_UP, IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_BASIC_EMA_FOR_DOWN, IntervalEnum.MIN_1);
        controller.run();
    }
}
