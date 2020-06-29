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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.nitri.gauge.Gauge;


public class MainActivity extends Activity {
    MainActivity m = this;
    TextView speedText = null;
    TextView  rpmText = null;
    TextView voltText;
    Integer curSpeed;
    Integer curRPM;
    Gauge speedG, rpmG;
    int tSleepTime = 20;
    protected  ArrayList<Integer> buffer = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        speedText = findViewById(R.id.speedText);
        rpmText = findViewById(R.id.rpmText);
        voltText = findViewById(R.id.voltText);
        Button button = (Button) findViewById(R.id.connectBTN);

        speedG = findViewById(R.id.gaugeSpeed);
        rpmG = findViewById(R.id.gaugeRPM);

        button.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Log.i(DISPLAY_SERVICE, "Button clicked : " + v.getId());

                init();
            }

        });
    }
    public void graphIntent(View view){
        Intent intent = new Intent(MainActivity.this,GraphScreen.class);
        intent.putExtra("type","speed");
        startActivity(intent);
    }


    public  void set_RPM_UI(float rpm){
        rpm /= 1000;
        if(rpm >= 0 && rpm<10)
            rpmG.moveToValue(rpm);
        else
            rpmG.moveToValue(0);
    }

    public  void set_speed_UI(int speed){

        if(speed >= 0 && speed<200)
            speedG.moveToValue(speed);
        else
            speedG.moveToValue(0);
    }

    public  void setVolt(String mvolt){
        voltText.setText(mvolt);
    }

    public  void setSpeed(String mspeed){
        curSpeed = Integer.parseInt(mspeed);
        set_speed_UI(curSpeed);
        speedText.setText(mspeed);
    }

    public  void setRPM(String rpm){
        curRPM = Integer.parseInt(rpm);
        set_RPM_UI((float) curRPM);
        rpmText.setText(rpm);
    }


    public void init(){
        //Context context = this.getApplicationContext();
        Ytask task = new Ytask(m);
        task.execute("");
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



}