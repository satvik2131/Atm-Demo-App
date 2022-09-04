package com.work.atm.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.work.atm.R;
import com.work.atm.model.Payment;
import com.work.atm.model.User;
import com.work.atm.repository.PaymentCallBack;
import com.work.atm.repository.PaymentRepository;
import com.work.atm.repository.UserCallBack;
import com.work.atm.repository.UserRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity implements UserCallBack, PaymentCallBack {
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        UserRepository.getInstance().fetchLoggedInUser(this);

        FloatingActionButton button = findViewById(R.id.go_to_home);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryActivity.this,UserDashBoardActivity.class));
            }
        });

    }

    @Override
    public void onLoggedInUserCallback(User user) {
        if(user == null) {
            //Toast
            return;
        }

        loggedInUser = user;

//        LinearLayout homeLayoutci = findViewById(R.id.homeLayoutci);
//        Common.displayHome(loggedInUser, this, homeLayoutci);


        PaymentRepository.getInstance().findByUser(loggedInUser.getUserId(), this);
    }

    @Override
    public void onAllUserCallback(List<User> userList) {

    }


    @Override
    public void onPaymentCallback(List<Payment> paymentList) {
        displayPayments(paymentList);
    }

    private void displayPayments(List<Payment> paymentList) {

        TextView avlHistBalance = findViewById(R.id.avlHistBalance);
        avlHistBalance.setText("" + loggedInUser.getBankBalance() + " Rs");

        TableLayout paymentTable = (TableLayout) findViewById(R.id.payments);
        paymentTable.removeAllViewsInLayout();

        TableRow headerRow = new TableRow(this);
        TableRow.LayoutParams lpHead = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        headerRow.setLayoutParams(lpHead);

        String[] headers =
                new String[]{" Date ", "Time"," Deposit ", " Withdraw "};

        for(String header : headers) {
            TextView textView = new TextView(this);
            textView.setText(header);
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            textView.setPadding(20,5,20,5);


            headerRow.addView(textView);
        }
        paymentTable.addView(headerRow);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.US);



        //Inserting Amount and date and time to the table
        for (Payment payment : paymentList) {

            TableRow row = new TableRow(this);
            TableRow.LayoutParams tlp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(tlp);

            //Taking Date and time from db and setting it to the table
            Date date = new Date(payment.getPaymentDate()* 1000);
            String transactionDate = new SimpleDateFormat("MM/dd/yyyy").format(date);
            String transactionTime = date.getHours() + ":" +date.getSeconds();

            //Date TextView
            TextView dateTv = new TextView(this);
            //Time TextView
            TextView timeTv = new TextView(this);

            //Setting space between Date and time
            timeTv.setPadding(20,5,20,5);

            //Set Date
            dateTv.setText(transactionDate);
            //Set Time
            timeTv.setText(transactionTime);

            //Add View to the Row
            //Date
            row.addView(dateTv);

            //Time
            row.addView(timeTv);

            //Empty Text
            TextView emptyTv = new TextView(this);
            emptyTv.setText("");

            if(!payment.getDeposit())
                row.addView(emptyTv);

            //Amount Column
            TextView amountTv = new TextView(this);
            amountTv.setText(payment.getAmount() + " Rs");
            amountTv.setTextSize(20);
            amountTv.setPadding(20,5,20,5);
            row.addView(amountTv);

            if(payment.getDeposit())
                row.addView(emptyTv);


            paymentTable.addView(row);

        }
    }
}