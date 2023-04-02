package com.regrx.serena.common;

public class Setting {
    public static final boolean TEST_LABEL = true;

    public static boolean USE_INJECT_HISTORY = true;

    public static final boolean TREND_FOLLOW_MA = false;

    public static final boolean MA_PRIMARY = true;

    public static final int FILL_GAP_BY_MA = 20;

    //TODO: add final HERE
    public static double LOSS_LIMIT_THRESHOLD = 23;

    public static double PROFIT_LIMIT_THRESHOLD = 15;

    public static double RESTORE_THRESHOLD = 19;

    public static double TRADE_THRESHOLD = 0;

    public static double FILL_GAP_THRESHOLD = 10;

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
    public static final int OPERATION_SPEED_MULTIPLIER = 500;   // unit is millisecond

    public static final int FOLLOW_RETRY_INTERVAL = 10000;

    public static final int MAX_LENGTH = Integer.MAX_VALUE;

    public static final int MAX_DECISION_QUEUE_SIZE = 1;

    // strategy priority
    public static final int BLOCK_LOW_PRIORITY_STRATEGY = 10000;

    public static final int DEFAULT_PRIORITY = 0;

    public static final int DEFAULT_FORCE_TRIGGER_PRIORITY = 0;

    public static final int HIGH_LOSS_LIMIT_PRIORITY = 200;

    public static final int DEFAULT_LOSS_LIMIT_PRIORITY = Integer.MAX_VALUE;

    public static final int HIGH_PROFIT_LIMIT_PRIORITY = 200;

    public static final int DEFAULT_PROFIT_LIMIT_PRIORITY = Integer.MAX_VALUE;

    public static final int DEFAULT_MA_520_PRIORITY = 100;

    public static final int DEFAULT_FILL_GAP_PRIORITY = 500;

    public static final int DEFAULT_MA_240_PRIORITY = 100;

    public static final int DEFAULT_MA_240_520_PRIORITY = 200;

    public static final int DEFAULT_BASIC_EMA_PRIORITY = 100;

    // only one per day
    public static final int PUT_BUYING_LIMIT = 1;

    public static final int SHORT_SELLING_LIMIT = 1;

    // history update
    public static final int HISTORY_UPDATE_INTERVAL = 1800 * 1000; // millisecond

    // Time pattern
    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String STOCK_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd,HH:mm:ss";

    public static final String OTHER_FUTURE_PRICE_DATA_TIME_PATTERN = "yyyy-MM-dd HHmmss";
}
