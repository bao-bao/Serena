package com.regrx.serena;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.controller.Controller;

public class Serena {
    public static void main(String[] args) {
        Controller controller = Controller.getInstance("IC2212");

        controller.addDataTrack(IntervalEnum.MIN_1);
        controller.addDataTrack(IntervalEnum.MIN_2);
        controller.addDataTrack(IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_MA_240, IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_MA_240_520, IntervalEnum.MIN_5);
        controller.addStrategy(StrategyEnum.STRATEGY_ONLY_ONE_PER_DAY, IntervalEnum.NULL);

        controller.run();
    }
}
