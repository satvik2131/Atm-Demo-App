package com.work.atm.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.work.atm.R;

public class ListDeposit extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_fd);

        FixedDepositFirebase fixedDepositFirebase = new FixedDepositFirebase(this);

        TableLayout tableLayout = findViewById(R.id.list_fixed_deposit);
        fixedDepositFirebase.listFixedDeposit(tableLayout);
    }

    public void goToHome(View view) {
        startActivity(new Intent(this,UserDashBoardActivity.class));
    }
}
