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

    private TradeUtil() {}

    public static boolean trade(Decision decision) {
        Status stat = Status.getInstance();
        boolean result  = false;
        switch (decision.getTradingType()) {
            case PUT_BUYING:
                if(stat.getStatus() == TradingType.SHORT_SELLING) {
                    result = submit('A');
                } else {
                    result = submit('P');
                }
                break;
            case SHORT_SELLING:
                if(stat.getStatus() == TradingType.PUT_BUYING) {
                    result = submit('B');
                } else {
                    result = submit('S');
                }
                break;
            case EMPTY:         result = submit('E'); break;
            case NO_ACTION: break;
        }
        if(result) {
            stat.setLastTradePrice(decision.getPrice().getPrice());
            stat.setStatus(decision.getTradingType());
            stat.setStrategy(decision.getSource());
            stat.setInterval(decision.getInterval());
        }
        return result;
    }

    private static boolean submit(char label) {
        if(!Setting.TEST_LABEL) {
            Future<Boolean> future = threadPool.submit(new KeySprite(label));
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException ignored) {
                LogUtil.getInstance().severe("Error when try to trade");
            }
        }
        return true;
    }

    public static void forceEmpty() {
        submit('E');
    }
}
