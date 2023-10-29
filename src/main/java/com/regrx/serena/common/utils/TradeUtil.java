package com.regrx.serena.common.utils;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.service.KeySprite;
import com.regrx.serena.data.base.ExPrice;
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
        switch (decision.getTradingType()) {
            case PUT_BUYING:
                if (stat.getStatus() == TradingType.SHORT_SELLING) {
                    result = submit('E', type);
                    if (result) {
                        updateStatus(decision);
                    }
                }
                result = submit('P', type);
                if (result) {
                    updateStatus(decision);
                }
                break;
            case SHORT_SELLING:
                if (stat.getStatus() == TradingType.PUT_BUYING) {
                    result = submit('E', type);
                    if (result) {
                        updateStatus(decision);
                    }
                }
                result = submit('S', type);

                if (result) {
                    updateStatus(decision);
                }
                break;
            case EMPTY:
                result = submit('E', type);
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

    private static void updateStatus(Decision decision) {
        Status stat = Status.getInstance();
        stat.setLastTradePrice(decision.getPrice().getPrice());
        stat.setLastTradeTime(decision.getPrice().getTime());
        stat.setStatus(decision.getTradingType());
        stat.setStrategy(decision.getSource());
        stat.setInterval(decision.getInterval());
    }

    public static void forceEmpty(String type) {
        submit('E', type);
    }
}
