package com.regrx.trade.control;

import java.awt.*;
import java.awt.event.InputEvent;

public class KeySprite {

    public static void PutBuying() {
        try {
            Robot r = new Robot();
            // press put buying
            r.mouseMove(60, 860);
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
            r.mouseMove(170, 860);
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
            r.mouseMove(280, 860);
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
            // press put buying
            r.mouseMove(430, 975);
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
            // press put buying
            r.mouseMove(1040, 600);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void Confirm() {
        try {
            Robot r = new Robot();
            r.mouseMove(960, 605);
            r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
