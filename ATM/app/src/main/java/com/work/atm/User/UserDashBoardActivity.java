package com.work.atm.User;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.work.atm.Login.LoginActivity;
import com.work.atm.R;

import java.io.IOException;
import java.util.concurrent.Executor;

import jp.wasabeef.blurry.Blurry;

public class UserDashBoardActivity extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    //ACTION BAR
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toggle;


    //Firebase Auth and Storage
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    private Uri filePath;
    ImageView profile_picture;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dash_board);

        //Initialization
        /*###################################*/
        auth = FirebaseAuth.getInstance();
        //Profile picture setup
        profile_picture = findViewById(R.id.profile_picture);
        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        /*###################################*/


        //Every functionality will work after this
        //      FINGERPRINT AUTH
        //*****************************************
                setFingerPrintAuthentication();
        //*****************************************


////        //Setting Drawer
        //*****************************************
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //*****************************************


        //*****************************************
        //Setting navigation bar color
        getWindow().setNavigationBarColor(Color.parseColor("#5A5757"));
        //*****************************************


        //Set User Profile Picture From Database
        //*****************************************
        setProfilePictureFromDb();
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        TextView name = findViewById(R.id.name_value);
        TextView email = findViewById(R.id.email_value);

        String[] username = auth.getCurrentUser().getEmail().split("@");

        name.setText(username[0]);
        email.setText(auth.getCurrentUser().getEmail());
        //*****************************************

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deposit_navigation_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                profile_picture.setImageBitmap(bitmap);



                ///UPLOADING IMAGE .... TO Firebase Storage
                uploadImage();
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }


    //Sets Image from database
    private void setProfilePictureFromDb(){

        StorageReference ref = storageReference.child(
                "images/"
                        + auth.getCurrentUser().getUid());


        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(getApplicationContext())
                        .load(uri)
                        .into(profile_picture);
            }
        });
    }



    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + auth.getCurrentUser().getUid());

            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(UserDashBoardActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(UserDashBoardActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }




    // Select Image method
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }


    //Fingerprint Auth popup and text
    private void setFingerPrintAuthentication(){
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(UserDashBoardActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
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




    //Bottom Menu Items
//******************************************************************
    public void moveToDeposit(MenuItem item) {
        Toast.makeText(getApplicationContext(), "Deposit money", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(UserDashBoardActivity.this, TransactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDeposit", true);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void moveToWithdraw(MenuItem item) {
        Toast.makeText(getApplicationContext(), "Withdraw money", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(UserDashBoardActivity.this, TransactActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isDeposit", false);
        intent.putExtras(bundle);

        startActivity(intent);
    }

    public void moveToHistory(MenuItem item) {
        Toast.makeText(getApplicationContext(), "History money", Toast.LENGTH_SHORT).show();
        Intent historyIntent = new Intent(UserDashBoardActivity.this, HistoryActivity.class);
        startActivity(historyIntent);
    }

    public void logout(MenuItem item) {
        Toast.makeText(getApplicationContext(), "In the logout", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(UserDashBoardActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    public void moveToHome(MenuItem item) {
        startActivity(getIntent());
    }
//******************************************************************



    //Top  Toolbar Listener
    //******************************************************************

    public void moveToAddFD(MenuItem item) {
        startActivity(new Intent(this,AddFixedDeposit.class));
    }

    public void moveToListDeposit(MenuItem item) {
        startActivity(new Intent(this,ListDeposit.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        RelativeLayout rootView = findViewById(R.id.fd_drawer);

        Blurry.delete(rootView);
    }

    public void changeEmail(View view) {
        RelativeLayout rootView = findViewById(R.id.fd_drawer);

        Blurry.with(this)
                .radius(10)
                .sampling(8)
                .async()
                .animate(2)
                .onto(rootView);

        startActivity(new Intent(this,ChangeEmail.class));
    }

    public void chagePassword(View view) {
        RelativeLayout rootView = findViewById(R.id.fd_drawer);

        Blurry.with(this)
                .radius(10)
                .sampling(8)
                .async()
                .animate(2)
                .onto(rootView);

        startActivity(new Intent(this,ChangePassword.class));
    }
    //******************************************************************

}