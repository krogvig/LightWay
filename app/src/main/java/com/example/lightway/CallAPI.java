package com.example.lightway;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CallAPI extends Fragment {
    private String url;
    private ProgressDialog progressDialog;


    @Override
    public void onAttach(Activity activity){
        super.onAttach (activity);
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
    }
    public void putArguments(Bundle args) {
        url = args.getString("url");

        try {
            new connectToAPI(getActivity()).execute(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class connectToAPI extends AsyncTask<URL, Integer, String> {        //Create a "ASyncTask" so that the API can be fetched in the background (think AJAX). https://stackoverflow.com/questions/18289623/how-to-use-asynctask/18289746#18289746
        Activity gMapsActivity;

        public connectToAPI (Activity a) {
            this.gMapsActivity = a;
        }

        @Override
        protected String doInBackground(URL... urlInput){       //Take in the API URL, try to return the response as String

            String type = "";
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

                if (url.toString().contains("Parking")) {
                    type = "parking";
                }

                else if (url.toString().contains("Pump")){
                    type = "pump";
                }
                parse(content.toString(), type);

            }

            catch (IOException e) {
                System.out.println(e);
            }
            return type;        //Return the list of LatLong coord arrays, which will be returned to onPostExecute()
        }

        @Override
        protected void onPostExecute(String type) {
            GMapsActivity gmaps = ((GMapsActivity) getActivity());
            gmaps.addAllMarkersToMap(type);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        private boolean parse(String jsonLine, String objType) {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            GMapsActivity gma = ((GMapsActivity) getActivity());
            JsonElement jelement = new JsonParser().parse(jsonLine);    //Sort of starting it all
            JsonObject  jobject = jelement.getAsJsonObject();       //Gets the first object

            try {
                if (objType.equals("pump")) {
                    for (Map.Entry<String, JsonElement> entry : jobject.entrySet()) {
                        String key = entry.getKey();
                        JsonObject value = entry.getValue().getAsJsonObject();
                        String adress = "N/A";
                        String ventiler = "N/A";
                        String modell = "N/A";
                        String status = "N/A";
                        String object_id = "N/A";

                        String type = value.getAsJsonPrimitive("type").getAsString();
                        String id = value.getAsJsonPrimitive("id").getAsString();
                        double[] coordinates = {value.getAsJsonArray("coordinates").get(1).getAsDouble(), value.getAsJsonArray("coordinates").get(0).getAsDouble()};
                        String geometry_name = value.getAsJsonPrimitive("geometry_name").getAsString();
                        JsonObject jsonProperties = value.getAsJsonObject("properties");
                        if (jsonProperties.getAsJsonPrimitive("OBJECT_ID") != null) { object_id = jsonProperties.getAsJsonPrimitive("OBJECT_ID").getAsString(); }
                        if (jsonProperties.getAsJsonPrimitive("Adress") != null) { adress = jsonProperties.getAsJsonPrimitive("Adress").getAsString(); }
                        if (jsonProperties.getAsJsonPrimitive("Ventiler") != null) { ventiler = jsonProperties.getAsJsonPrimitive("Ventiler").getAsString(); }
                        if (jsonProperties.getAsJsonPrimitive("Modell") != null) { modell = jsonProperties.getAsJsonPrimitive("Modell").getAsString(); }
                        if (jsonProperties.getAsJsonPrimitive("Status") != null) { status = jsonProperties.getAsJsonPrimitive("Status").getAsString(); }

                        gma.setAllPumps("" + coordinates[0] + "," + coordinates[1], new Pump(type, id, coordinates, geometry_name, object_id, adress, ventiler, modell, status));
                    }
                }
                else {
                    for (Map.Entry<String, JsonElement> entry : jobject.entrySet()) {
                        String key = entry.getKey();
                        JsonObject value = entry.getValue().getAsJsonObject();
                        String typ = "N/A";
                        String antal_enheter = "N/A";
                        String antal_platser= "N/A";
                        String object_id = "N/A";

                        String type = value.getAsJsonPrimitive("type").getAsString();
                        String id = value.getAsJsonPrimitive("id").getAsString();
                        double[] coordinates = {value.getAsJsonArray("coordinates").get(1).getAsDouble(), value.getAsJsonArray("coordinates").get(0).getAsDouble()};
                        String geometry_name = value.getAsJsonPrimitive("geometry_name").getAsString();
                        JsonObject jsonProperties = value.getAsJsonObject("properties");
                        if (jsonProperties.getAsJsonPrimitive("OBJECT_ID") != null) { object_id = jsonProperties.getAsJsonPrimitive("OBJECT_ID").getAsString(); }
                        if (jsonProperties.getAsJsonPrimitive("Typ") != null) { typ = jsonProperties.getAsJsonPrimitive("Typ").getAsString(); }
                        if (jsonProperties.getAsJsonPrimitive("Antal_enheter") != null) { antal_enheter = jsonProperties.getAsJsonPrimitive("Antal_enheter").getAsString(); }
                        if (jsonProperties.getAsJsonPrimitive("Antal_platser") != null) { antal_platser = jsonProperties.getAsJsonPrimitive("Antal_platser").getAsString(); }

                        gma.setAllParkings("" + coordinates[0] + "," + coordinates[1], new Parking(type, id, coordinates, geometry_name, object_id, typ, antal_enheter, antal_platser));
            }
        }
    }
    catch (NullPointerException e) {
        System.out.println(e);
    }

            return true;

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



