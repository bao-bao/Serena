package com.regrx.trade.util;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.control.KeySprite;
import com.regrx.trade.data.PriceData;
import com.regrx.trade.data.Status;

import java.util.concurrent.ExecutorService;

public class Trade {

    public static Status putBuying(PriceData currPrice, Status status, ExecutorService newCachedThreadPool) {
        status.setStatus(Constant.PUT_BUYING);
        if(Constant.TEST_LABEL == 0) {
            newCachedThreadPool.submit(new KeySprite("P"));
        }
        status.setLastTradePrice(currPrice.getPrice());
        return status;
    }

    public static Status shortSelling(PriceData currPrice, Status status, ExecutorService newCachedThreadPool) {
        status.setStatus(Constant.SHORT_SELLING);
        if(Constant.TEST_LABEL == 0) {
            newCachedThreadPool.submit(new KeySprite("S"));
        }
        status.setLastTradePrice(currPrice.getPrice());
        return status;
    }

    public static Status empty(PriceData currPrice, Status status, ExecutorService newCachedThreadPool) {
        status.setStatus(Constant.EMPTY);
        if(Constant.TEST_LABEL == 0) {
            newCachedThreadPool.submit(new KeySprite("E"));
        }
        status.setLastTradePrice(currPrice.getPrice());
        return status;
    }
}
