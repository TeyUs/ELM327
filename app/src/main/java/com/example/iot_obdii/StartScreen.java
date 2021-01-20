package com.example.iot_obdii;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartScreen extends AppCompatActivity {
    EditText engineCap;
    //ConstraintLayout xx;
    Button xx, b1, b2, b3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        engineCap = findViewById(R.id.engineCaptxt);
        xx = findViewById(R.id.colorPlate);
        b1 = findViewById(R.id.wifi_button);
        b2 = findViewById(R.id.button);
        b3 = findViewById(R.id.button2);

        setColor();


        Intent intent = getIntent();
        if (intent.getBooleanExtra("problem",false)){
            Toast.makeText(this, intent.getStringExtra("problemType"), Toast.LENGTH_LONG).show();
        }
    }

    private void setColor(){
        SharedPreferences sharedPreferences = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        int color = sharedPreferences.getInt("colour",0);

        /*
        xx.setBackgroundColor(getColor(R.color.nice_red));
        b1.setBackgroundResource(R.drawable.button_shape_col1);
        b2.setBackgroundResource(R.drawable.button_shape_col1);
        b3.setBackgroundResource(R.drawable.button_shape_col1);
        */

        switch (color){
            case R.color.colorAccent:
                xx.setBackgroundColor(getColor(R.color.colorAccent));
                b1.setBackgroundResource(R.drawable.button_shape_col2);
                b2.setBackgroundResource(R.drawable.button_shape_col2);
                b3.setBackgroundResource(R.drawable.button_shape_col2);
                break;
            case R.color.nice_blue:
                xx.setBackgroundColor(getColor(R.color.nice_blue));
                b1.setBackgroundResource(R.drawable.button_shape);
                b2.setBackgroundResource(R.drawable.button_shape);
                b3.setBackgroundResource(R.drawable.button_shape);
                break;
            case R.color.nice_red:
                xx.setBackgroundColor(getColor(R.color.nice_red));
                b1.setBackgroundResource(R.drawable.button_shape_col1);
                b2.setBackgroundResource(R.drawable.button_shape_col1);
                b3.setBackgroundResource(R.drawable.button_shape_col1);
                break;
            default:
                Toast.makeText(this, "colorFault", Toast.LENGTH_SHORT).show();
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
        editor.putInt("colour", R.color.nice_blue).apply();
        xx.setBackgroundColor(getColor(R.color.nice_blue));
        b1.setBackgroundResource(R.drawable.button_shape);
        b2.setBackgroundResource(R.drawable.button_shape);
        b3.setBackgroundResource(R.drawable.button_shape);
    }


    public void color2(View view){
        SharedPreferences spref = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putInt("colour", R.color.nice_red).apply();
        xx.setBackgroundColor(getColor(R.color.nice_red));
        b1.setBackgroundResource(R.drawable.button_shape_col1);
        b2.setBackgroundResource(R.drawable.button_shape_col1);
        b3.setBackgroundResource(R.drawable.button_shape_col1);
    }

    public void color3(View view){
        SharedPreferences spref = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        SharedPreferences.Editor editor = spref.edit();
        editor.putInt("colour", R.color.colorAccent).apply();
        xx.setBackgroundColor(getColor(R.color.colorAccent));
        b1.setBackgroundResource(R.drawable.button_shape_col2);
        b2.setBackgroundResource(R.drawable.button_shape_col2);
        b3.setBackgroundResource(R.drawable.button_shape_col2);
    }



}