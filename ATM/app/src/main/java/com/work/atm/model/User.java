package com.work.atm.model;

import java.util.Map;

public class User {
    private String userId;
    private String email;

    private Long bankBalance;
    private Boolean isAdmin;



    public User(){}

    public User(String userId, String email, Long bankBalance, Boolean admin ){

        this.userId = userId;
        this.email = email;
        this.bankBalance = bankBalance;
        this.isAdmin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Long getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(Long bankBalance) {
        this.bankBalance = bankBalance;
    }

}