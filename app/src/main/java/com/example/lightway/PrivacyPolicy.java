package com.example.lightway;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PrivacyPolicy extends AppCompatActivity {

    private Button English;
    private Button Swedish;
    private Button OK;
    private TextView EnglishText;
    private TextView Swedishtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_and_a);

        English = findViewById(R.id.englishbtn);
        Swedish = findViewById(R.id.swedishbtn);
        OK = findViewById(R.id.Okbtn);
        EnglishText = findViewById(R.id.textEnglish);
        Swedishtext = findViewById(R.id.textSwedish);


        }





    public void ShowEnglish(){
        EnglishText.setVisibility(View.VISIBLE);
        Swedishtext.setVisibility(View.INVISIBLE);
    }


    public void ShowSwedish() {
        EnglishText.setVisibility(View.INVISIBLE);
        Swedishtext.setVisibility(View.VISIBLE);
    }
}
