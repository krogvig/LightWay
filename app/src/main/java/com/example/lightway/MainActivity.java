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

        Button map = findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {

           @Override
            public void onClick(View v) {
                viewMap(v);
             }
           });

    }

  //Knappen Karta öppnar nu en MapActivity och i den xmlfilen finns en map view att bygga på


    public void airStationsAPIActivity(View view) {
        Intent intent = new Intent(this, AirStationsAPIActivity.class);
        startActivity(intent);
    }

    public void parkingAPIActivity(View view) {
        Intent intent = new Intent(this, ParkingAPIActivity.class);
        startActivity(intent);
    }

    public void viewMap(View view) {
        //Intent intent = new Intent(this, com.example.lightway.MapActivity.class);
        Intent intent = new Intent(this, GMapsActivity.class);
        startActivity(intent);
    }

}



    