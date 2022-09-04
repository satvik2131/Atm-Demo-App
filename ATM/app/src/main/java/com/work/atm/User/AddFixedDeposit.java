package com.work.atm.User;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.work.atm.R;
import com.work.atm.model.User;

public class AddFixedDeposit extends AppCompatActivity  {
    private User loggedInUser;
    Button fd_duration;
    Button submit_fd;
    CharSequence durationAndInterest;
    private FirebaseAuth auth;
    private DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_fd);

        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();

        fd_duration = findViewById(R.id.fd_duration);
        submit_fd = findViewById(R.id.submit_fixed_deposit);

        //Handles Submission
        submit_fd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fixDepositMoney(v);
            }
        });

        //Handles Menu Listener
        fd_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v, R.menu.fd_duration);
            }
        });
    }


    private void showMenu(View v, int menuRes){
        PopupMenu popup = new PopupMenu(this,v);
        popup.getMenuInflater().inflate(menuRes,popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                durationAndInterest = item.getTitle();
                Toast.makeText(AddFixedDeposit.this,durationAndInterest.toString(),Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        popup.show();
    }


    public void fixDepositMoney(View view) {
        String uid = auth.getUid();

        ref.child("user").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);

                TextView deposit_amount = findViewById(R.id.deposit_amount);
                String amount = deposit_amount.getText().toString();
                FixedDepositFirebase depositFirebase = new FixedDepositFirebase(AddFixedDeposit.this);

                //Adds Money to Fixed Deposit
                depositFirebase.submitFdMoney(amount,durationAndInterest.toString(),email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
