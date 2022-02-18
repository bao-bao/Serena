package com.regrx.trade.constant;

public class Constant {

    public static final int START_FAST_TRADE = 0;       // MUST BE EVEN

    public static final double TRADE_THRESHOLD = 0.5;

    //TODO: add final HERE
    public static int PROFIT_LIMIT_THRESHOLD = 13;

    public static int LOSS_LIMIT_THRESHOLD = 12;

    public static int RESTORE_THRESHOLD = 10;

    public static final int FOLLOW_TIME = 2;

    public static final int TEST_LABEL = 1;

    //TODO: DONT TOUCH ANYTHING BELOW HERE!!!
    public static final int MAX_LENGTH = 1000;

    // Time interval section
    public static final int MIN_1 = 1;

    public static final int MIN_3 = 3;

    public static final int MIN_5 = 5;

    public static final int MIN_15 = 15;

    public static final int MIN_30 = 30;

    public static final int MIN_60 = 60;

    // MA section
    public static final int MA5 = 0;

    public static final int MA10 = 1;

    public static final int MA20 = 2;

    public static final int MA30 = 3;

    public static final int MA60 = 4;

    public static final int MA90 = 5;

    public static final int MA120 = 6;

    public static final int MA250 = 7;

    // Status section
    public static final int SHORT_SELLING = 100;

    public static final int PUT_BUYING = 200;

    public static final int BOTH = 300;

    public static final int EMPTY = 0;

    // Breed section
    public static final int STOCK = 0;

    public static final int FUTURE_NO_NIGHT = 1;

    public static final int FUTURE_NIGHT_2300 = 2;

    public static final int FUTURE_NIGHT_0100 = 3;

    public static final int FUTURE_NIGHT_0230 = 4;

    // Error code section
    public static final int DOWNLOAD_ERROR_CODE = 201;

    public static final int PARSE_ERROR_CODE = 202;
}
