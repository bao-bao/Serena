package com.regrx.serena.common.utils;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.StrategyEnum;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.service.KeySprite;
import com.regrx.serena.data.base.Status;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TradeUtil {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private TradeUtil() {
    }

    public static boolean trade(Decision decision, String type) {
        Status stat = Status.getInstance();
        boolean result = false;
        if (decision.getSource() == StrategyEnum.STRATEGY_CHECK_MAIN_CONTRACT) {
            result = submit('E', type);
            updateStatus(decision, result);
            switch (decision.getTradingType()) {
                case PUT_BUYING:
                    result = submit('P', type);
                    updateStatus(decision, result);
                    break;
                case SHORT_SELLING:
                    result = submit('S', type);
                    updateStatus(decision, result);
                    break;
                default:
                    break;
            }
            return result;
        }
        switch (decision.getTradingType()) {
            case PUT_BUYING:
                if (stat.getStatus() == TradingType.SHORT_SELLING) {
                    result = submit('E', type);
                    updateStatus(decision, result);
                }
                result = submit('P', type);
                updateStatus(decision, result);
                break;
            case SHORT_SELLING:
                if (stat.getStatus() == TradingType.PUT_BUYING) {
                    result = submit('E', type);
                    updateStatus(decision, result);
                }
                result = submit('S', type);

                updateStatus(decision, result);
                break;
            case EMPTY:
                result = submit('E', type);
                updateStatus(decision, result);
                break;
            case NO_ACTION:
                break;
        }

        return result;
    }

    private static boolean submit(char label, String type) {
        if (!Setting.TEST_LABEL) {
            Future<Boolean> future = threadPool.submit(new KeySprite(label, type));
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException ignored) {
                LogUtil.getInstance().severe("Error when try to trade");
            }
        }
        return true;
    }

    private static void updateStatus(Decision decision, boolean result) {
        if (result) {
            Status stat = Status.getInstance();
            stat.setLastTradePrice(decision.getPrice().getPrice());
            stat.setLastTradeTime(decision.getPrice().getTime());
            stat.setStatus(decision.getTradingType());
            stat.setStrategy(decision.getSource());
            stat.setInterval(decision.getInterval());
        }
    }

    public static void forceEmpty(String type) {
        submit('E', type);
    }
}
