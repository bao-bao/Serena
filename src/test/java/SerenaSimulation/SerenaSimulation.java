package SerenaSimulation;

import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.constant.StrategyEnum;

public class SerenaSimulation {

    public static void main(String[] args) {
        ControllerTest controller = ControllerTest.getInstance("IF2203");

        controller.addStrategy(StrategyEnum.STRATEGY_LOSS_LIMIT, IntervalEnum.MIN_1);
        controller.addStrategy(StrategyEnum.STRATEGY_MA_520, IntervalEnum.MIN_5);

        controller.run();
    }

}
