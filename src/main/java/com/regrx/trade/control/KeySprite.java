package com.regrx.trade.control;

import java.awt.*;
import java.awt.event.InputEvent;

public class KeySprite {

    public static void PutBuying() {
        try {
            Robot r = new Robot();
            // press put buying
            r.mouseMove(260, 870);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(2000);
            // press confirm
            Confirm();
            r.delay(10000);
            // try follow
            Follow();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void ShortSelling() {
        try {
            Robot r = new Robot();
            // press put buying
            r.mouseMove(370, 870);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(2000);
            // press confirm
            Confirm();
            r.delay(10000);
            // try follow
            Follow();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    public static void Empty() {
        try {
            Robot r = new Robot();
            // press put buying
            r.mouseMove(480, 870);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(2000);
            // press confirm
            Confirm();
            r.delay(2000);
            // try close warning
            CloseWarning();
            r.delay(10000);
            // try follow
            Follow();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


    public static void Follow() {
        try {
            Robot r = new Robot();
            // select the possible stacked
            r.mouseMove(660, 955);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(2000);
            // press follow button
            r.mouseMove(1813, 993);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            r.delay(2000);
            // press confirm
            Confirm();
            r.delay(2000);
            // try close warning
            CloseWarning();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }


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
}
