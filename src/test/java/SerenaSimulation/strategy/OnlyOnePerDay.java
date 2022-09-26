package SerenaSimulation.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.TradingType;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.data.base.Status;

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
    public Decision check(Decision origin, int hour, int minute) {
        tryReset(hour, minute);
        if(origin.isExecute() && origin.getTradingType() == TradingType.PUT_BUYING) {
            if(putBuyingRemain == 0) {
                if(Status.getInstance().getStatus() == TradingType.SHORT_SELLING) {
                    origin.make(TradingType.EMPTY, origin.getReason());
                } else {
                    origin.make(TradingType.NO_ACTION, "Denied");
                }
            } else {
                putBuyingRemain--;
            }
        }
        if (origin.isExecute() && origin.getTradingType() == TradingType.SHORT_SELLING) {
            if(shortSellingRemain == 0) {
                if(Status.getInstance().getStatus() == TradingType.PUT_BUYING) {
                    origin.make(TradingType.EMPTY, origin.getReason());
                } else {
                    origin.make(TradingType.NO_ACTION, "Denied");
                }
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

    private void tryReset(int hour, int minute) {
        if(hour == 9 && minute >= 20 && minute <= 31) {
            putBuyingRemain = Setting.PUT_BUYING_LIMIT;
            shortSellingRemain = Setting.SHORT_SELLING_LIMIT;
        }
    }
}
