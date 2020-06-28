package com.example.iot_obdii;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Ytask extends AsyncTask<Void, String, Void> {

    MainActivity main;
    public  Ytask(MainActivity main){

        this.main = main;
    }

    @Override
    protected void onProgressUpdate(String... params) {

        this.main.setSpeed(params[0]);

        this.main.setRPM(params[1]);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Socket wSocket = new Socket("192.168.0.10",35000);
            while (true){
                sendCmd(wSocket,"01 0D");
                String speedData = readSpeedData(wSocket,1);
                // this.main.setSpeed("mspeedewrere");
                //publishProgress(value,"Speed");


                sendCmd(wSocket,"01 0C");
                String rpmData = readRPMData(wSocket,1);
                // this.main.setSpeed("mspeedewrere");
                //publishProgress(valuestr,"rpm");

                publishProgress(speedData,rpmData);

            }
            //}
        }   catch (Exception e) {
            Log.i("com.example.app", e.getMessage());
        }
        return null;
    }

    private void sendCmd(Socket wSocket,String cmd) throws IOException {
        OutputStream out = wSocket.getOutputStream();

        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    private String readRPMData(Socket wSocket,int index) throws Exception {
        List buffer = new ArrayList<Integer>();
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

}

