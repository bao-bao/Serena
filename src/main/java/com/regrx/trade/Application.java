package com.regrx.trade;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.DataTrack;

public class Application {
    public static void main(String[] args) {
        DataTrack dataTrack = new DataTrack("IF2203", Constant.MIN_5);
        dataTrack.track();
    }
}
