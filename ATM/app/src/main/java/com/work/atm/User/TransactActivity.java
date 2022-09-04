package com.work.atm.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.work.atm.R;
import com.work.atm.model.ATMMachine;
import com.work.atm.model.Payment;
import com.work.atm.model.User;
import com.work.atm.repository.ATMMachineCallBack;
import com.work.atm.repository.ATMMachineRepository;
import com.work.atm.repository.PaymentRepository;
import com.work.atm.repository.UserCallBack;
import com.work.atm.repository.UserRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class TransactActivity extends AppCompatActivity implements UserCallBack, ATMMachineCallBack {

    private User loggedInUser;
    private List<ATMMachine> atmMachineList;
    public boolean isDeposit = true;

    //Biometric Authentication
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transact);

        UserRepository.getInstance().fetchLoggedInUser(this);

    }



    @Override
    public void onLoggedInUserCallback(User user) {
        if(user == null) {
            //Toast
            return;
        }

        loggedInUser = user;


//        LinearLayout homeLayout = findViewById(R.id.homeLayouttr);
//        Common.displayHome(loggedInUser, this, homeLayout);

        ATMMachineRepository.getInstance().read( this);
    }

    @Override
    public void onAllUserCallback(List<User> userList) {

    }

    @Override
    public void onAllATMMachineCallback(List<ATMMachine> atmMachineList) {

        this.atmMachineList = atmMachineList;

        Spinner atmSpinner = findViewById(R.id.atmMachineTrList);
        ArrayAdapter<ATMMachine> atmAdapter = new ArrayAdapter<ATMMachine>(this, android.R.layout.simple_spinner_item,
                atmMachineList.toArray(new ATMMachine[1])){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView label = (TextView) super.getView(position, convertView, parent);
                label.setText(atmMachineList.get(position).getAtmName());
                return label;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                label.setText(atmMachineList.get(position).getAtmName());
                return label;
            }
        };
        atmAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        atmSpinner.setAdapter(atmAdapter);


        setup();
    }

    @Override
    public void onSingleAtmMachineCallback(ATMMachine atmMachine) {

    }

    public void setup() {

        Bundle bundle = getIntent().getExtras();
        isDeposit = bundle.getBoolean("isDeposit");


        Spinner atmSpinner = findViewById(R.id.atmMachineTrList);
        atmSpinner.setVisibility(isDeposit ? View.INVISIBLE : View.VISIBLE);



        Button transactButton = findViewById(R.id.transactButton);
        transactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transactAmount();
            }
        });

        Button cancelTransButton = findViewById(R.id.cancelTransButton);
        cancelTransButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactActivity.this, UserDashBoardActivity.class);
                startActivity(intent);
            }
        });

        TextView transactTitleTv = findViewById(R.id.transactTitle);
        transactTitleTv.setText(isDeposit ? "Deposit money" : "Withdraw money");

        TextView amountLbl = findViewById(R.id.amountLbl);
        amountLbl.setText(isDeposit ? "Deposit amount" : "Withdraw amount");

        updateUserBalanceView();

        transactButton.setText(isDeposit ? "Deposit" : "Withdraw");
    }

    public void transactAmount() {
//        setFingerPrintAuthentication();

        EditText amountET = findViewById(R.id.amount);
        Long amount = Long.parseLong(amountET.getText().toString());
        if(amount <= 0) {
            Toast.makeText(getApplicationContext(), "Please give positive amount", Toast.LENGTH_LONG).show();
            return;
        }

        if(isDeposit) {
            loggedInUser.setBankBalance(loggedInUser.getBankBalance() + amount);
            UserRepository.getInstance().update(loggedInUser);

            Toast.makeText(getApplicationContext(), "Successfully deposited " + amount + " into the account", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,TransactActivity.class));
        } else {
            Spinner atmSpinner = findViewById(R.id.atmMachineTrList);
            ATMMachine atmMachine = (ATMMachine) atmSpinner.getSelectedItem();
            if(atmMachine == null) {
                Toast.makeText(getApplicationContext(), "Please choose an ATM", Toast.LENGTH_LONG).show();
                return;
            }

            if(atmMachine.getBalance() < amount) {
                Toast.makeText(getApplicationContext(), "ATM out of money", Toast.LENGTH_LONG).show();
                return;
            }

            if(loggedInUser.getBankBalance() < amount) {
                Toast.makeText(getApplicationContext(), "Your balance is low", Toast.LENGTH_LONG).show();
                return;
            }

            loggedInUser.setBankBalance(loggedInUser.getBankBalance() - amount);
            UserRepository.getInstance().update(loggedInUser);

            atmMachine.setBalance(atmMachine.getBalance() - amount);
            ATMMachineRepository.getInstance().update(atmMachine);

            Toast.makeText(getApplicationContext(), "Successfully withdrawn " + amount + " from the account", Toast.LENGTH_LONG).show();
        }

        Payment payment = new Payment(loggedInUser.getUserId(), isDeposit,
                System.currentTimeMillis()/1000,
//                Long.parseLong(ServerValue.TIMESTAMP.get("timestamp")),
                amount);

        PaymentRepository.getInstance().create(payment);

        updateUserBalanceView();

        Intent intent = new Intent(TransactActivity.this, UserDashBoardActivity.class);
        startActivity(intent);
    }



    //Fingerprint Auth popup and text
    private void setFingerPrintAuthentication(){
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(TransactActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                finish();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Put your fingerprint to start")
                .setSubtitle("Here")
                .setNegativeButtonText("Fingerprint")
                .build();

        biometricPrompt.authenticate(promptInfo);

    }



    private void updateUserBalanceView() {
        TextView accBalance = findViewById(R.id.accBalance);
        accBalance.setText("" + loggedInUser.getBankBalance());
        System.out.println(accBalance.getText());
    }

    public void goToHome(View view) {
        startActivity(new Intent(this,UserDashBoardActivity.class));
    }
}