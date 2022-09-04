package com.work.atm.User;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.work.atm.R;

public class ChangePassword extends AppCompatActivity {

    FirebaseUser auth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance().getCurrentUser();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Window window = getWindow();
        window.setLayout(((width/10)*8),((height/10)*5));
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.popup));
    }


    public void setNewPassword(View view) {
        EditText password = findViewById(R.id.password_field);
        String finalPassword = password.getText().toString();

        auth.updatePassword(finalPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ChangePassword.this,"Password Updated",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChangePassword.this,UserDashBoardActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChangePassword.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
