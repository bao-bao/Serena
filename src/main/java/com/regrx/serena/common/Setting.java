package com.regrx.serena.common;

public class Setting {
    public static final boolean TEST_LABEL = true;

    public static final int START_FAST_TRADE = 0;       // MUST BE EVEN

    public static final double TRADE_THRESHOLD = 0.5;

    //TODO: add final HERE
    public static int PROFIT_LIMIT_THRESHOLD = 13;

    public static int LOSS_LIMIT_THRESHOLD = 12;

    public static int RESTORE_THRESHOLD = 10;

    // mouse control settings
    public static final int OPERATION_SPEED_MULTIPLIER = 500;   // unit is millisecond

    public static final int FOLLOW_TIME = 2;

    public static final int FOLLOW_RETRY_INTERVAL = 10000;

    //TODO: DONT TOUCH ANYTHING BELOW HERE!!!
    public static final int MAX_LENGTH = TEST_LABEL ? Integer.MAX_VALUE : 1000;

    public static final int MAX_DECISION_QUEUE_SIZE = 1;

    public static final int DEFAULT_LOSS_LIMIT_PRIORITY = 0;

    public static final int DEFAULT_MA_520_PRIORITY = 100;

    // Time pattern
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String STOCK_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd,HH:mm:ss";

    public static final String OTHER_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd HHmmss";
}
