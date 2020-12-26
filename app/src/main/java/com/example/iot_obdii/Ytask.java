package com.example.iot_obdii;

import android.content.Intent;
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
    Integer miktar = 3;

    public Ytask(MainActivity main) {
        this.main = main;
    }

    @Override
    protected void onProgressUpdate(String... params) {
        if(params[0] ==  "1"){
        this.main.writeToFile(params[1] + "==>>" + params[2]);
        }else{
            this.main.writeToFile(params[1]);
        }

        /*
        this.main.setSpeed(params[1]);
        System.out.println("Params speed : " + params[1]);
        this.main.setRPM(params[2]);
        System.out.println("Params rpm : " + params[2]);

        switch (params[0]) {//params[0]
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
        }
        */
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Socket wSocket = new Socket("192.168.0.10", 35000);
            while(true) {
                for (int j = 0; j < 10; j++) {
                    for (int i = 0; i < 197; i++) {
                        String code = "";
                        String retrive = "";
                        try {
                            code = Integer.toHexString(j) + " " + Integer.toHexString(i);
                            retrive = readPIDData(wSocket, code);
                            publishProgress("1", code, retrive);

                            Log.i("com.example.app", code+ "==>>" +retrive);
                        } catch (Exception e) {
                            publishProgress("0", "Error : " + code);
                            if (false) {
                                main.writeToFile("");
                                main.writeToFile("Error : " + code);

                                Log.i("com.example.app",  "Errorxxxx : " + e.getMessage());
                            }
                        }
                    }
                }
            }

            /*
            Integer counter = 0;
            while (false) {
                //Sürekli çekilecek veriler

                String speedData = readSpeedData(wSocket, "01 0D");
                String rpmData = readRPMData(wSocket,"01 0C");




                switch (1) {//counter % miktar
                    case 0:
                        String voltageData = readVoltData(wSocket, "atrv");
                        publishProgress("1", speedData, rpmData, voltageData);
                        break;

                    case 1:
                        String fuelLevelData = readFuelData(wSocket, "01 2F");
                        publishProgress("2", speedData, rpmData, fuelLevelData);
                        break;

                    case 2:
                        String coolantTempData = readCoolantTempData(wSocket, "01 05");
                        publishProgress("3", speedData, rpmData, coolantTempData);
                        break;

                    default:
                        publishProgress("4", speedData, rpmData);
                        break;
                }
                counter++;
            }*/

        } catch (Exception e) {
            Log.i("com.example.app", e.getMessage());
            this.cancel(true);
            Intent intent = new Intent(main, StartScreen.class);
            intent.putExtra("problemType", e.getMessage());
            intent.putExtra("problem", true);
            main.startActivity(intent);
        }
        return null;
    }

    private String readPIDData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket, cmd);
        Thread.sleep(threadSleepTime);
        String rawData = null;
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);

        rawData = res.toString().trim();

        Log.i("com.example.app", "All Data: " + rawData);
        return rawData;
    }

    private void sendCmd(Socket wSocket, String cmd) throws IOException {
        OutputStream out = wSocket.getOutputStream();
        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    private String readRPMData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket, cmd);
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
        sendCmd(wSocket, cmd);
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
        sendCmd(wSocket, cmd);
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

        if (!rawData.contains("atrv")) {

            return rawData;
        }
        rawData = rawData.replace("atrv", "");

        Log.i("com.example.app", "Volt Data: " + rawData);
        return rawData;

    }

    private String readFuelData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket, cmd);
        Thread.sleep(threadSleepTime);
        String rawData = null;
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();
        System.out.println("1");
        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);

        System.out.println("2");

        rawData = res.toString().trim();

        System.out.println("11" + rawData);
        System.out.println("3");
        if (!rawData.contains("01 2F")) {
            System.out.println("22" + rawData);
            return rawData;
        }
        System.out.println("4");
        System.out.println("33" + rawData);

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 2F", "");
        rawData = rawData.replaceAll("41 2F", " ").trim();
        String[] data = rawData.split(" ");

        System.out.println("5");
        Log.i("com.example.app", "Fuel Data: " + Integer.decode("0x" + data[0]));

        Integer x = (Integer) (Integer.decode("0x" + data[0]));
        Double ort = (double) x;
        ort = ort * 50 / 240;
        System.out.println("6");
        return ort.toString();
    }

    private String readCoolantTempData(Socket wSocket, String cmd) throws Exception {
        sendCmd(wSocket, cmd);
        Thread.sleep(threadSleepTime);
        String rawData = null;
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);


        rawData = res.toString().trim();

        if (!rawData.contains("01 05")) {
            return rawData;
        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll("01 05", "");
        rawData = rawData.replaceAll("41 05", " ").trim();
        String[] data = rawData.split(" ");

        Log.i("com.example.app", "Coolant Data: " + Integer.decode("0x" + data[0]));

        return Integer.decode("0x" + data[0]).toString();
    }

}