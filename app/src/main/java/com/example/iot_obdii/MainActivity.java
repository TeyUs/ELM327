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
    MainActivity activity = this;
    EditText  speedText = null;
    EditText  rpmText = null;
    EditText voltage;
    protected  ArrayList<Integer> buffer = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedText = findViewById(R.id.editText1);
        rpmText = findViewById(R.id.editText2);
        voltage = findViewById(R.id.voltage);
    }

    public void connectBTN(View view){
        Log.i(DISPLAY_SERVICE, "Connection button clicked : " + view.getId());
        //Context context = this.getApplicationContext();
        Ytask task = new Ytask(activity);
        task.execute("");
    }

    public void setSpeed(Integer speed){
        speedText.setText(speed.toString());
    }

    public  void setRPM(Integer rpm){
        rpmText.setText(rpm.toString());
    }
    public void setVoltage(Integer volt){
        voltage.setText(volt.toString());
    }

    private class Ytask extends AsyncTask<String, Integer, String> {

        MainActivity main;

        public  Ytask(MainActivity main){
            this.main = main;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {

            if(params[1] == 0)
                this.main.setSpeed(params[0]);

            if(params[1] == 1)
                this.main.setRPM(params[0]);
            if(params[1] == 2)
                this.main.setVoltage(params[0]);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Socket   wSocket = new Socket("192.168.0.10",35000);
                while (true){
                    sendCmd(wSocket,"01 0D");
                    Integer speedData = readSpeedData(wSocket,1);
                    // this.main.setSpeed("mspeedewrere");
                    publishProgress(speedData,0); // 0 Speed

                    sendCmd(wSocket,"01 0C");
                    int RPMData = readRPMData(wSocket,1);
                    // this.main.setSpeed("mspeedewrere");
                    publishProgress(RPMData,1);  // 1 RPM

                    sendCmd(wSocket,"atrv");
                    int voltageData = readVoltageData(wSocket,1);
                    // this.main.setSpeed("mspeedewrere");
                    publishProgress(voltageData,2); // 2 voltage
/*
                    sendCmd(wSocket,"01 05");
                    String coolantData = readRPMData(wSocket,1);
                    // this.main.setSpeed("mspeedewrere");
                    publishProgress(coolantData,"coolant");*/
                }
                //}
            }   catch (Exception e) {
                Log.i("com.example.app", e.getMessage());
            }
            return "";
        }
    }
    private int readVoltageData(Socket wSocket,int index) throws Exception {
        List  buffer = new ArrayList<Integer>();
        Thread.sleep(100);//eski değer 400//******************
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

            return Integer.parseInt(rawData);

        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("atrv", "");
        rawData = rawData.replaceAll("41 0D"," ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "rawData2: "+rawData);
        Log.i("com.example.app", "data2: "+data[0]);
        Log.i("com.example.app", "datawew2: "+Integer.decode("0x" + data[0]));
        Log.i("com.example.app", "datawew2: "+String.valueOf(Integer.decode("0x" + data[0])));

        return Integer.decode("0x" + data[0]);
        //return Integer.decode(data[0]);
    }

    private void sendCmd(Socket wSocket,String cmd) throws IOException {
        OutputStream out = wSocket.getOutputStream();
        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    private int readRPMData(Socket wSocket,int index) throws Exception {
        Thread.sleep(100);//eski değer 400//******************
        String rawData = null;
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);

        rawData = res.toString().trim();

        if(!rawData.contains("01 0C")){
            return Integer.parseInt(rawData.toString());
        }

        rawData = rawData.replaceAll("\r", " ");
        System.out.println("rawData Ogrenme : "+rawData);
        rawData = rawData.replaceAll("01 0C", "");
        System.out.println("rawData Ogrenme : "+rawData);
        rawData = rawData.replaceAll("41 0C"," ").trim();
        System.out.println("rawData Ogrenme : "+rawData);
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
        return values;
    }

    private Integer readSpeedData(Socket wSocket,int index) throws Exception {
        List  buffer = new ArrayList<Integer>();
        Thread.sleep(100);//eski değer 400//******************
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

            return Integer.parseInt(rawData);

        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 0D", "");
        rawData = rawData.replaceAll("41 0D"," ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "rawData: "+rawData);
        Log.i("com.example.app", "data: "+data[0]);
        Log.i("com.example.app", "datawew: "+Integer.decode("0x" + data[0]));
        Log.i("com.example.app", "datawew: "+String.valueOf(Integer.decode("0x" + data[0])));

        return Integer.decode("0x" + data[0]);
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