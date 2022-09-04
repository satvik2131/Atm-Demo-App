package com.work.atm.repository;

import com.work.atm.model.Payment;

import java.util.List;

public interface PaymentCallBack {
    void onPaymentCallback(List<Payment> paymentList);
}
