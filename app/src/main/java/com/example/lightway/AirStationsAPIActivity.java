package com.example.lightway;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class AirStationsAPIActivity extends AppCompatActivity  {
    public static final String PUBLIC_STATIC_STRING_IDENTIFIER = "Coordinates";
    public static final int RESULT_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_api);
        callAPI();
    }

    private void callAPI() {      //Create new thread that can work in the background fetching the API and use the API URL as parameter
        try {
            new connectToAPI().execute(new URL("https://openstreetgs.stockholm.se/geoservice/api/dd0997ea-f66b-4c80-9b87-3783e3faa6f9/wfs?request=GetFeature&typeName=od_gis:Cykelpump_Punkt&outputFormat=JSON"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class connectToAPI extends AsyncTask<URL, Integer, ArrayList<String>> {        //Create a "ASyncTask" so that the API can be fetched in the background (think AJAX). https://stackoverflow.com/questions/18289623/how-to-use-asynctask/18289746#18289746

        protected ArrayList<String> doInBackground(URL... urlInput){       //Take in the API URL, try to return the response as String
            ArrayList<String> coordsList = new ArrayList<String>();
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

                //Call this method and send in UTM-String in this form: 34 V 327680.04 6543920.33
                List<String[]> utmCoordArray = parse(content.toString());     //Get the UTM coordinates
                for (int x = 0; x<utmCoordArray.size(); x++) {
                    String coords = "34 V " + utmCoordArray.get(x)[0] + " " + utmCoordArray.get(x)[1];        //Format to the correct string for input to the conversion
                    double[] convertedCoords = new CoordCalc().calculateCoord(coords);      //Convert the coordinates
                    coordsList.add(""+convertedCoords[0]+","+convertedCoords[1]);       //Format to the correct string for input to the Directions API
                }

                return coordsList;      //Return the coordinates in the correct format Lat/Long
            }

            catch (IOException e) {
                System.out.println(e);
            }
            return coordsList;
        }

        protected void onPostExecute(ArrayList<String> result) {
            Intent resultIntent = new Intent();
            resultIntent.putStringArrayListExtra(PUBLIC_STATIC_STRING_IDENTIFIER, result);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        private List<String[]> parse(String jsonLine) {
            List<String[]> result = new ArrayList<String[]>();
            JsonElement jelement = new JsonParser().parse(jsonLine);    //Sort of starting it all
            JsonObject  jobject = jelement.getAsJsonObject();       //Gets the first object
            JsonArray jarrayAll = jobject.getAsJsonArray("features");      //Get the array named "features" which contains everything as an array
            for (int x = 0; x<64; x++ )
            {
                jobject = jarrayAll.get(x).getAsJsonObject();      //Get the first value of the "features array" (which only contains one object on index 0)
                jobject = jobject.getAsJsonObject("geometry");        //Get the object "geometry", which contains the coordinates we are interested in
                JsonArray jarrayCurrentObject = jobject.getAsJsonArray("coordinates");
                String[] coordinates = {jarrayCurrentObject.get(0).getAsString(),jarrayCurrentObject.get(1).getAsString()};
                result.add(coordinates);
            }
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



