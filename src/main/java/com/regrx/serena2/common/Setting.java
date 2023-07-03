package com.regrx.serena2.common;

public class Setting {
    public static final boolean TEST_LABEL = false;

    public static boolean USE_INJECT_HISTORY = true;


    // exponential moving average settings ( should be falling within (0,1] )
    public static double[] EMA_ALPHA = {90.0, 280.0, 370.0, 390.0};

    public static double EMA_UP_PROFIT_THRESHOLD = 0.006;

    public static double EMA_UP_PROFIT_LIMIT = 0.7;

    public static double EMA_UP_LOSS_LIMIT = 0.003;

    public static double EMA_DOWN_PROFIT_THRESHOLD = 0.015;

    public static double EMA_DOWN_PROFIT_LIMIT = 0.6;

    public static double EMA_DOWN_LOSS_LIMIT = 0.0075;


    //TODO: DONT TOUCH ANYTHING BELOW HERE!!!

    // mouse control settings
    public static final int OPERATION_SPEED_MULTIPLIER = 200;   // unit is millisecond

    public static final int FOLLOW_RETRY_INTERVAL = 10000;

    public static final int MAX_LENGTH = Integer.MAX_VALUE;

    public static final int MAX_DECISION_QUEUE_SIZE = 1;

    // strategy priority
    public static final int DEFAULT_PRIORITY = 0;

    public static final int DEFAULT_FORCE_TRIGGER_PRIORITY = 0;

    public static final int BLOCK_LOW_PRIORITY_STRATEGY = 10000;

    public static final int DEFAULT_BASIC_EMA_PRIORITY = 100;

    // history update
    public static final int HISTORY_UPDATE_INTERVAL = 1800 * 1000; // millisecond

    // Time pattern
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String STOCK_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd,HH:mm:ss";

    public static final String OTHER_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd HHmmss";

    // network retry
    public static final int DOWNLOAD_RETRY_COUNT = 10;

    public static final int DOWNLOAD_RETRY_TIME = 3000;
}
