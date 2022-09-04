package com.work.atm.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.work.atm.R;

public class AdminDashBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);
    }

    public void registerRequests(View view) {
        startActivity(new Intent(this,RegisterRequests.class));
    }

    public void depositRequests(View view) {
        startActivity(new Intent(this,DepositRequest.class));
    }

    public void logout(View view) {
        startActivity(new Intent(this,AdminLogin.class));
        finish();
    }
}