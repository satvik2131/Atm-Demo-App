package com.work.atm.User;

public class FixedDeposit {
    public Long Amt;
    public String uid,Duration,status,email;

    public FixedDeposit(String uid ,Long Amt, String Duration , String status,String email){
        this.uid = uid;
        this.Amt = Amt;
        this.Duration = Duration;
        this.status = status;
        this.email = email;
    }
}
