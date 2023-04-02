package com.regrx.serena.data;

public class HistoryData {
    private int p;
    private double c;    // closePrice
    private double h;    // highestPrice
    private double l;    // lowestPrice
    private double o;    // openPrice
    private long v;       // volume
    private String d;    // time

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public double getClosePrice() {
        return c;
    }

    public void setClosePrice(double c) {
        this.c = c;
    }

    public double getHighestPrice() {
        return h;
    }

    public void setHighestPrice(double h) {
        this.h = h;
    }

    public double getLowestPrice() {
        return l;
    }

    public void setLowestPrice(double l) {
        this.l = l;
    }

    public double getOpenPrice() {
        return o;
    }

    public void setOpenPrice(double o) {
        this.o = o;
    }

    public long getVolume() {
        return v;
    }

    public void setVolume(long v) {
        this.v = v;
    }

    public String getDate() {
        return d;
    }

    public void setDate(String d) {
        this.d = d;
    }
}
