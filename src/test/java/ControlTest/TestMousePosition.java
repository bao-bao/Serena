package ControlTest;

import com.regrx.trade.control.KeySprite;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TestMousePosition {

    public static void main(String[] args) throws InterruptedException {

        while(true == true)
        {
            TimeUnit.SECONDS.sleep(1);
            double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
            double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
            System.out.println("X:" + mouseX);
            System.out.println("Y:" + mouseY);
            //make sure to import
        }

    }

}
