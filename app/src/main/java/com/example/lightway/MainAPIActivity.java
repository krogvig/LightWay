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
        fetchAPI();
    }

    private void fetchAPI () {
        TextView apiTextView = findViewById(R.id.apiTextView);
        String result = callAPI();
        apiTextView.setText(result);      //Update the textView with the result of the API request
    }

    private String callAPI() {      //Create new thread that can work in the background fetching the API and use the API URL as parameter
        try {
            URL inputURL = new URL("https://openstreetgs.stockholm.se/geoservice/api/dd0997ea-f66b-4c80-9b87-3783e3faa6f9/wfs?request=GetFeature&typeName=od_gis:Cykelpump_Punkt&cql_filter=Index=%271150056CP%27&outputFormat=JSON")
            String result = new connectToAPI().doInBackground(inputURL);
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private String parse(String jsonLine) {     //To parse the JSON respond as String/Java object (?). Not done.
        JsonElement jelement = new JsonParser().parse(jsonLine);
        JsonObject jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject("data");
        jobject.get("Adress");
        JsonArray jarray = jobject.getAsJsonArray("translations");
        jobject = jarray.get(0).getAsJsonObject();
        String result = jobject.get("translatedText").getAsString();
        return result;
    }
}



