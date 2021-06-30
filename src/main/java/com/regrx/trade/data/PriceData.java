package com.regrx.trade.data;


import java.util.Date;

public class PriceData {
    private double price;
    private Date date;

    public PriceData(double price, Date date) {
        this.price = price;
        this.date = date;
    }

    public PriceData() {
        this.price = -1.0;
        this.date = new Date();
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
