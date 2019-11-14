package com.danielogbuti.androideatitserver.model;

import java.util.List;

public class Request {

    private String Phone;
    private String Name;
    private String Address;
    private String Total;
    private List<Order> Foods;
    private String status;



    public Request() {
    }

    public Request(String phone, String name, String address, String total, List<Order> foods) {
        Phone = phone;
        Name = name;
        Address = address;
        Total = total;
        Foods = foods;
        this.status = "0";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public List<Order> getFoods() {
        return Foods;
    }

    public void setFoods(List<Order> foods) {
        Foods = foods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
