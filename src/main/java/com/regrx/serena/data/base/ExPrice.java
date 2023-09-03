package com.regrx.serena.data.base;


public class ExPrice {
    private double price;
    private double highest;
    private double lowest;
    private String time;

    public ExPrice(double price, String time) {
        this.price = price;
        this.time = time;
    }

    public ExPrice(double price) {
        this.price = price;
    }

    public ExPrice() {
        this.price = 0.0;
        this.time = "NULL";
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getHighest() {
        return highest;
    }

    public void setHighest(double highest) {
        this.highest = highest;
    }

    public double getLowest() {
        return lowest;
    }

    public void setLowest(double lowest) {
        this.lowest = lowest;
    }

    public static ExPrice add(ExPrice price1, ExPrice price2) {
        return new ExPrice(price1.getPrice() + price2.getPrice());
    }

    @Override
    public String toString() {
        return "Price: " + String.format("%.2f", price) + "; " +
                "Time: " + time;
    }
}
