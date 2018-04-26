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


public class AirStationsAPIActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_api);
        callAPI();
    }

    private void callAPI() {      //Create new thread that can work in the background fetching the API and use the API URL as parameter
        try {
            new connectToAPI().execute(new URL("https://openstreetgs.stockholm.se/geoservice/api/dd0997ea-f66b-4c80-9b87-3783e3faa6f9/wfs?request=GetFeature&typeName=od_gis:Cykelpump_Punkt&cql_filter=Index=%271150056CP%27&outputFormat=JSON"));
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
                result = parse(content.toString());
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

            /*
            * The JSON we get looks sort of like this:
             {
             "features":[
                    {
                        "type":"Feature",
                        "id":"Cykelpump_Punkt.1531149101137275",
                        "geometry":{"type":"Point","coordinates":[144199.483974,6585325.351304,0]},
                        "geometry_name":"GEOMETRY",
                        "properties": {
                            "OBJECT_ID":15311491,
                            "VERSION_ID":1,
                            "FEATURE_TYPE_NAME":"Cykelpump",
                            "FEATURE_TYPE_OBJECT_ID":12436332,
                            "FEATURE_TYPE_VERSION_ID":1,
                            "MAIN_ATTRIBUTE_NAME":"Adress",
                            "MAIN_ATTRIBUTE_VALUE":"Spånga stationsplan ",
                            "MAIN_ATTRIBUTE_DESCRIPTION":"Spånga stationsplan ",
                            "Adress":"Spånga stationsplan ",
                            "Index":"1150056CP",
                            "Ventiler":"Cykelventil och bilventil",
                            "Modell":"Cykelpump integrerad",
                            "Status":"Driftsatt",
                            "Kommentar":" monterad i WC ",
                            "VALID_FROM":201801080000,
                            "VALID_TO":null,
                            "CID":1531149101137275
                            }
                    }
                ],

            "crs":{
                "type":"name",
                "properties":{ "name":"urn:ogc:def:crs:EPSG::3011" }
                }
            }
            */
        }
    }
}



