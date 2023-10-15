package com.regrx.serena.common;

import com.regrx.serena.common.constant.MAEnum;

public class Setting {
    public static final boolean TEST_LABEL = true;

    public static boolean USE_INJECT_HISTORY = true;

    public static final boolean TREND_FOLLOW_MA = false;

    public static final boolean MA_PRIMARY = true;

    public static final int FILL_GAP_BY_MA = 20;

    // opening window
    public static final int MORNING_START_HOUR = 9;
    public static final int MORNING_START_MINUTE = 29;
    public static final int MORNING_CLOSE_HOUR = 11;
    public static final int MORNING_CLOSE_MINUTE = 30;

    public static final int AFTERNOON_START_HOUR = 13;
    public static final int AFTERNOON_START_MINUTE = 0;
    public static final int AFTERNOON_CLOSE_HOUR = 15;
    public static final int AFTERNOON_CLOSE_MINUTE = 0;

    public static final int SHUTDOWN_HOUR = 15;
    public static final int SHUTDOWN_MINUTE = 10;

    // repeat trade action
    public static final int TRADE_OPERATE_REPEAT = 3;

    // trade action click position
    public static final int SELECT_POSITION_X = 264;
    public static final int SELECT_POSITION_Y = 846;

    public static final int PUT_POSITION_X = 260;
    public static final int PUT_POSITION_Y = 900;

    public static final int SHORT_POSITION_X = 370;
    public static final int SHORT_POSITION_Y = 900;

    public static final int EMPTY_POSITION_X = 480;
    public static final int EMPTY_POSITION_Y = 900;

    public static final int FOLLOW_SELECT_POSITION_X = 610;
    public static final int FOLLOW_SELECT_POSITION_Y = 1001;

    public static final int FOLLOW_CLICK_POSITION_X = 654;
    public static final int FOLLOW_CLICK_POSITION_Y = 1052;

    public static final int CLOSE_WARNING_POSITION_X = 1040;
    public static final int CLOSE_WARNING_POSITION_Y = 598;

    public static final int CONFIRM_POSITION_X = 960;
    public static final int CONFIRM_POSITION_Y = 600;

    //TODO: add final HERE

    public static double LOSS_LIMIT_THRESHOLD = 23;

    public static double PROFIT_LIMIT_THRESHOLD = 15;

    public static double RESTORE_THRESHOLD = 19;

    public static double TRADE_THRESHOLD = 0;

    public static double FILL_GAP_THRESHOLD = 10;

    public static int MA_MAX_AGGREGATE = 100;

    // exponential moving average settings ( should be falling within (0,1] )
    public static double[] EMA_ALPHA = {90.0, 280.0, 370.0, 390.0};

    public static double EMA_UP_PROFIT_THRESHOLD = 0.006;

    public static double EMA_UP_PROFIT_LIMIT = 0.7;

    public static double EMA_UP_LOSS_LIMIT = 0.003;

    public static double EMA_DOWN_PROFIT_THRESHOLD = 0.015;

    public static double EMA_DOWN_PROFIT_LIMIT = 0.6;

    public static double EMA_DOWN_LOSS_LIMIT = 0.0075;

    // bollinger settings
    public static MAEnum BOLLINGER_MA_BASE = MAEnum.MA30;

    public static int BOLLINGER_AGGREGATE_COUNT = 30;

    public static double BOLLINGER_DEVIATION_MULTIPLIER = 1.3;

    public static double BOLLINGER_B_PRICE_RATIO = 0.5;

    public static double BOLLINGER_S_PRICE_RATIO = 0.5;

    public static double BOLLINGER_B_PRICE_REFERENCE = 5;

    public static double BOLLINGER_S_PRICE_REFERENCE = 5;

    public static double BOLLINGER_B_PROFIT_TREAT = 15;

    public static double BOLLINGER_S_PROFIT_TREAT = 15;

    public static double BOLLINGER_B_FALLBACK = 10;

    public static double BOLLINGER_S_FALLBACK = 10;

    public static double BOLLINGER_B_LOSE_LIMIT = 10;

    public static double BOLLINGER_S_LOSE_LIMIT = 10;

    // donchian golden settings
    public static int DONCHIAN_GOLDEN_AGGREGATE_COUNT = 40;

    public static double DONCHIAN_GOLDEN_PUT_THRESHOLD = 0.191;

    public static double DONCHIAN_GOLDEN_PUT_EMPTY_THRESHOLD = 0.809;

    public static double DONCHIAN_GOLDEN_SHORT_THRESHOLD = 0.809;

    public static double DONCHIAN_GOLDEN_SHORT_EMPTY_THRESHOLD = 0.191;

    public static int DONCHIAN_GOLDEN_CONTINOUS_LOSS_LIMIT = 3;

    public static double DONCHIAN_GOLDEN_LOSS_THRESHOLD = 12;

    //TODO: DONT TOUCH ANYTHING BELOW HERE!!!

    // mouse control settings
    public static final int OPERATION_SPEED_MULTIPLIER = 200;   // unit is millisecond

    public static final int OPERATION_RETRY_INTERVAL = 1000;   // unit is millisecond

    public static final int MOUSE_CLICK_PRESS_TIME = 75; // release when pressed 75ms

    public static int FOLLOW_TIME = 1;

    public static final int FOLLOW_RETRY_INTERVAL = 5000;

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

    // network retry
    public static final int DOWNLOAD_RETRY_COUNT = 10;

    public static final int DOWNLOAD_RETRY_TIME = 3000;
}
