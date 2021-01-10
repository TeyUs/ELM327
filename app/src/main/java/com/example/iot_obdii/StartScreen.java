package com.example.iot_obdii;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartScreen extends AppCompatActivity {
    EditText engineCap;
    ConstraintLayout xx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        engineCap = findViewById(R.id.engineCaptxt);
        xx = findViewById(R.id.startScreen);

        SharedPreferences sharedPreferences = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        xx.setBackgroundColor(sharedPreferences.getInt("colour",0));
        //xx.setBackgroundColor(Color.WHITE);

        Intent intent = getIntent();
        if (intent.getBooleanExtra("problem",false)){
            Toast.makeText(this, intent.getStringExtra("problemType"), Toast.LENGTH_LONG).show();
        }
    }

    public void goGraphFromStart(View view) {
        Intent intent = new Intent(StartScreen.this, GraphScreen.class);
        intent.putExtra("type", "speed");
        startActivity(intent);
    }

    public void goConsoleFromStart(View view) {
        try {
            String rawCap = engineCap.getText().toString();
            float engCap = Float.parseFloat(rawCap);
            SharedPreferences spref = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
            SharedPreferences.Editor editor = spref.edit();
            editor.putFloat("engCap", engCap).apply();
            Intent intent = new Intent(StartScreen.this, MainActivity.class);
            startActivity(intent);
        }catch (Exception e){
            Toast.makeText(this, "\"Engine Capacity\" information is incorrect!!\nPlease separate with (.)dot", Toast.LENGTH_SHORT).show();
        }
    }

    public void connectWifi(View view){
        WifiManager wifiManager;
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Wifi wifi = new Wifi(wifiManager);
        wifi.connectWifi();
    }

    public void color1(View view){
        SharedPreferences spref = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putInt("colour", getColor(R.color.nice_blue)).apply();
        xx.setBackgroundColor(getColor(R.color.nice_blue));
        Toast.makeText(this, ""+getColor(R.color.nice_blue), Toast.LENGTH_SHORT).show();
    }


    public void color2(View view){
        SharedPreferences spref = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putInt("colour", getColor(R.color.nice_red)).apply();
        xx.setBackgroundColor(getColor(R.color.nice_red));
        Toast.makeText(this, ""+getColor(R.color.nice_red), Toast.LENGTH_SHORT).show();
    }

    public void color3(View view){
        SharedPreferences spref = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putInt("colour", getColor(R.color.colorAccent)).apply();
        xx.setBackgroundColor(getColor(R.color.colorAccent));
        Toast.makeText(this, ""+getColor(R.color.colorAccent), Toast.LENGTH_SHORT).show();
    }



}