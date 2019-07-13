package com.aniket.myledgerbook.models;

public class User {
    public String name,email,phone,accountName,imageurl;

    public User(String name, String email, String phone, String accountName,String imageurl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.accountName = accountName;
        this.imageurl = imageurl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getPhone() {
        return phone;
    }

    public String getAccountName() {
        return accountName;
    }

    public User(){

    }
}
