package com.regrx.trade;

import com.regrx.trade.constant.Constant;
import com.regrx.trade.data.DataTrack;

public class Application {
    public static void main(String[] args) {
        DataTrack dataTrack = new DataTrack(Constant.MIN_1);
        dataTrack.track("https://hq.sinajs.cn/list=nf_IF2107");
    }
}
