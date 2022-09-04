package com.work.atm.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.work.atm.R;

public class AdminLogin extends AppCompatActivity {

    EditText admin_username;
    EditText admin_password;
    DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login);

        ref = FirebaseDatabase.getInstance().getReference();

        admin_username = findViewById(R.id.admin_username);
        admin_password = findViewById(R.id.admin_password);

    }

    public void gotToAdminHome(View view) {
        String username = admin_username.getText().toString().trim();
        String password = admin_password.getText().toString().trim();


        ref.child("admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String unm = (snapshot
                                .child("username")
                                .getValue(String.class));
                        String pwd = (snapshot
                                .child("password")
                                .getValue(String.class));


                        if(username.compareTo(unm)==0 && password.compareTo(pwd)==0){
                            startActivity(new Intent(AdminLogin.this,AdminDashBoardActivity.class));
                        }else{
                            finish();
                            return;
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}
