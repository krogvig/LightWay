package com.example.lightway;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ParkingAPIActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airstations_api);
        callAPI();
    }

    private void callAPI() {
        try {
            new connectToAPI().execute(new URL("https://openstreetgs.stockholm.se/geoservice/api/dd0997ea-f66b-4c80-9b87-3783e3faa6f9/wfs?request=GetFeature&typeName=od_gis:Cykelparkering_Punkt&outputFormat=JSON"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class connectToAPI extends AsyncTask<URL, Integer, String> {        //Create a "ASyncTask" so that the API can be fetched in the background (think AJAX). https://stackoverflow.com/questions/18289623/how-to-use-asynctask/18289746#18289746

        protected String doInBackground(URL... urlInput){       //Take in the API URL, try to return the response as String
            String result = "Nothing has been updated";
            try {
                URL url = urlInput[0];
                HttpURLConnection con = (HttpURLConnection) url.openConnection();       //Open the connection via HTTPS
                con.setRequestMethod("GET");        //Make a GET request on the API URL to get a JSON string back

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); //reportError [type: 211, code: 524300]: Error reading from input stream
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {       //Read the string we got from the GET request and add it to our variable "content"
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();       //Close connection
                result = content.toString();      //This should probably be parsed with GSON ( parse() ) instead
            }

            catch (IOException e) {
                System.out.println(e);
            }
            return result;
        }

        protected void onPostExecute(String result) {
            TextView textView = (TextView) findViewById(R.id.apiTextView);      //Update the TextView with the result
            textView.setText(result);
        }

        private String parse(String jsonLine) {
            JsonElement jelement = new JsonParser().parse(jsonLine);    //Sort of starting it all
            JsonObject  jobject = jelement.getAsJsonObject();       //Gets the first object
            JsonArray jarray = jobject.getAsJsonArray("features");      //Get the array named "features" which contains everything as an array
            jobject = jarray.get(0).getAsJsonObject();      //Get the first value of the "features array" (which only contains one object on index 0)
            jobject = jobject.getAsJsonObject("properties");        //Get the object "properties", which contains most values we are interested in
            String result = jobject.get("Adress").getAsString();        //Get the value of "Adress" as a string
            return result;
        }
    }
}



