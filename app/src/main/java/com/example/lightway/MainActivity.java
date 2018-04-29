package com.example.lightway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void airStationsAPIActivity(View view) {
        Intent intent = new Intent(this, AirStationsAPIActivity.class);
        startActivity(intent);
    }

    public void parkingAPIActivity(View view) {
        Intent intent = new Intent(this, ParkingAPIActivity.class);
        startActivity(intent);
    }
}



    