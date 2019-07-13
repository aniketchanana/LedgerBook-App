package com.aniket.myledgerbook.models;

public class Customer {
    public String id;
    public String name;
    public String phone;
    public int pending;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer(){

    }
    public void updateincashPending(int money){
        this.pending = this.pending - money;
    }
    public void updateoutcashPending(int money){
        this.pending = this.pending + money;
    }
    public Customer(String name, String phone, int pending,String id) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.pending = pending;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPending() {
        return pending;
    }

    public void setPending(int pending) {
        this.pending = pending;
    }
}
