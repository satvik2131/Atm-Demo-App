package com.work.atm.User;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


//Responsible for uploading and fetching data from firebase of Fixed Deposit
public class FixedDepositFirebase {

    private DatabaseReference ref;
    private FirebaseAuth auth;
    Context currContext;

    FixedDepositFirebase(Context context) {
        currContext = context;
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();
    }

    
    //Deposit Fixed Deposit
    void submitFdMoney(String amount , String interestAndDuration,String email){
        try{
            long amt = Long.parseLong(amount);
            String uid = auth.getCurrentUser().getUid();

            //Updates Current Balance
            ref.child("user").orderByChild("userId").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snap: snapshot.getChildren()){
                        long updatedBalance = snap.child("bankBalance").getValue(Long.class);
                        updatedBalance -= amt;

                        if(updatedBalance<0){
                            Toast.makeText(currContext, "Balance can't be negative",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        snap.getRef().child("bankBalance").setValue(updatedBalance);
                    }

                    String status = "Pending";
                    FixedDeposit fixedDeposit = new FixedDeposit(uid,amt,interestAndDuration,status,email);

                    ref.child("fixed_deposit").child(uid).setValue(fixedDeposit).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            currContext.startActivity(new Intent(currContext,UserDashBoardActivity.class));
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch(Exception e){

        }

    }

    //Fetches the data from firebase and sets in the fixed deposit table
    void listFixedDeposit(TableLayout tableLayout){
        String uid = auth.getCurrentUser().getUid();

        ref.child("fixed_deposit").orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()){
                    long amnt;
                   String  duration ,status;

                   amnt = snap.child("Amt").getValue(Long.class);
                   duration = snap.child("Duration").getValue(String.class);
                   status = snap.child("status").getValue(String.class);

                    TableRow row = new TableRow(currContext);
                    TableRow.LayoutParams tlp = new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT,
                            1F
                    );
                    row.setLayoutParams(tlp);

                    //For Duration
                    TextView tvDuration = new TextView(currContext);
                    tvDuration.setText(duration);
                    tvDuration.setWidth(100);
                    tvDuration.setGravity(Gravity.CENTER);
                    tvDuration.setLayoutParams(tlp);
                    tvDuration.setPadding(0,5,0,5);
                    //For Amount
                    TextView tvAmount = new TextView(currContext);
                    tvAmount.setText(String.valueOf(amnt));
                    tvAmount.setWidth(100);
                    tvAmount.setGravity(Gravity.CENTER);
                    tvAmount.setLayoutParams(tlp);
                    tvAmount.setPadding(0,5,0,5);


                    //For Status
                    TextView tvStatus = new TextView(currContext);
                    tvStatus.setText(status);
                    tvStatus.setWidth(100);
                    tvStatus.setGravity(Gravity.CENTER);
                    tvStatus.setLayoutParams(tlp);
                    tvStatus.setPadding(0,10,0,10);



                    //Adding Text View in row
                    row.addView(tvDuration);
                    row.addView(tvAmount);
                    row.addView(tvStatus);

                    tableLayout.addView(row);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
