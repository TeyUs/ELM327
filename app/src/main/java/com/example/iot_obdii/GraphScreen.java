package com.example.iot_obdii;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class GraphScreen extends AppCompatActivity {
    LineChart graphSpeed, graphRpm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_screen);
        graphSpeed = findViewById(R.id.graphSpeed);
        graphRpm = findViewById(R.id.gaugeRPM);

        ArrayList<Entry> speedList = new ArrayList<>();
        ArrayList<Entry> rpmList = new ArrayList<>();
        try {
            SQLiteDatabase database = openOrCreateDatabase("Data", MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM data",null);
            int speedix = cursor.getColumnIndex("speed");
            int rpmix = cursor.getColumnIndex("rpm");
            int index = 0;
            cursor.moveToFirst();
            while (cursor.moveToNext()){
                speedList.add(new Entry(index,cursor.getInt(speedix)));
                rpmList.add(new Entry(index,cursor.getInt(rpmix)));
                index++;
            }
            cursor.close();
            LineDataSet lineDataSetSpeed = new LineDataSet(speedList, "Speed");
            LineDataSet lineDataSetRpm = new LineDataSet(rpmList,"RPM");

            lineDataSetRpm.setColor(Color.RED);
            lineDataSetSpeed.setColor(Color.BLUE);
            lineDataSetRpm.setLineWidth(3);
            lineDataSetSpeed.setLineWidth(3);
            lineDataSetRpm.setCircleColor(Color.RED);
            lineDataSetSpeed.setCircleColor(Color.BLUE);


            ArrayList<ILineDataSet> dataSetSpeed = new ArrayList<>();
            dataSetSpeed.add(lineDataSetSpeed);

            LineData lineDataSpeed = new LineData(dataSetSpeed);
            graphSpeed.setData(lineDataSpeed);
            graphSpeed.invalidate();


            ArrayList<ILineDataSet> dataSetRpm = new ArrayList<>();
            dataSetRpm.add(lineDataSetRpm);

            LineData lineDataRPM = new LineData(dataSetSpeed);
            graphRpm.setData(lineDataRPM);
            graphRpm.invalidate();


        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
