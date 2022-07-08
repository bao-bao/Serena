package com.regrx.serena;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.controller.Controller;

public class Serena {
    public static void main(String[] args) {
        Controller controller = Controller.getInstance("IF2212");

        controller.addDataTrack(IntervalEnum.MIN_1);
        controller.addDataTrack(IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_MA_520, IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_LOSS_LIMIT, IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_CLOSE_ON_END, IntervalEnum.NULL);

        controller.run();
    }
}
