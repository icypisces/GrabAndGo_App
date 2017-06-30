package com.example.ntut.grabandgo.orders_daily;

import java.sql.Date;

public class Order {
    int OrderId;
    String buyerName;
    String buyerPhon;
    Date OrderTime;
    String RestaurentId;
    int totalAmout;
    String status;

    public Order() {

    }

    public Order(int orderId, String buyerName, String buyerPhon, Date orderTime,
                 String restaurentId, int totalAmout, String status) {
        OrderId = orderId;
        this.buyerName = buyerName;
        this.buyerPhon = buyerPhon;
        OrderTime = orderTime;
        RestaurentId = restaurentId;
        this.totalAmout = totalAmout;
        this.status = status;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerPhon() {
        return buyerPhon;
    }

    public void setBuyerPhon(String buyerPhon) {
        this.buyerPhon = buyerPhon;
    }

    public Date getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(Date orderTime) {
        OrderTime = orderTime;
    }

    public String getRestaurentId() {
        return RestaurentId;
    }

    public void setRestaurentId(String restaurentId) {
        RestaurentId = restaurentId;
    }

    public int getTotalAmout() {
        return totalAmout;
    }

    public void setTotalAmout(int totalAmout) {
        this.totalAmout = totalAmout;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
