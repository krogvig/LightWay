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
import java.util.Map;


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
            new connectToAPI().execute(new URL("https://lightway-90a9c.firebaseio.com/Test.json"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class connectToAPI extends AsyncTask<URL, Integer, ArrayList<String>> {        //Create a "ASyncTask" so that the API can be fetched in the background (think AJAX). https://stackoverflow.com/questions/18289623/how-to-use-asynctask/18289746#18289746

        protected ArrayList<String> doInBackground(URL... urlInput){       //Take in the API URL, try to return the response as String
            ArrayList<String> allLatLongCoordsArraysAsList = new ArrayList<String>();
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
                List<String[]> utmCoordArray = parse(content.toString());     //Send everything we got for parsing and get back only the corrdinates
                for (int x = 0; x < utmCoordArray.size(); x++) {
                    String coords = "34 V " + utmCoordArray.get(x)[0] + " " + utmCoordArray.get(x)[1];        //Format to the correct string for input to the conversion
                    double[] convertedCoords = new CoordCalc().calculateCoord(coords);      // For each string, convert the coordinates to "our" LatLong format
                    allLatLongCoordsArraysAsList.add(""+convertedCoords[0]+","+convertedCoords[1]);       //Format to the correct string for input to the Directions API
                }
            }

            catch (IOException e) {
                System.out.println(e);
            }
            return allLatLongCoordsArraysAsList;        //Return the list of LatLong coord arrays, which will be returned to onPostExecute()
        }

        protected void onPostExecute(ArrayList<String> result) {
            Intent resultIntent = new Intent();     //Create a new intent, add allLatLongCoordsArraysAsList to it and then send it back to GMapsActivity
            resultIntent.putStringArrayListExtra(PUBLIC_STATIC_STRING_IDENTIFIER, result);
            setResult(RESULT_OK, resultIntent);
            finish();
        }

        private List<String[]> parse(String jsonLine) {
            List<String[]> allUTMCoordsArraysAsList = new ArrayList<String[]>();
            JsonElement jelement = new JsonParser().parse(jsonLine);    //Sort of starting it all
            JsonObject  jobject = jelement.getAsJsonObject();       //Gets the first object

            for(Map.Entry<String,JsonElement> entry : jobject.entrySet()) {
                String key = entry.getKey();
                JsonObject value = entry.getValue().getAsJsonObject();

                String type = value.getAsJsonPrimitive("type").getAsString();
                String id = value.getAsJsonPrimitive("id").getAsString();
                double[] coordinates = {value.getAsJsonArray("coordinates").get(0).getAsDouble(),value.getAsJsonArray("coordinates").get(1).getAsDouble()};
                String geometry_name = value.getAsJsonPrimitive("geometry_name").getAsString();
                JsonObject jsonProperties = value.getAsJsonObject("properties");
                String object_id = jsonProperties.getAsJsonPrimitive("OBJECT_ID").getAsString();
                String adress = jsonProperties.getAsJsonPrimitive("Adress").getAsString();
                String ventiler = jsonProperties.getAsJsonPrimitive("Ventiler").getAsString();
                String modell = jsonProperties.getAsJsonPrimitive("Modell").getAsString();
                String status = jsonProperties.getAsJsonPrimitive("Status").getAsString();

                //TODO: Add a "pump" object to a HashMap with all pump objects with the following parameters: pump(type, id, coordinates, geometry_name, object_id, adress, ventiler, modell, status)
            }

            JsonArray jarrayAll = jobject.getAsJsonArray("features");      //Get the array named "features" which contains everything as an array

            for (int x = 0; x<64; x++ )
            {
                jobject = jarrayAll.get(x).getAsJsonObject();      //Iterate every array
                jobject = jobject.getAsJsonObject("geometry");        //Get the object "geometry", which contains the coordinates we are interested in
                JsonArray jarrayCurrentObject = jobject.getAsJsonArray("coordinates");      //Get the coordinates
                String[] coordinates = {jarrayCurrentObject.get(0).getAsString(),jarrayCurrentObject.get(1).getAsString()};     //Add the coordinates to it's own string in the following format: Latitude,Longitude
                allUTMCoordsArraysAsList.add(coordinates);        //Add the string to our list
            }
            return allUTMCoordsArraysAsList ;

            /*
            * The JSON we get looks sort of like this:
                {
				"type":"Cykelpump",
                "id":"Cykelpump_Punkt.1531149101137275",
                "coordinates":[144199.483974,6585325.351304],
                "geometry_name":"GEOMETRY",
                "properties": {
                    "OBJECT_ID":15311491,
                    "Adress":"Spånga stationsplan ",
                    "Ventiler":"Cykelventil och bilventil",
                    "Modell":"Cykelpump integrerad",
                    "Status":"Driftsatt",
                }
            }
            */
        }
    }
}



