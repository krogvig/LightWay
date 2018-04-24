package com.example.lightway;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lightway.R;

public class MapActivity extends AppCompatActivity {

    /*Just nu gör denna inget mer än att den har en kartaktivitet i xml-filen,
    gissar ett en behöver importera googles kartapi innan man pluggar in det
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }


}
