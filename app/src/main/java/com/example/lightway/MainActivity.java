package com.example.lightway;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    private Button map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = (Button)findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {

           @Override
            public void onClick(View v) {
                viewMap();
             }
           });

    }

  //Knappen Karta öppnar nu en MapActivity och i den xmlfilen finns en map view att bygga på

    public void viewMap() {
        Intent intent = new Intent(this, com.example.lightway.MapActivity.class);
        startActivity(intent);
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



    