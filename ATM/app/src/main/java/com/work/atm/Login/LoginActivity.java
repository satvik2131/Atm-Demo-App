package com.work.atm.Login;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.work.atm.Admin.AdminDashBoardActivity;
import com.work.atm.Admin.AdminLogin;
import com.work.atm.MainActivity;
import com.work.atm.R;
import com.work.atm.User.UserDashBoardActivity;
import com.work.atm.model.User;
import com.work.atm.repository.UserCallBack;
import com.work.atm.repository.UserRepository;

import java.util.List;

public class LoginActivity extends AppCompatActivity  {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference();



        if (auth.getCurrentUser() != null ) {
            String uid = auth.getCurrentUser().getUid();
            boolean val = ref.child("user").child(uid).child("admin").equals(true);

            if(val==true){
                startActivity(new Intent(this,UserDashBoardActivity.class));
            }

        }



        // set the view now
        setContentView(R.layout.activity_login);

        initialiseUserDropdown();



        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
//        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    String UID = task.getResult().getUser().getUid();
                                    ref.child("user").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.child("admin").getValue(Boolean.class)==false){
                                                Toast.makeText(LoginActivity.this,"Your application is under observation , you can try to login after sometime",Toast.LENGTH_LONG).show();
                                            }else{
                                                finish();
                                                startActivity(new Intent(LoginActivity.this,UserDashBoardActivity.class));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                            }
                        });
            }
        });
    }


    private void initialiseUserDropdown() {

//        UserRepository.getInstance().read(this);

    }



//    @Override
//    public void onLoggedInUserCallback(User user) {
//        if(user == null) {
////            Toast.makeText(LoginActivity.this, "Failed to get user detail", Toast.LENGTH_LONG).show();
////            finish();
////            return;
//            auth = FirebaseAuth.getInstance();
//            FirebaseUser fbUser = auth.getCurrentUser();
//            user = new User(fbUser.getUid(), fbUser.getEmail(), fbUser.getDisplayName(), "", 0L, UserRepository.getInstance().findIfAdmin(fbUser.getEmail()));
//            UserRepository.getInstance().create(user);
//
//        }
//
//        Intent intent = new Intent(LoginActivity.this,
//                UserRepository.getInstance().isAdmin(user) ?
//                        AdminDashBoardActivity.class : UserDashBoardActivity.class);
//        startActivity(intent);
//    }

//    @Override
//    public void onAllUserCallback(List<User> userList) {
//
//        String array[] = new String[userList.size() + 1];
//
//        array[0] = (".......");
//        int i = 1;
//        for(User user : userList) {
//
//            System.out.println("Display = "+ user.getEmail());
//            array[i++] = user.getEmail();
//
//        }
//
//
//
//
//    }

    public void goToAdmin(View view) {
        startActivity(new Intent(this, AdminLogin.class));
    }
}
