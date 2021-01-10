package com.example.iot_obdii;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView speedText, voltText, fuelStatusText, coolantTempText, dateTXT, rangeTXT, fuelRatetXT;
    private ProgressBar progressBarRPM;
    private ProgressBar progressBarSpeed;

    Integer curCoolant = 0, curSpeed = 0, curRPM = 0;
    Double curVolt = 0.0, curFuel_l_km = 0.0, curFuel_l_h = 0.0, curTotalKM = 0.0;

    Long timeNew = 0L;
    Double speed_Integral = 0.0;
    Double cur_z1_s = 0.0;
    Double int_z1_s = 0.0;
    Boolean isMove = false;
    Double fuelRate;
    Float engCap = 0.0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        startYtask();
    }


    public Double avarageKM(int speedInt) {
        Double speed = (double) speedInt;
        speed_Integral = (timeDiff() * (speed + cur_z1_s) + 2 * int_z1_s) / 2;
        cur_z1_s = speed;
        int_z1_s = speed_Integral;
        return speed_Integral / 475.0;
    }

    public double timeDiff(){
        Long tempMilliSec = timeNew;
        Calendar calendar = Calendar.getInstance();
        timeNew = calendar.getTimeInMillis();

        if(tempMilliSec == 0) return 0.0;

        return (timeNew - tempMilliSec)/1000.0;
    }

    public void setSpeed(String mspeed) {
        curSpeed = Integer.parseInt(mspeed);
        curTotalKM = avarageKM(curSpeed);
        String m = String.format("%.2f", curTotalKM);
        m = m + "KM";
        rangeTXT.setText(m);
        if (mspeed.matches("0")) {
            isMove = false;
        } else {
            isMove = true;
        }
        speedText.setText(mspeed);
        int speed = Integer.parseInt(mspeed);
        int kayma = 0;
        speed += kayma;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBarSpeed.setProgress(speed, true);
        } else {
            progressBarSpeed.setProgress(speed);
        }
        speedText.setText(mspeed);
    }

    public void setRPM(String rpm) {
        curRPM = Integer.parseInt(rpm);
        dataBase();
        int kayma = 0;
        int rpmInt = Integer.parseInt(rpm);
        rpmInt = (rpmInt * 3) / 100 + kayma;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBarRPM.setProgress(rpmInt, true);
        } else {
            progressBarRPM.setProgress(rpmInt);
        }
    }

    public void setVolt(String mvolt) {
        voltText.setText(mvolt);
        mvolt = mvolt.replace("V", "").trim();
        try {
            curVolt = Double.parseDouble(mvolt);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    public void setFuelStatus(String m) {
        double x = Double.parseDouble(m);
        String y = String.format("%.2f", x);
        y = y + "l/h";
        fuelStatusText.setText(y);

        if(curSpeed != 0){
            double ration = x / curSpeed * 100;
            String z = String.format("%.2f", ration);
            z = z + "l/100km";
            fuelRatetXT.setText(z);
        }else {
            fuelRatetXT.setText(Character.toString('\u221E') );
        }
    }

    public void setcoolantTemp(String m) {
        String n = m + " C";
        coolantTempText.setText(n);
        try {
            curCoolant = Integer.parseInt(m);
        } catch (Exception e) {
            Toast.makeText(this, "coolant " +e.toString(), Toast.LENGTH_SHORT).show();
        }

        dataBaseRare();
    }

    public void init(View view) {
        //Context context = this.getApplicationContext();
        Ytask task = new Ytask(this);
        task.execute();
    }

    public void dataBase() {
        try {
            SQLiteDatabase database = openOrCreateDatabase("Data", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS data (speed INTEGER, rpm INTEGER)");
            database.execSQL("INSERT INTO data (speed, rpm) VALUES (" + curSpeed + ", " + curRPM + ")");

            String s =  "  speed : " + curSpeed + "  RPM : " + curRPM;
            writeToFile(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dataBaseRare() {
        try {
            SQLiteDatabase database = openOrCreateDatabase("Data", MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS rare (volt FLOAT, fuel_l_km FLOAT, fuel_l_h FLOAT, totalKM FLOAT, coolant INTEGER )");
            database.execSQL("INSERT INTO rare (volt, fuel_l_km, fuel_l_h, totalKM, coolant) VALUES (" + curVolt + ", " + curFuel_l_km + ", " + curFuel_l_h + ", " + curTotalKM + ", " + curCoolant + ")");

            Calendar calendar = Calendar.getInstance();
            String time = "time : " + calendar.get(Calendar.HOUR_OF_DAY) +":"+ calendar.get(Calendar.MINUTE) +":"+ calendar.get(Calendar.SECOND);

            String s =  time + "  Volt : " + curVolt + "  Fuel l/km : " + curFuel_l_km + " Fuel km/h " + curFuel_l_h + " TotalKM " + curTotalKM  + "  Coolant " + curCoolant;
            writeToFile(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void graphIntent(View view) {
        Intent intent = new Intent(MainActivity.this, GraphScreen.class);
        intent.putExtra("type", "speed");
        startActivity(intent);
    }

    public void setDate() {
        Calendar calendar = Calendar.getInstance();
        String s = calendar.getTime().toString();
        String[] token = s.split(" ");
        String date = token[2] + " " + token[1] + " " + token[5];
        String[] tokenDate = token[3].split(":");
        String timeInDay = tokenDate[0] + ":" + tokenDate[1];
        System.out.println(timeInDay);
        System.out.println(date);
        this.dateTXT.setText(date);
    }

    public void writeToFile(String line) {
        File dosyaYolu = Environment.getExternalStorageDirectory();
        File dataFile = new File(dosyaYolu, "Data_File_OBD_ELM327.txt");
        try {
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            FileWriter fw = new FileWriter(dataFile,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter printWriter = new PrintWriter(bw);
            printWriter.println(line);
            printWriter.flush();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initLayout(){
        progressBarRPM = findViewById(R.id.progressBarRPM);
        progressBarSpeed = findViewById(R.id.progressBarSpeed);
        speedText = findViewById(R.id.textView_speed);
        voltText = findViewById(R.id.textVolt);
        fuelStatusText = findViewById(R.id.burn_rateTXT);
        coolantTempText = findViewById(R.id.engine_temperature);
        dateTXT = findViewById(R.id.dateText);
        rangeTXT = findViewById(R.id.range);
        fuelRatetXT = findViewById(R.id.burn_rate);
        setDate();
        SharedPreferences sharedPreferences = getSharedPreferences("EngineCapacity", MODE_PRIVATE);
        engCap = sharedPreferences.getFloat("engCap",0.0f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }


    private void startYtask() {
        Ytask task = null;
        try {
            task = new Ytask(this);
            task.execute();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            System.out.println(e.toString());
            task.cancel(true);
            Intent intent = new Intent(MainActivity.this, StartScreen.class);
            intent.putExtra("problem", false);
            intent.putExtra("problemType", e.toString());
            startActivity(intent);
        }
    }
}