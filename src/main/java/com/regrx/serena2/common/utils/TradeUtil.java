package com.regrx.serena2.common.utils;

import com.regrx.serena2.common.Setting;
import com.regrx.serena2.data.base.Decision;
import com.regrx.serena2.data.base.Status;
import com.regrx.serena2.service.KeySprite;
import com.regrx.serena2.common.constant.TradingType;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TradeUtil {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private TradeUtil() {}

    public static boolean trade(Decision decision, String type) {
        Status stat = Status.getInstance();
        boolean result  = false;
        switch (decision.getTradingType()) {
            case PUT_BUYING:
                if(stat.getStatus() == TradingType.SHORT_SELLING) {
                    result = submit('A', type);
                } else {
                    result = submit('P', type);
                }
                break;
            case SHORT_SELLING:
                if(stat.getStatus() == TradingType.PUT_BUYING) {
                    result = submit('B', type);
                } else {
                    result = submit('S', type);
                }
                break;
            case EMPTY:
                result = submit('E', type); break;
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

    private static boolean submit(char label, String type) {
        if(!Setting.TEST_LABEL) {
            Future<Boolean> future = threadPool.submit(new KeySprite(label, type));
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException ignored) {
                LogUtil.getInstance().severe("Error when try to trade");
            }
        }
        return true;
    }

    public static void forceEmpty(String type) {
        submit('E', type);
    }
}
