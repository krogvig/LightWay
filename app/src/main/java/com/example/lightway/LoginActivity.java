package com.example.lightway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText uName = (EditText) findViewById(R.id.username);
        final EditText uPassword = (EditText) findViewById(R.id.userPassword);

        final Button loginButton = (Button) findViewById(R.id.loginButton);
    }
}
