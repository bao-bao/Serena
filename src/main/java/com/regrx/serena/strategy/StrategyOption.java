package com.regrx.serena.strategy;

public class StrategyOption {
    public static final int NoOption = 100;
    public static final int DefaultNST = 101;
    public static final int DefaultLongOnly = 111;
    public static final int DefaultShortOnly = 112;
    public static final int DefaultLongCover = 121;
    public static final int DefaultShortCover = 122;

    public static final int BollingerLongByDefault = 211;
    public static final int BollingerLongByTail = 212;
    public static final int BollingerShortByDefault = 213;
    public static final int BollingerShortByTail = 214;
    public static final int BollingerLongCoverByFallback = 221;
    public static final int BollingerLongCoverByLose = 222;
    public static final int BollingerShortCoverByFallback = 223;
    public static final int BollingerShortCoverByLose = 224;

}
