package com.regrx.serena.service;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.utils.LogUtil;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;

public class KeySprite implements Callable<Boolean> {
    private final char operate;
    private final String type;


    public KeySprite(char operate, String type) {
        this.operate = operate;
        this.type = type;
    }

    @Override
    public Boolean call() {
        switch (operate) {
            case 'P': return PutBuying();
            case 'S': return ShortSelling();
            case 'E': return Empty();
            case 'A': return Empty() && PutBuying();
            case 'B': return Empty() && ShortSelling();
            default:
                return false;
        }
    }

    private boolean Select() {
        try {
            Robot r = new Robot();
            // press input block
            r.mouseMove(Setting.SELECT_POSITION_X, Setting.SELECT_POSITION_Y);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(20);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            // delete former string
            r.keyPress(KeyEvent.VK_BACK_SPACE);
            r.keyRelease(KeyEvent.VK_BACK_SPACE);
            r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            // input new
            for (char c : type.substring(0, 2).toCharArray()) {
                r.keyPress(c);
                r.keyRelease(c);
                r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            }
            // double-click enter to choose new item
            for (int i = 0; i < 2; i++) {
                r.keyPress(KeyEvent.VK_ENTER);
                r.keyRelease(KeyEvent.VK_ENTER);
                r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            }
            return true;
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
            return false;
        }
    }

    // consume 1s
    public boolean PutBuying() {
        try {
            Robot r = new Robot();
            if (!Select()) {
                return false;
            }
            // press put buying
            for(int i = 0; i < Setting.TRADE_OPERATE_REPEAT; i++) {
                r.mouseMove(Setting.PUT_POSITION_X, Setting.PUT_POSITION_Y);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
                // press confirm and follow
                if (Confirm()) {
                    return false;
                }
                r.delay(Setting.OPERATION_RETRY_INTERVAL);
            }
            if (FollowNTime()) {
                return false;
            }
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
            return false;
        }
        return true;
    }

    // consume 1s
    public boolean ShortSelling() {
        try {
            Robot r = new Robot();
            if (!Select()) {
                return false;
            }
            // press short selling
            for(int i = 0; i < Setting.TRADE_OPERATE_REPEAT; i++) {
                r.mouseMove(Setting.SHORT_POSITION_X, Setting.SHORT_POSITION_Y);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
                // press confirm and follow
                if (Confirm()) {
                    return false;
                }
                r.delay(Setting.OPERATION_RETRY_INTERVAL);
            }
            if (FollowNTime()) {
                return false;
            }
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
            return false;
        }
        return true;
    }

    // consume 1s
    public boolean Empty() {
        try {
            Robot r = new Robot();
            if (!Select()) {
                return false;
            }
            // press empty
            for(int i = 0; i < Setting.TRADE_OPERATE_REPEAT; i++) {
                r.mouseMove(Setting.EMPTY_POSITION_X, Setting.EMPTY_POSITION_Y);
                r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
                if (Confirm()) {
                    return false;
                }
                r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
                if (CloseWarning()) {
                    return false;
                }
                r.delay(Setting.OPERATION_RETRY_INTERVAL);
            }
            if (FollowNTime()) {
                return false;
            }
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
            return false;
        }
        return true;
    }

    // consume 1.5s
    public static boolean Follow() {
        try {
            Robot r = new Robot();
            // select the possible stacked
            r.mouseMove(Setting.FOLLOW_SELECT_POSITION_X, Setting.FOLLOW_SELECT_POSITION_Y);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            // press follow button
            r.mouseMove(Setting.FOLLOW_CLICK_POSITION_X, Setting.FOLLOW_CLICK_POSITION_Y);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            // press confirm
            if (Confirm()) {
                return false;
            }
            r.delay(Setting.OPERATION_SPEED_MULTIPLIER);
            // try close warning
            if (CloseWarning()) {
                return false;
            }
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
            return false;
        }
        return true;
    }

    // consume 0s
    private static boolean CloseWarning() {
        try {
            Robot r = new Robot();
            // press warning
            r.mouseMove(Setting.CLOSE_WARNING_POSITION_X, Setting.CLOSE_WARNING_POSITION_Y);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
            return true;
        }
        return false;
    }

    // consume 0s
    private static boolean Confirm() {
        try {
            Robot r = new Robot();
            r.mouseMove(Setting.CONFIRM_POSITION_X, Setting.CONFIRM_POSITION_Y);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException ignored) {
            LogUtil.getInstance().severe("AWT Error!");
            return true;
        }
        return false;
    }

    // consume n x (10s + 1.5s)
    private static boolean FollowNTime() {
        for (int i = 0; i < Setting.FOLLOW_TIME; i++) {
            try {
                Robot r = new Robot();
                r.delay(Setting.FOLLOW_RETRY_INTERVAL);
                if (!Follow()) {
                    return true;
                }
            } catch (AWTException ignored) {
                LogUtil.getInstance().severe("AWT Error!");
                return true;
            }
        }
        return false;
    }

    public static boolean MouseClick(Robot r, int x, int y) {
        r.mouseMove(x, y);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.delay(Setting.MOUSE_CLICK_PRESS_TIME);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        return true;
    }
}
