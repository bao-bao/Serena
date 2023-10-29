package ControlTest;

import com.regrx.serena.common.utils.LogUtil;
import com.regrx.serena.controller.Controller;
import com.regrx.serena.service.KeySprite;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestMouseClick {

    public static void main(String[] args) {
        // P -> put buying
        // S -> short selling
        // E -> empty
        // A -> empty + put buying
        // B -> empty + short selling

        char label = 'S';
        String type = "IF0";

        Controller.getInstance(type);
        ExecutorService threadPool = Executors.newCachedThreadPool();
        Future<Boolean> future = threadPool.submit(new KeySprite(label, type));
        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException ignored) {
            LogUtil.getInstance().severe("Error when try to trade");
        }
        threadPool.shutdown();
    }
}

