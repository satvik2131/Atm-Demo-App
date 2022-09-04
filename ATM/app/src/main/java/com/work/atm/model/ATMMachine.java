package com.work.atm.model;

public class ATMMachine {
    private String atmId;
    private String atmName;
    private Long balance;

    public ATMMachine() {

    }

    public ATMMachine(String atmName, Long balance) {
        this.atmName = atmName;
        this.balance = balance;
    }

    public String getAtmId() {
        return atmId;
    }

    public void setAtmId(String atmId) {
        this.atmId = atmId;
    }

    public String getAtmName() {
        return atmName;
    }

    public void setAtmName(String atmName) {
        this.atmName = atmName;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }
}
