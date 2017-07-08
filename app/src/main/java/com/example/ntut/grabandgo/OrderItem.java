package com.example.ntut.grabandgo;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private int serial_no;
    private int ord_id;
    private int prod_id;
    private String item_name;
    private int item_price;
    private int item_amount;
    private String item_note;
    private String m_usename;

    public OrderItem() {
    }

    public OrderItem(int prod_id, String item_name, int item_price,
                     int item_amount, String item_note) {
        this.prod_id = prod_id;
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_amount = item_amount;
        this.item_note = item_note;
    }

    public OrderItem(int prod_id, String item_name, int item_price, int item_amount, String item_note, String m_usename) {
        this.prod_id = prod_id;
        this.item_name = item_name;
        this.item_price = item_price;
        this.item_amount = item_amount;
        this.item_note = item_note;
        this.m_usename = m_usename;
    }

    public int getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(int serial_no) {
        this.serial_no = serial_no;
    }

    public int getOrd_id() {
        return ord_id;
    }

    public void setOrd_id(int ord_id) {
        this.ord_id = ord_id;
    }

    public int getProd_id() {
        return prod_id;
    }

    public void setProd_id(int prod_id) {
        this.prod_id = prod_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public int getItem_price() {
        return item_price;
    }

    public void setItem_price(int item_price) {
        this.item_price = item_price;
    }

    public int getItem_amount() {
        return item_amount;
    }

    public void setItem_amount(int item_amount) {
        this.item_amount = item_amount;
    }

    public String getItem_note() {
        return item_note;
    }

    public void setItem_note(String item_note) {
        this.item_note = item_note;
    }

    public String getM_usename() {
        return m_usename;
    }

    public void setM_usename(String m_usename) {
        this.m_usename = m_usename;
    }


    @Override
    public String toString() {
        return "OrderItem{" +
                "serial_no=" + serial_no +
                ", ord_id=" + ord_id +
                ", prod_id=" + prod_id +
                ", item_name='" + item_name + '\'' +
                ", item_price=" + item_price +
                ", item_amount=" + item_amount +
                ", item_note='" + item_note + '\'' +
                ", m_usename='" + m_usename + '\'' +
                '}';
    }
}
