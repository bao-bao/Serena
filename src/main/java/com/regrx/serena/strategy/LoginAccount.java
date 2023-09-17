package com.regrx.serena.strategy;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;
import com.regrx.serena.service.KeySprite;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

public class LoginAccount extends ForceTriggerStrategy implements Runnable {
    private static final int OP_INTERVAL = 60000;     // ms

    private static final int ACCOUNT_BUTT_POS_X = 1676;
    private static final int ACCOUNT_BUTT_POS_Y = 1063;

    private static final int POP_WINDOW_1_POS_X = 1193;
    private static final int POP_WINDOW_1_POS_Y = 358;
    private static final int POP_WINDOW_2_POS_X = 639;
    private static final int POP_WINDOW_2_POS_Y = 794;
    private static final int POP_WINDOW_3_POS_X = 561;
    private static final int POP_WINDOW_3_POS_Y = 792;
    private static final int POP_WINDOW_4_POS_X = 514;
    private static final int POP_WINDOW_4_POS_Y = 767;

    private static final ArrayList<Pair<Integer, Integer>> POP_WINDOWS = new ArrayList<>(
            Arrays.asList(
                    Pair.of(POP_WINDOW_1_POS_X, POP_WINDOW_1_POS_Y),
                    Pair.of(POP_WINDOW_1_POS_X, POP_WINDOW_1_POS_Y),
                    Pair.of(POP_WINDOW_2_POS_X, POP_WINDOW_2_POS_Y),
                    Pair.of(POP_WINDOW_3_POS_X, POP_WINDOW_3_POS_Y),
                    Pair.of(POP_WINDOW_4_POS_X, POP_WINDOW_4_POS_Y)
            )
    );

    private static final String PASSWORD = "A12345";

    public LoginAccount(int hour, int minute) {
        super(IntervalEnum.NULL);
        super.setName("Login Account");
        this.setTriggerTime(hour, minute);
    }

    @Override
    public Decision execute(ExPrice price) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (!Setting.TEST_LABEL && this.isTriggered(hour, minute)) {
            //if (weekday != Calendar.SATURDAY && weekday != Calendar.SUNDAY) {
            execOperation();
            //}
        }
        return null;
    }

    @Override
    public boolean isTriggered(int hour, int minute) {
        return hour == this.triggerHour && minute == this.triggerMinute;
    }

    private void execOperation() {
        System.out.println("start login");
        try {
            Robot r = new Robot();
            KeySprite.MouseClick(r, ACCOUNT_BUTT_POS_X, ACCOUNT_BUTT_POS_Y);
            r.delay(OP_INTERVAL);
            for (char c : PASSWORD.toCharArray()) {
                if (Character.isAlphabetic(c) && Character.isLowerCase(c)) {
                    c = Character.toUpperCase(c);
                    r.keyPress(c);
                    r.keyRelease(c);
                } else if (Character.isAlphabetic(c) && Character.isUpperCase(c)) {
                    r.keyPress(KeyEvent.VK_SHIFT);
                    r.keyPress(c);
                    r.keyRelease(c);
                    r.keyRelease(KeyEvent.VK_SHIFT);
                } else {
                    r.keyPress(c);
                    r.keyRelease(c);
                }
                r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            }
            r.delay(OP_INTERVAL);
            r.keyPress(KeyEvent.VK_ENTER);
            r.keyRelease(KeyEvent.VK_ENTER);
            for (Pair<Integer, Integer> pos : POP_WINDOWS) {
                r.delay(OP_INTERVAL);
                KeySprite.MouseClick(r, pos.getLeft(), pos.getRight());
            }
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.execute(null);
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
