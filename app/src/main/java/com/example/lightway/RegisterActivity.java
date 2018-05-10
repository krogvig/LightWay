package com.example.lightway;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    private EditText emailField;
    private EditText nameField;
    private EditText passwordField;

    private Button registerButton;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

    private void errorHandling(){
        final String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        boolean emptyFields = checkEmptyFields(email, password);
        boolean passwordFormat = correctPasswordFormat(password);

        if (emptyFields || !passwordFormat){
            return;
        } else {
            startRegister(name, email, password);
        }
    }

    private boolean checkEmptyFields(String email, String password){
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this, "Fields are empty, please try again", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean correctPasswordFormat(String password){
        if(password != null){
            if(password.length() < 6){
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("Oops!");
                alertDialog.setMessage("Password too short!" + '\n' + "Minimum 6 characters.");
                alertDialog.setButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                alertDialog.show();
                return false;
            }
            return true;
        }
        return false;
    }

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

    private void startRegister(final String name, final String email, String password) {

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgress.setMessage("Creating account ...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        sendVerificationEmail(email);
                        String user_id = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = mDatabase.child(user_id);

                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");

                        mProgress.dismiss();

                        mAuth.signOut();
                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                }
            });
        }

    }

}


