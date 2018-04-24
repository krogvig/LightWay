package com.example.lightway;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class connectToAPI extends AsyncTask<URL, Integer, String> {        //Connect to the URL and somehow send the response back to MainAPIActivity, not done, working on this now

        protected String doInBackground(URL... urlInput){       //Take in the API URL, try to return the response as String
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
                return content.toString();
            }

            catch (IOException e) {
                System.out.println(e);
            }
            return "Error";
        }

    protected void onPostExecute(String result) {
        TextView textView = (TextView) findViewById(R.id.apiTextView);      //Working on the findViewByID error...
        textView.setText(result);
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
