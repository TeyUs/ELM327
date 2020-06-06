package com.example.iot_obdii;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
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

import de.nitri.gauge.Gauge;


public class MainActivity extends Activity {
    MainActivity m = this;
    EditText  speedText = null;
    EditText  rpmText = null;
    EditText voltText;
    Integer curSpeed;
    Integer curRPM;
    Gauge speedG, rpmG;
    protected  ArrayList<Integer> buffer = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedText = (EditText) findViewById(R.id.speedText);
        rpmText = (EditText) findViewById(R.id.rpmText);
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
            rpmG.moveToValue(speed);
        else
            rpmG.moveToValue(0);
    }

    public  void setVolt(String mvolt){
        voltText.setText(mvolt);
    }

    public  void setSpeed(String mspeed){
        speedText.setText(mspeed);
    }

    public  void setRPM(String rpm){
        rpmText.setText(rpm);
    }


    public void init(){
        //Context context = this.getApplicationContext();
        Ytask task = new Ytask(m);
        task.execute("");
    }



    private class Ytask extends AsyncTask<String, String, String> {
        int count = 0;
        MainActivity main;
        public  Ytask(MainActivity main){

            this.main = main;
        }

        @Override
        protected void onProgressUpdate(String... params) {

            if(params[1].equalsIgnoreCase("speed")){
                curSpeed = Integer.parseInt(params[0]);
                this.main.setSpeed(params[0]);
                set_speed_UI(curSpeed);
                count = count +1;
            }

            if(params[1].equalsIgnoreCase("rpm")) {
                curRPM = Integer.parseInt(params[0]);
                this.main.setRPM(params[0]);
                set_RPM_UI((float) curRPM);
                count = count +10;
            }

            if(params[1].equalsIgnoreCase("volt")){
                this.main.setVolt(params[0]);
            }

            if(count == 11){
                count = 0;
                try{
                    SQLiteDatabase database = main.openOrCreateDatabase("Data", MODE_PRIVATE,null);
                    database.execSQL("CREATE TABLE IF NOT EXISTS data (speed INTEGER, rpm INTEGER)");
                    database.execSQL("INSERT INTO data (speed, rpm) VALUES ("+curSpeed+", "+curRPM+")");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Socket   wSocket = new Socket("192.168.0.10",35000);
                while (true){
                    sendCmd(wSocket,"01 0D");
                    String value = readSpeedData(wSocket,1);
                    // this.main.setSpeed("mspeedewrere");
                    publishProgress(value,"Speed");


                    sendCmd(wSocket,"01 0C");
                    String valuestr = readRPMData(wSocket,1);
                    // this.main.setSpeed("mspeedewrere");
                    publishProgress(valuestr,"rpm");

                    sendCmd(wSocket,"atrv");
                    String voltage = readVoltData(wSocket,1);
                    // this.main.setSpeed("mspeedewrere");
                    publishProgress(voltage,"volt");

                }
                //}
            }   catch (Exception e) {
                Log.i("com.example.app", e.getMessage());
            }
            return "";
        }



    }

    private void sendCmd(Socket wSocket,String cmd) throws IOException {
        OutputStream out = wSocket.getOutputStream();

        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    private String readRPMData(Socket wSocket,int index) throws Exception {
        List  buffer = new ArrayList<Integer>();
        Thread.sleep(400);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);


        rawData = res.toString().trim();

        if(!rawData.contains("01 0C")){

            return rawData;

        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 0C", "");
        rawData = rawData.replaceAll("41 0C"," ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "rawData: "+rawData);
        Log.i("com.example.app", "data: "+data[0]);
        Log.i("com.example.app", "datawew: "+Integer.decode("0x" + data[0]));
        Log.i("com.example.app", "datawew: "+String.valueOf(Integer.decode("0x" + data[0])));

        int a =  Integer.decode("0x" + data[0]).intValue();

        Log.i("com.example.app", "rawData1: "+rawData);
        Log.i("com.example.app", "data1: "+data[1]);
        Log.i("com.example.app", "datawew1: "+Integer.decode("0x" + data[1]));
        Log.i("com.example.app", "datawew1: "+String.valueOf(Integer.decode("0x" + data[1])));

        int b1 =  Integer.decode("0x" + data[1]).intValue();


        int values = ((a*256)+b1)/4;

        Log.i("com.example.app", "values RPM: "+values);
        return String.valueOf(values);
    }


    private String readSpeedData(Socket wSocket,int index) throws Exception {
        List  buffer = new ArrayList<Integer>();
        Thread.sleep(400);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);


        rawData = res.toString().trim();

        if(!rawData.contains("01 0D")){

            return rawData;

        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 0D", "");
        rawData = rawData.replaceAll("41 0D"," ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "rawData: "+rawData);
        Log.i("com.example.app", "data: "+data[0]);
        Log.i("com.example.app", "datawew: "+Integer.decode("0x" + data[0]));
        Log.i("com.example.app", "datawew: "+String.valueOf(Integer.decode("0x" + data[0])));

        return Integer.decode("0x" + data[0]).toString();

    }


    private String readVoltData(Socket wSocket,int index) throws Exception {
        List  buffer = new ArrayList<Integer>();
        Thread.sleep(400);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);


        rawData = res.toString().trim();

        if(!rawData.contains("atrv")){

            return rawData;

        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("atrv", "");
        rawData = rawData.replaceAll("41 0D"," ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "rawData: "+rawData);
        Log.i("com.example.app", "data: "+data[0]);
        Log.i("com.example.app", "datawew: "+Integer.decode("0x" + data[0]));
        Log.i("com.example.app", "datawew: "+String.valueOf(Integer.decode("0x" + data[0])));

        return Integer.decode("0x" + data[0]).toString();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}