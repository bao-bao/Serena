package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;

import java.util.Calendar;
import java.util.TimeZone;

public class OnlyOnePerDay extends AfterCheckStrategy {

    private int putBuyingRemain = Setting.PUT_BUYING_LIMIT;
    private int shortSellingRemain = Setting.SHORT_SELLING_LIMIT;

    public OnlyOnePerDay() {
        super();
        super.setName("Only One Per Day");
    }

    @Override
    public Decision check(Decision origin) {
        tryReset();
        if(origin.isExecute() && origin.getTradingType() == TradingType.PUT_BUYING) {
            if(putBuyingRemain == 0) {
                origin.make(TradingType.NO_ACTION, "Denied");
            } else {
                putBuyingRemain--;
            }
        }
        if (origin.isExecute() && origin.getTradingType() == TradingType.SHORT_SELLING) {
            if(shortSellingRemain == 0) {
                origin.make(TradingType.NO_ACTION, "Denied");
            } else {
                shortSellingRemain--;
            }
        }
        return origin;
    }

    @Override
    public Decision execute(ExPrice price) {
        return null;
    }

    private void tryReset() {
        Calendar currTime = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int currMinute = currTime.get(Calendar.MINUTE);
        int currHour = currTime.get(Calendar.HOUR_OF_DAY);
        if(currHour == 9 && currMinute >= 20 && currMinute <= 30) {
            putBuyingRemain = Setting.PUT_BUYING_LIMIT;
            shortSellingRemain = Setting.SHORT_SELLING_LIMIT;
        }
    }
}
