package com.example.lightway;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class connectToAPI extends AsyncTask<URL, Void, String> {        //Connect to the URL and somehow send the response back to MainAPIActivity, not done, working on this now
        protected String doInBackground(URL... urlInput){
            try {
                URL url = urlInput[0];
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");        //Make a GET request on the API URL to get a JSON string back

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {       //Read the string we got from the GET request and add it to our variable "content"
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                //return parse(content.toString());
                return content.toString();
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            return "Error";
        }
}
