package com.work.atm.model;

public class Payment {
    private String paymentId;
    private Boolean isDeposit;
    private Long paymentDate;
    private Long amount;
    private String userId;

    public Payment() {

    }

    public Payment(String userId, Boolean isDeposit, Long paymentDate, Long amount) {
        this.userId = userId;
        this.isDeposit = isDeposit;
        this.paymentDate = paymentDate;
        this.amount = amount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }


    public Long getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Long paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getDeposit() {
        return isDeposit;
    }

    public void setDeposit(Boolean deposit) {
        isDeposit = deposit;
    }
}
