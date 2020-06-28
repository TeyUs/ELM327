package com.example.iot_obdii;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {
    MainActivity m = this;
    EditText  speedText = null;
    EditText  rpmText = null;
    EditText  voltText = null;
    Integer curSpeed;
    Integer curRPM;
    protected  ArrayList<Integer> buffer = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedText = (EditText) findViewById(R.id.editText1);
        rpmText = (EditText) findViewById(R.id.editText2);
        voltText = (EditText) findViewById(R.id.volttext);
        Button button = (Button) findViewById(R.id.connectBTN);

        button.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Log.i(DISPLAY_SERVICE, "Button clicked : " + v.getId());

                init();
            }

        });
    }


    public  void setSpeed(String mspeed){
        curSpeed = Integer.parseInt(mspeed);
        speedText.setText(mspeed);
    }

    public  void setRPM(String rpm){
        curRPM = Integer.parseInt(rpm);
        rpmText.setText(rpm);
    }

    public void setVolt(String volt){
        voltText.setText(volt);
    }


    public void init(){
        //Context context = this.getApplicationContext();
        Ytask task = new Ytask(m);
        task.execute();
    }


}