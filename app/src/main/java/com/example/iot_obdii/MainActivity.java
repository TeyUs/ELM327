package com.example.iot_obdii;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import de.nitri.gauge.Gauge;


public class MainActivity extends Activity {
    TextView speedText;
    TextView voltText;
    Integer curSpeed = 0;
    Integer curRPM = 0;
    private ProgressBar progressBarRPM;
    private ProgressBar progressBarSpeed;
    int tSleepTime = 20;
    Integer value;
    protected  ArrayList<Integer> buffer = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        Button upbtn = findViewById(R.id.buttonup);
        Button downbtn = findViewById(R.id.buttondown);
        progressBarRPM = (ProgressBar) findViewById(R.id.progressBarRPM);
        progressBarSpeed = (ProgressBar) findViewById(R.id.progressBarSpeed);
        speedText = findViewById(R.id.textView_speed);
        voltText =findViewById(R.id.textVolt);
        upbtn.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                value +=150;
                setSpeed(value.toString());
                setRPM(value.toString());
            }
        });

        downbtn.setOnClickListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                value -=5;
                setSpeed(value.toString());
                setRPM(value.toString());
            }
        });
    }

    public  void setVolt(String mvolt){
        voltText.setText(mvolt);
    }

    public  void setSpeed(String mspeed){
        curSpeed = Integer.parseInt(mspeed);

        speedText.setText(mspeed);
        dataBase();
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
        int kayma = 0;
        int rpmInt = Integer.parseInt(rpm);
        rpmInt = (rpmInt * 3) / 100 + kayma;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBarRPM.setProgress(rpmInt,true);
        }else{
            progressBarRPM.setProgress(rpmInt);
        }
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

    public void graphIntent(View view){
        Intent intent = new Intent(MainActivity.this,GraphScreen.class);
        intent.putExtra("type","speed");
        startActivity(intent);
    }
}