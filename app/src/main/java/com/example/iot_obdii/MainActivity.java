package com.example.iot_obdii;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView speedText,voltText, fuelStatusText, coolantTempText, dateTXT;
    Integer curFuel = 0, curCoolant = 0 ,curSpeed = 0, curRPM = 0;
    Double curVolt = 0.0;
    private ProgressBar progressBarRPM;
    private ProgressBar progressBarSpeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarRPM = (ProgressBar) findViewById(R.id.progressBarRPM);
        progressBarSpeed = (ProgressBar) findViewById(R.id.progressBarSpeed);
        speedText = findViewById(R.id.textView_speed);
        voltText =findViewById(R.id.textVolt);
        fuelStatusText =findViewById(R.id.gas_tank);
        coolantTempText =findViewById(R.id.engine_temperature);
        dateTXT = findViewById(R.id.dateText);
        setDate();

    }
    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    public  void setSpeed(String mspeed){
        curSpeed = Integer.parseInt(mspeed);
        speedText.setText(mspeed);
        int speed = Integer.parseInt(mspeed);
        int kayma = 0;
        speed +=kayma;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBarSpeed.setProgress(speed,true);
        }else{
            progressBarSpeed.setProgress(speed);
        }
        speedText.setText(mspeed);
    }
    public  void setRPM(String rpm){
        curRPM = Integer.parseInt(rpm);
        dataBase();
        int kayma = 0;
        int rpmInt = Integer.parseInt(rpm);
        rpmInt = (rpmInt * 3) / 100 + kayma;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBarRPM.setProgress(rpmInt,true);
        }else{
            progressBarRPM.setProgress(rpmInt);
        }
    }

    public  void setVolt(String mvolt){
        voltText.setText(mvolt);
        mvolt.replace("V","").trim();
        //curVolt = Double.parseDouble(mvolt);
        Toast.makeText(this, curVolt.toString(), Toast.LENGTH_SHORT).show();
    }
    public  void setFuelStatus(String m){
        fuelStatusText.setText(m);
        try {
            curFuel = Integer.parseInt(m);
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public  void setcoolantTemp(String m){
        coolantTempText.setText(m);
        try {
            curCoolant = Integer.parseInt(m);
        }catch (Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }



            //dataBaseRare();
    }

    public void init(View view){
        //Context context = this.getApplicationContext();

        Log.i(DISPLAY_SERVICE, "Button clicked : " + view.getId());

        Ytask task = new Ytask(this);
        task.execute();
    }

    public void dataBase(){
        try{
            SQLiteDatabase database = openOrCreateDatabase("Data", MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS data (speed INTEGER, rpm INTEGER)");
            database.execSQL("INSERT INTO data (speed, rpm) VALUES ("+curSpeed+", "+curRPM+")");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void dataBaseRare(){
        try{
            SQLiteDatabase database = openOrCreateDatabase("Data", MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS rare (volt FLOAT, fuel INTEGER, coolant INTEGER)");
            database.execSQL("INSERT INTO rare (volt, fuel, coolant) VALUES ("+curVolt+", "+curFuel+", "+curCoolant+")");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void graphIntent(View view){
        Intent intent = new Intent(MainActivity.this,GraphScreen.class);
        intent.putExtra("type","speed");
        startActivity(intent);
    }
    public void setDate(){
        Calendar calendar = Calendar.getInstance();
        String s = calendar.getTime().toString();
        String [] token = s.split(" ");
        String date = token[2]+ " " + token[1] + " " + token[5];
        String [] tokenDate = token[3].split(":");
        String timeInDay = tokenDate[0]+ ":" + tokenDate[1];
        System.out.println(timeInDay);
        System.out.println(date);
        this.dateTXT.setText(date);
    }
}