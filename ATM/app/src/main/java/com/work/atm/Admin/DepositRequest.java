package com.work.atm.Admin;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.work.atm.R;

public class DepositRequest extends AppCompatActivity {
    TableLayout tableLayout;
    DatabaseReference ref;
    private Context currContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_deposit_request);
        currContext = this;
        ref = FirebaseDatabase.getInstance().getReference();
        tableLayout = findViewById(R.id.admin_regiter_list);


        ref.child("fixed_deposit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()  ){

                    //Getting Values
                    String uid = snap.child("uid").getValue(String.class);
                    String duration = snap.child("Duration").getValue(String.class);
                    Long amount = snap.child("Amt").getValue(Long.class);
                    String status = snap.child("status").getValue(String.class);
                    String email = snap.child("email").getValue(String.class);

                    //Setting Values
                    TableRow row = new TableRow(DepositRequest.this);
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
                    tvDuration.setPadding(5,5,5,5);

                    //For Amount
                    TextView tvAmount = new TextView(currContext);
                    tvAmount.setText(String.valueOf(amount));
                    tvAmount.setWidth(100);
                    tvAmount.setGravity(Gravity.CENTER);
                    tvAmount.setLayoutParams(tlp);
                    tvAmount.setPadding(5,5,5,5);


                    //For Status
                    TextView tvStatus = new TextView(currContext);
                    tvStatus.setText(status);
                    tvStatus.setWidth(100);
                    tvStatus.setGravity(Gravity.CENTER);
                    tvStatus.setLayoutParams(tlp);
                    tvStatus.setPadding(5,10,5,10);

                    //For email
                    TextView emailView = new TextView(currContext);
                    tvStatus.setText(email);
                    tvStatus.setWidth(100);
                    tvStatus.setGravity(Gravity.CENTER);
                    tvStatus.setLayoutParams(tlp);
                    tvStatus.setPadding(5,10,5,10);


                    Button approveOrDisapprove = new Button(currContext);

                    if (status.compareTo("Pending")==0) {
                        approveOrDisapprove.setText("Approve");
                        approveOrDisapprove.setBackgroundColor(Color.parseColor("#2eea2e"));
                    } else {
                        approveOrDisapprove.setText("Disapprove");
                        approveOrDisapprove.setBackgroundColor(Color.parseColor("#ea2e2e"));

                    }
                    approveOrDisapprove.setGravity(Gravity.CENTER);
                    approveOrDisapprove.setLayoutParams(tlp);
                    approveOrDisapprove.setPadding(5,10,5,10);
                    approveOrDisapprove.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Button btn = (Button)v;
                            CharSequence btnText = btn.getText();
                            String result;

                            if(btnText == "Approve"){
                                result = "Approved";
                            }else{result = "Disapproved";}

                            //Change status
                            ref.child("fixed_deposit")
                                    .child(uid)
                                    .child("status")
                                    .setValue(result);
                            finish();
                            startActivity(getIntent());
                        }
                    });


                    LinearLayout statusColumn = new LinearLayout(currContext);
                    statusColumn.setOrientation(LinearLayout.VERTICAL);
                    statusColumn.addView(tvStatus);
                    statusColumn.addView(approveOrDisapprove);

                    //Adding Text View in row
                    row.addView(tvDuration);
                    row.addView(tvAmount);
                    row.addView(statusColumn);
                    row.addView(emailView);

                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
