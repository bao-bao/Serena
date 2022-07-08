package com.regrx.serena.common;

public class Setting {
    public static final boolean TEST_LABEL = true;

    public static final int START_FAST_TRADE = 0;       // MUST BE EVEN

    //TODO: add final HERE
    final public static double TRADE_THRESHOLD = 0;

    final public static double PROFIT_LIMIT_THRESHOLD = 15;

    final public static double LOSS_LIMIT_THRESHOLD = 23;

    final public static double RESTORE_THRESHOLD = 19;

    // mouse control settings
    public static final int OPERATION_SPEED_MULTIPLIER = 500;   // unit is millisecond

    public static final int FOLLOW_TIME = 2;

    public static final int FOLLOW_RETRY_INTERVAL = 10000;

    //TODO: DONT TOUCH ANYTHING BELOW HERE!!!
    public static final int MAX_LENGTH = TEST_LABEL ? Integer.MAX_VALUE : 1000;

    public static final int MAX_DECISION_QUEUE_SIZE = 1;

    // strategy priority
    public static final int BLOCK_LOW_PRIORITY_STRATEGY = 10000;

    public static final int DEFAULT_FORCE_TRIGGER_PRIORITY = 0;

    public static final int HIGH_LOSS_LIMIT_PRIORITY = 100;

    public static final int DEFAULT_LOSS_LIMIT_PRIORITY = Integer.MAX_VALUE;

    public static final int HIGH_PROFIT_LIMIT_PRIORITY = 100;

    public static final int DEFAULT_PROFIT_LIMIT_PRIORITY = Integer.MAX_VALUE;

    public static final int DEFAULT_MA_520_PRIORITY = 500;

    // Time pattern
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String STOCK_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd,HH:mm:ss";

    public static final String OTHER_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd HHmmss";
}
