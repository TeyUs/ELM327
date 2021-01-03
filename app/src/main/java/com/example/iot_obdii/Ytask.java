package com.example.iot_obdii;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ytask extends AsyncTask<String, String, Void> {
    MainActivity main;
    Socket wSocket;
    Integer maf =1, speed;
    public Integer threadSleepTime = 20;
    Integer miktar = 3;

    public Ytask(MainActivity main) {

        this.main = main;
    }

    @Override
    protected void onProgressUpdate(String... params) {


        this.main.setSpeed(params[1]);
        System.out.println("Params speed : " + params[1]);
        this.main.setRPM(params[2]);
        System.out.println("Params rpm : " + params[2]);

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
        }
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            wSocket = new Socket("192.168.0.10", 35000);
            Integer counter = 0;
            while (true) {
                //Sürekli çekilecek veriler

                String speedData = readSpeedData("01 0D");

                String rpmData = readRPMData("01 0C");

                if (!readData("rpm","01 0C").contains("NO DATA")){

                    parseData(readData("rpm","01 0C"));
                }

                if (!readData("maf","01 10").contains("NO DATA")){
                    maf = parseData(readData("maf","01 10"));
                    Double value = (2.7 * speed) / maf;

                    main.fuelDetails.setText(maf + value.toString());
                }else {
                    System.out.println("sıçtı bekiiiiiiir");
                }






                switch (counter % miktar) {//counter % miktar
                    case 0:
                        String voltageData = readVoltData("atrv");
                        //parseData(readData("volt","atrv"));
                        publishProgress("1", speedData, rpmData, voltageData);
                        break;

                    case 1:
                        String fuelLevelData = readFuelData("01 2F");
                        //parseData(readData("fuelratio","01 2F"));
                        publishProgress("2", speedData, rpmData, fuelLevelData);
                        break;

                    case 2:
                        String coolantTempData = readCoolantTempData("01 05");
                        //parseData(readData("coolant", "01 05"));
                        publishProgress("3", speedData, rpmData, coolantTempData);
                        break;

                    default:
                        publishProgress("4", speedData, rpmData);
                        break;
                }
                counter++;
            }
        } catch (Exception e) {
            Log.i("com.example.app", "büyük hata " + Objects.requireNonNull(e.getMessage()));
            this.cancel(true);
            Intent intent = new Intent(main, StartScreen.class);
            intent.putExtra("problemType", e.getMessage());
            intent.putExtra("problem",true);
            main.startActivity(intent);
        }
        return null;
    }

    private void sendCmd(String cmd) throws IOException {
        OutputStream out = wSocket.getOutputStream();
        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    private String readData(String process, String cmd) throws Exception {
        sendCmd(cmd);
        Thread.sleep(threadSleepTime);
        String rawData = "";
        InputStream in = wSocket.getInputStream();
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>')
            res.append((char) b);

        rawData = res.toString().trim();
        System.out.println("readDatactrl       " + rawData+"son");

        if (rawData.contains("N")){
            System.out.println("çıkıyoz abi");
            return "NO DATA";
        }

        if (!rawData.contains(cmd)) {
            return rawData;
        }

        rawData = rawData.replaceAll("\r", " ");
        rawData = rawData.replaceAll(cmd, "");

        cmd = '4' + cmd.substring(1);
        rawData = rawData.replaceAll(cmd, " ").trim();

        Log.i("com.example.app", process +"  => "+ cmd + " Data => " + rawData);
        rawData = rawData.substring(0,rawData.indexOf('\n'));

        Log.i("com.example.app", process +"  => "+ cmd + " Data => " + rawData);

        System.out.println(process +"  => "+ cmd + " Data => " + rawData);
        MainActivity.writeToFile(process +"  => "+ cmd + " Data => " + rawData);
        return rawData;
    }

    private Integer parseData(String rawData) throws Exception {
        byte b = 0;
        int value = 0;

        String[] dataList = rawData.split(" ");

        for (String dataPart: dataList)
            System.out.println(dataPart);


        for (String dataPart: dataList) {
            System.out.println("xx" +value);
            value *= 256 ;
            value += Integer.decode("0x" + dataPart).intValue();  //.intValue()
        }

        Log.i("com.example.app", "DATA: " + value);
        System.out.println("DATA: " + value);
        return value;
    }




    private String readRPMData(String cmd) throws Exception {
        sendCmd(cmd);
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

    private String readSpeedData(String cmd) throws Exception {
        sendCmd(cmd);
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

    private String readVoltData(String cmd) throws Exception {
        sendCmd(cmd);
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

    private String readFuelData(String cmd) throws Exception {
        sendCmd(cmd);
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
            throw new IOException("read Fuel Data   =>    No data");
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

    private String readCoolantTempData(String cmd) throws Exception {
        sendCmd(cmd);
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