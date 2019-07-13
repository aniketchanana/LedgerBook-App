package com.aniket.myledgerbook.models;

public class records {
    String money,id;

    public records(String money, String id) {
        this.money = money;
        this.id = id;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public records(){

    }

    public String toString(){
        return getId()+" "+getMoney() + "\n";
    }

}
