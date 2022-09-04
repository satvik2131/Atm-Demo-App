package com.work.atm.repository;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.work.atm.model.Payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PaymentRepository {

    public final static String TABLE_NAME = "Payment";

    private static PaymentRepository paymentRepository = null;


    public static PaymentRepository getInstance() {

        if(paymentRepository==null)
            paymentRepository = new PaymentRepository();

        return paymentRepository;

    }

    public Payment create (Payment payment) {

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(TABLE_NAME).push().setValue(payment);
        return payment;
    }


    /**
     * retrieve all payment
     * @param paymentCallBack
     */
    public void read(PaymentCallBack paymentCallBack) {

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME);

        Query query = ref2;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Payment> paymentList = new ArrayList<>();
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Payment payment = dsp.getValue(Payment.class);
                    payment.setPaymentId(dsp.getKey());
                    paymentList.add(payment);
                }
                Collections.sort(paymentList, new Comparator<Payment>() {
                    @Override
                    public int compare(Payment o1, Payment o2) {
                        return o2.getPaymentDate().compareTo(o1.getPaymentDate());
                    }
                });

                paymentCallBack.onPaymentCallback(paymentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * retrieve payments of the given user
     * @param paymentCallBack
     */
    public void findByUser(String userId, PaymentCallBack paymentCallBack) {

        if(userId == null)
            paymentCallBack.onPaymentCallback(null);

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME);

        Query query = ref2;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Payment> paymentList = new ArrayList<>();
                // Result will be holded Here
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Payment payment = dsp.getValue(Payment.class);
                    payment.setPaymentId(dsp.getKey());

                    if(userId.equals(payment.getUserId()))
                        paymentList.add(payment);
                }
                Collections.sort(paymentList, new Comparator<Payment>() {
                    @Override
                    public int compare(Payment o1, Payment o2) {
                        return o2.getPaymentDate().compareTo(o1.getPaymentDate());
                    }
                });

                paymentCallBack.onPaymentCallback(paymentList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void update(Payment payment) {

        DatabaseReference mDatabase;
        if(payment == null || payment.getPaymentId() == null)
            return;

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(TABLE_NAME).child(payment.getPaymentId()).setValue(payment);

    }


    public void delete(Payment payment) {

        if (payment == null || payment.getPaymentId() == null)
            return;

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref2;
        ref2 = mDatabase.child(TABLE_NAME).child(payment.getPaymentId());
        ref2.removeValue();
    }
}
