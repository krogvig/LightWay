package com.example.lightway;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailField;
    private Button forgotButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        emailField = findViewById(R.id.emailAddress);

        forgotButton = findViewById(R.id.forgotButton);

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReset();

            }
        });
    }

    private void startReset() {

        final String email = emailField.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(ForgotPasswordActivity.this, "Field is empty!", Toast.LENGTH_LONG).show();
        }else{

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent!", Toast.LENGTH_LONG).show();
                        } else if(!task.isSuccessful()){
                            Toast.makeText(ForgotPasswordActivity.this, "Incorrect email!", Toast.LENGTH_LONG).show();}

                        }
                    });
                }
    }
}







