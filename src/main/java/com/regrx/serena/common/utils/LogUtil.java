package com.regrx.serena.common.utils;

import com.regrx.serena.common.Setting;
import com.regrx.serena.common.constant.IntervalEnum;
import com.regrx.serena.data.base.Decision;
import com.regrx.serena.data.base.ExPrice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LogUtil {

    private static Logger logger;

    private LogUtil() {}

    public static Logger getInstance() {
        if(logger == null) {
            logger = Logger.getLogger("SerenaLog");
            if(!Setting.TEST_LABEL) {
                try {
                    logger.addHandler(new FileHandler("Serena.log"));
                } catch (IOException e) {
                    logger.warning("Log file handler link fail!");
                }
            }
        }
        return logger;
    }

    private static String basicTradeLog(Decision decision) {
        return decision.getTradingType() + " at " + decision.getPrice().getTime() +
                " for " + decision.getPrice().getPrice() +
                " under " + decision.getInterval() + " minute data" +
                ", Reason: " + decision.getReason() +
                ", Current: " + decision.getTradingType();
    }

    public static void tradeLog(String type, Decision decision) {
        FileUtil.writeTradeHistory("Trade_" + type, basicTradeLog(decision));
        //logger.info(basicTradeLog(decision));
    }

}
