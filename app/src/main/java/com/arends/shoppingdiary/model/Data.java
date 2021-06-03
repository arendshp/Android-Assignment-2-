package com.arends.shoppingdiary.model;

public class Data {

    String where;
    String item;
    String payment_type;
    int amount;
    String date;
    String id;

    public Data() {
    }

    public Data(String where, String item, String payment_type, int amount, String date, String id) {
        this.where = where;
        this.item = item;
        this.payment_type = payment_type;
        this.amount = amount;
        this.date = date;
        this.id = id;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
