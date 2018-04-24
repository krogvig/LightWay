package com.example.lightway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.gson.*;

import java.net.MalformedURLException;
import java.net.URL;


public class MainAPIActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_api);
        callAPI();
    }


    private void callAPI() {      //Create new thread that can work in the background fetching the API and use the API URL as parameter
        try {
            new connectToAPI().execute(new URL("https://openstreetgs.stockholm.se/geoservice/api/dd0997ea-f66b-4c80-9b87-3783e3faa6f9/wfs?request=GetFeature&typeName=od_gis:Cykelpump_Punkt&cql_filter=Index=%271150056CP%27&outputFormat=JSON"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}



