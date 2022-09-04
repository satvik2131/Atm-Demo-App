package com.work.atm.User;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.work.atm.R;

public class ChangeEmail extends AppCompatActivity {
    DatabaseReference ref;
    FirebaseAuth auth;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email);
        getSupportActionBar().hide();

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Window window = getWindow();
        window.setLayout(((width/10)*8),((height/10)*5));
        getWindow().setBackgroundDrawable(getDrawable(R.drawable.popup));
    }


    public void setNewEmail(View view) {
        EditText email =(EditText) findViewById(R.id.email_field_value);
        String finalEmail = email.getText().toString();
        String uid = auth.getCurrentUser().getUid();



        auth.getCurrentUser().updateEmail(finalEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ChangeEmail.this,"Email Updated",Toast.LENGTH_SHORT).show();

                ref.child("user").child(uid).child("email").setValue(finalEmail);
                startActivity(new Intent(ChangeEmail.this,UserDashBoardActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("e---",e.getMessage());
                Toast.makeText(ChangeEmail.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
}
