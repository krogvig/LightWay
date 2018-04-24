package com.example.lightway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainAPIActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_api);
        Intent intent = getIntent();

        TextView apiTextView = findViewById(R.id.apiTextView);
        apiTextView.setText("TEST");
    }


}
