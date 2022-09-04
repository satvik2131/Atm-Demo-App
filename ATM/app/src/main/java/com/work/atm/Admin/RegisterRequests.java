package com.work.atm.Admin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

public class RegisterRequests extends AppCompatActivity {
    TableLayout tableLayout;
    DatabaseReference ref;
    private Context currContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_register_requests);
        currContext = this;
        ref = FirebaseDatabase.getInstance().getReference();
        tableLayout = findViewById(R.id.admin_regiter_list);


        ref.child("user").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()  ){

                    //Getting Values
                    String uid = snap.child("userId").getValue(String.class);
                    String email = snap.child("email").getValue(String.class);
                    Boolean status = snap.child("admin").getValue(Boolean.class);

                    //Setting Values
                    TableRow row = new TableRow(RegisterRequests.this);
                    TableRow.LayoutParams tlp = new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT,
                            1F
                    );
                    row.setLayoutParams(tlp);


                    //For Duration
                    TextView Uid = new TextView(currContext);
                    Uid.setText(uid);
                    Uid.setWidth(100);
                    Uid.setGravity(Gravity.CENTER);
                    Uid.setLayoutParams(tlp);
                    Uid.setPadding(5,5,5,5);
                    //For Amount
                    TextView tvEmail = new TextView(currContext);
                    tvEmail.setText(email);
                    tvEmail.setWidth(100);
                    tvEmail.setGravity(Gravity.CENTER);
                    tvEmail.setLayoutParams(tlp);
                    tvEmail.setPadding(5,5,5,5);


                    //For Status
                    TextView tvStatus = new TextView(currContext);
                    if (status == false) {
                        tvStatus.setText("Pending");
                    } else {
                        tvStatus.setText("Approved");
                    }
                    tvStatus.setWidth(100);
                    tvStatus.setGravity(Gravity.CENTER);
                    tvStatus.setLayoutParams(tlp);
                    tvStatus.setPadding(5,10,5,10);


                    Button approveOrDisapprove = new Button(currContext);
                    if (status == false) {
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
                            Boolean result;

                            if(btnText == "Approve"){
                                result = true;
                            }else{result = false;}

                            //Change status
                            ref.child("user").child(uid).child("admin").setValue(result);
                            finish();
                            startActivity(getIntent());
                        }
                    });


                    LinearLayout statusColumn = new LinearLayout(currContext);
                    statusColumn.setOrientation(LinearLayout.VERTICAL);
                    statusColumn.addView(tvStatus);
                    statusColumn.addView(approveOrDisapprove);

                    //Adding Text View in row
                    row.addView(Uid);
                    row.addView(tvEmail);
                    row.addView(statusColumn);

                    tableLayout.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
