package com.example.iot_obdii;

import android.content.Intent;
import android.content.SharedPreferences;
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
    public Integer threadSleepTime = 20;
    Integer miktar = 2;
    String ErrorStr = "Error";

    public Ytask(MainActivity main) {
        this.main = main;
    }

    @Override
    protected void onProgressUpdate(String... params) {

        this.main.setSpeed(params[1]);
        System.out.println("Params speed : " + params[1]);
        this.main.setRPM(params[2]);
        System.out.println("Params rpm : " + params[2]);


        if (params[3].matches( "")){
            System.out.println("params[0] : " + params[0]);
            System.out.println("params[3] : " + params[3]);
            params[0] = "4";
        }

        switch (params[0]) {
            case "1":
                this.main.setVolt(params[3]);
                System.out.println("Params volt : " + params[3]);
                break;
            case "2":
                this.main.setFuelStatus(params[3]);
                System.out.println("Params fuel : " + params[3]);
                break;
            case "3":
                this.main.setcoolantTemp(params[3]);
                System.out.println("Params coolent : " + params[3]);
                break;
            default:
                break;
        }

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Socket wSocket = new Socket("192.168.0.10", 35000);
            Integer counter = 0;
            while (true) {
                //Sürekli çekilecek veriler
                String speedData = readSpeedData(wSocket, "01 0D");
                if(speedData.matches(ErrorStr)) speedData = "";
                String rpmData = readRPMData(wSocket,"01 0C");
                if(rpmData.matches(ErrorStr)) rpmData = "";

                switch (counter % miktar) {
                    case 0:
                        String voltageData = readVoltData(wSocket, "atrv");
                        if(voltageData.matches(ErrorStr)) voltageData = "";
                        publishProgress("1", speedData, rpmData, voltageData);

                        String coolantTempData = readCoolantTempData(wSocket, "01 05");
                        if(coolantTempData.matches(ErrorStr)) coolantTempData = "";
                        publishProgress("3", speedData, rpmData, coolantTempData);
                        break;

                    case 1:
                        String fuelRate = readFuelRate(wSocket);
                        publishProgress("2", speedData, rpmData, fuelRate);
                        break;

                    default:
                        publishProgress("4", speedData, rpmData);
                        break;
                }
                counter++;
            }
        } catch (Exception e) {
            Log.i("com.example.app", e.getMessage());
            this.cancel(true);
            Intent intent = new Intent(main, StartScreen.class);
            intent.putExtra("problemType", e.getMessage());
            intent.putExtra("problem",true);
            main.startActivity(intent);
        }
        return null;
    }

    private void sendCmd(Socket wSocket, String cmd) throws IOException {
        OutputStream out = wSocket.getOutputStream();
        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    private String readFuelRate(Socket wSocket) throws Exception {
        int rpm = main.curRPM;

        String raw_MapData = readMAPData(wSocket, "01 0B");
        if(raw_MapData.matches(ErrorStr)) raw_MapData = "";
        int map = Integer.parseInt(raw_MapData);

        String raw_IATData = readIATData(wSocket, "01 0F");
        if(raw_IATData.matches(ErrorStr)) raw_IATData = "";
        int iat = Integer.parseInt(raw_IATData);

        double volEff = 1.0;            // araştır

        double imap = rpm * map / (double) iat / 2.0;

        double goa = (imap / 60) * (volEff) * main.engCap * 28.97 / 8.314;

        double cons = goa / 14.7 / 740 * 3600 * (0.7 /5.2 );

        return Double.toString(cons);
    }

    private String readIATData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket,cmd);
        List buffer = new ArrayList<Integer>();
        Thread.sleep(threadSleepTime);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);
        rawData = res.toString().trim();

        if (rawData.contains("NO DATA")){
            return ErrorStr;
        }
        if (!rawData.contains("01 0F")) {
            return rawData;
        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 0F", "");
        rawData = rawData.replaceAll("41 0F", " ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "Speed Data: " + Integer.decode("0x" + data[0]));

        return Integer.decode("0x" + data[0]).toString();
    }

    private String readMAPData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket,cmd);
        List buffer = new ArrayList<Integer>();
        Thread.sleep(threadSleepTime);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);


        rawData = res.toString().trim();

        if (rawData.contains("NO DATA")){
            return ErrorStr;
        }
        if (!rawData.contains("01 0B")) {
            return rawData;
        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 0B", "");
        rawData = rawData.replaceAll("41 0B", " ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "Speed Data: " + Integer.decode("0x" + data[0]));

        return Integer.decode("0x" + data[0]).toString();
    }

    private String readRPMData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket,cmd);
        List buffer = new ArrayList<Integer>();
        Thread.sleep(threadSleepTime);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);

        rawData = res.toString().trim();

        if ( rawData.contains("NO DATA")){
            return ErrorStr;
        }

        if (!rawData.contains("01 0C")) {

            return rawData;

        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 0C", "");
        rawData = rawData.replaceAll("41 0C", " ").trim();
        String[] data = rawData.split(" ");

        int a = Integer.decode("0x" + data[0]).intValue();
        int b1 = Integer.decode("0x" + data[1]).intValue();


        int values = ((a * 256) + b1) / 4;

        Log.i("com.example.app", "values RPM: " + values);
        return String.valueOf(values);
    }

    private String readSpeedData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket,cmd);
        List buffer = new ArrayList<Integer>();
        Thread.sleep(threadSleepTime);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);


        rawData = res.toString().trim();

        if (rawData.contains("NO DATA")){
            return ErrorStr;
        }
        if (!rawData.contains("01 0D")) {
            return rawData;
        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 0D", "");
        rawData = rawData.replaceAll("41 0D", " ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "Speed Data: " + Integer.decode("0x" + data[0]));

        return Integer.decode("0x" + data[0]).toString();
    }

    private String readVoltData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket,cmd);
        List buffer = new ArrayList<Integer>();
        Thread.sleep(threadSleepTime);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);

        rawData = res.toString().trim();

        if (rawData.contains("NO DATA")){
            return ErrorStr;
        }

        if (!rawData.contains("atrv")) {

            return rawData;
        }
        rawData = rawData.replace("atrv", "");

        Log.i("com.example.app", "Volt Data: " + rawData);
        return rawData;

    }

    private String readFuelData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket,cmd);
        List buffer = new ArrayList<Integer>();
        Thread.sleep(threadSleepTime);
        String rawData = null;
        String value = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);
        rawData = res.toString().trim();

        if (rawData.contains("NO DATA")){
            return ErrorStr;
        }
        if (!rawData.contains("01 2F")) {

            return rawData;

        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 2F", "");
        rawData = rawData.replaceAll("41 2F", " ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "Fuel Data: " + Integer.decode("0x" + data[0]));

        Integer x = (Integer) (Integer.decode("0x" + data[0]));
        Double ort = (double) x;
        ort = ort * 100.0 / 255.0;
        return ort.toString();
    }

    private String readCoolantTempData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket,cmd);
        Thread.sleep(threadSleepTime);
        String rawData = null;
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);
        rawData = res.toString().trim();

        if (rawData.contains("NO DATA")){
            return ErrorStr;
        }
        if (!rawData.contains("01 05")) {
            return rawData;
        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 05", "");
        rawData = rawData.replaceAll("41 05", " ").trim();
        String[] data = rawData.split(" ");

        int var = Integer.decode("0x" + data[0]);
        var -=  40;

        String xx = "" +var;
        Log.i("com.example.app", "Coolant Data: " + xx);


        return xx;
    }

}