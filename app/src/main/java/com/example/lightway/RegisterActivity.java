package com.example.lightway;

import android.app.AlertDialog;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private final String pattern = "(?=.*[0-9])(?=\\S+$).{6,32}";
    private EditText emailField;
    private EditText nameField;
    private EditText passwordField;

    private Button registerButton;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() !=null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mProgress = new ProgressDialog(this);


        nameField = findViewById(R.id.userName);
        emailField = findViewById(R.id.emailAddress);
        passwordField = findViewById(R.id.userPassword);

        registerButton = findViewById(R.id.createAccountButton);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorHandling();
            }
        });

    }

    public void errorHandling(){
        final String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if(checkEmptyFields(email, password)){
            return;
        } else if (!(isEmailValid(email))){
            return;
        } else if(!(correctPasswordFormat(password))){
            return;
        }else {
            startRegister(name, email, password);
        }
    }

    public void errorMessage(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
        alertDialog.setTitle("Oops!");
        alertDialog.setMessage(message);
        alertDialog.setButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        alertDialog.show();
    }

    private boolean checkEmptyFields(String email, String password){
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Fields are empty, please try again", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean isEmailValid (String email){
        if(!(!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())){
            errorMessage("Incorrect email input!" + '\n' + "Email should look like: example@domain.com");
            return false;
        }
        return true;
    }

    public boolean correctPasswordFormat(String password){
        if(password != null){
            if(!(password.matches(pattern))){
                errorMessage("Invalid password!" + '\n' + "Password should contain:" + '\n'
                + "a digit" + '\n'
                + "no spaces" + '\n'
                + "and be 6 to 32 characters long" + '\n');
                return false;
            }
            return true;
        }
        return false;
    }

   /*
   private void sendVerificationEmail(final String email){
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Verification email sent to " + email, Toast.LENGTH_LONG).show();
                        } else {
                            Log.e("asd", "sendEmailVerification", task.getException());
                            Toast.makeText(RegisterActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
     */

    private void startRegister(final String name, final String email, String password) {
        final GMapsActivity gmaps = new GMapsActivity();
        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {


            mProgress.setMessage("Creating account ...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = mDatabase.child(user_id);

                        current_user_db.child("distance_traveled").setValue(0.0);
                        current_user_db.child("no_of_rides").setValue(0);

                       Uri uri = Uri.parse("http://2.bp.blogspot.com/-HzFJhEY3KtU/Tea7Ku92cpI/AAAAAAAAALw/uBMzwdFi_kA/s400/1.jpg");
                       updateFirebaseInfo(uri, name);


                        mProgress.dismiss();

                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);

                            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                                    .putBoolean("isfirstrun", true).apply();

                        startActivity(loginIntent);
                    } else {
                        try
                        {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException existEmail){
                            mProgress.cancel();
                            Toast.makeText(RegisterActivity.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            mProgress.cancel();
                        }
                    }
                }
            });
        }

    }

    public void updateFirebaseInfo(Uri photo, String name) {
        FirebaseUser user;

        try {
            user = mAuth.getCurrentUser(); //Gets the current user

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(photo) //Sets the photo from the picture gathered
                    .build();
            user.updateProfile(profileUpdates) //Updates the profile on firebase
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("Tag", "User profile updated.");
                                //Toast.makeText(GMapsActivity.this, "Profile picture has been changed", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (NullPointerException e) {
            System.out.print(e);
        }

    }

}
