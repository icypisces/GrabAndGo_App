package com.example.ntut.grabandgo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable{
    private int ord_id;
    private String m_username;
    private String m_pickupname;
    private Timestamp ord_time;
    private Timestamp ord_pickuptime;
    private int rest_id;
    private int ord_totalPrice;
    private String ord_status;
    private String rest_name;
    private List<OrderItem> items = new ArrayList<OrderItem>();
    private String ord_tel;
    private String ord_email;
    private int ord_evalued;

    public Order() {

    }

    public Order(int ord_id, String m_username, String m_pickupname, Timestamp ord_time, Timestamp ord_pickuptime, int rest_id, int ord_totalPrice, String ord_status, String rest_name, List<OrderItem> items, String ord_tel, String ord_email, int ord_evalued) {
        this.ord_id = ord_id;
        this.m_username = m_username;
        this.m_pickupname = m_pickupname;
        this.ord_time = ord_time;
        this.ord_pickuptime = ord_pickuptime;
        this.rest_id = rest_id;
        this.ord_totalPrice = ord_totalPrice;
        this.ord_status = ord_status;
        this.rest_name = rest_name;
        this.items = items;
        this.ord_tel = ord_tel;
        this.ord_email = ord_email;
        this.ord_evalued = ord_evalued;
    }

    public Order(int ord_id, String m_username, Timestamp ord_pickuptime, int ord_totalPrice, String ord_status, String ord_tel) {
        this.ord_id = ord_id;
        this.m_username = m_username;
        this.ord_pickuptime = ord_pickuptime;
        this.ord_totalPrice = ord_totalPrice;
        this.ord_status = ord_status;
        this.ord_tel = ord_tel;
    }

    public int getOrd_id() {
        return ord_id;
    }

    public void setOrd_id(int ord_id) {
        this.ord_id = ord_id;
    }

    public String getM_username() {
        return m_username;
    }

    public void setM_username(String m_username) {
        this.m_username = m_username;
    }

    public String getM_pickupname() {
        return m_pickupname;
    }

    public void setM_pickupname(String m_pickupname) {
        this.m_pickupname = m_pickupname;
    }

    public Timestamp getOrd_time() {
        return ord_time;
    }

    public void setOrd_time(Timestamp ord_time) {
        this.ord_time = ord_time;
    }

    public Timestamp getOrd_pickuptime() {
        return ord_pickuptime;
    }

    public void setOrd_pickuptime(Timestamp ord_pickuptime) {
        this.ord_pickuptime = ord_pickuptime;
    }

    public int getRest_id() {
        return rest_id;
    }

    public void setRest_id(int rest_id) {
        this.rest_id = rest_id;
    }

    public int getOrd_totalPrice() {
        return ord_totalPrice;
    }

    public void setOrd_totalPrice(int ord_totalPrice) {
        this.ord_totalPrice = ord_totalPrice;
    }

    public String getOrd_status() {
        return ord_status;
    }

    public void setOrd_status(String ord_status) {
        this.ord_status = ord_status;
    }

    public String getRest_name() {
        return rest_name;
    }

    public void setRest_name(String rest_name) {
        this.rest_name = rest_name;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getOrd_tel() {
        return ord_tel;
    }

    public void setOrd_tel(String ord_tel) {
        this.ord_tel = ord_tel;
    }

    public String getOrd_email() {
        return ord_email;
    }

    public void setOrd_email(String ord_email) {
        this.ord_email = ord_email;
    }

    public int getOrd_evalued() {
        return ord_evalued;
    }

    public void setOrd_evalued(int ord_evalued) {
        this.ord_evalued = ord_evalued;
    }

    @Override
    public String toString() {
        return "Order{" +
                "ord_id=" + ord_id +
                ", m_username='" + m_username + '\'' +
                ", m_pickupname='" + m_pickupname + '\'' +
                ", ord_time=" + ord_time +
                ", ord_pickuptime=" + ord_pickuptime +
                ", rest_id=" + rest_id +
                ", ord_totalPrice=" + ord_totalPrice +
                ", ord_status='" + ord_status + '\'' +
                ", rest_name='" + rest_name + '\'' +
                ", items=" + items +
                ", ord_tel='" + ord_tel + '\'' +
                ", ord_email='" + ord_email + '\'' +
                ", ord_evalued=" + ord_evalued +
                '}';
    }

}
