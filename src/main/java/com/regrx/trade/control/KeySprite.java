package com.regrx.trade.control;

import com.regrx.trade.constant.Constant;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.concurrent.Callable;

public class KeySprite implements Callable<Void> {
    private final String operate;


    public KeySprite(String operate) {
        this.operate = operate;
    }

    @Override
    public Void call() {
        switch (operate) {
            case "P": KeySprite.PutBuying();break;
            case "S": KeySprite.ShortSelling();break;
            case "E": KeySprite.Empty();break;
        }
        return null;
    }

    // consume 1s + (n x 13) s
    public static void PutBuying() {
        try {
            Robot r = new Robot();
            // press put buying
            r.mouseMove(260, 870);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(1000);
            // press confirm
            Confirm();
            // follow
            FollowNTime();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // consume 1s + (n x 13) s
    public static void ShortSelling() {
        try {
            Robot r = new Robot();
            // press put buying
            r.mouseMove(370, 870);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(1000);
            // press confirm
            Confirm();
            // follow
            FollowNTime();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // consume 2s + (n x 13) s
    public static void Empty() {
        try {
            Robot r = new Robot();
            // press put buying
            r.mouseMove(480, 870);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(1000);
            // press confirm
            Confirm();
            r.delay(1000);
            // try close warning
            CloseWarning();
            // follow
            FollowNTime();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // consume 3s
    public static void Follow() {
        try {
            Robot r = new Robot();
            // select the possible stacked
            r.mouseMove(660, 955);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(1000);
            // press follow button
            r.mouseMove(1813, 993);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(1000);
            // press confirm
            Confirm();
            r.delay(1000);
            // try close warning
            CloseWarning();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // consume 0s
    private static void CloseWarning() {
        try {
            Robot r = new Robot();
            // press warning
            r.mouseMove(1040, 580);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // consume 0s
    private static void Confirm() {
        try {
            Robot r = new Robot();
            r.mouseMove(960, 587);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // consume n x (10s + 3s)
    private static void FollowNTime() {
        for (int i = 0; i < Constant.FOLLOW_TIME; i++) {
            try {
                Robot r = new Robot();
                r.delay(10000);
                Follow();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

}
