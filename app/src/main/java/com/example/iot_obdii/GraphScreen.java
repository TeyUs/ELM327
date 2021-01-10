package com.example.iot_obdii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public class GraphScreen extends AppCompatActivity {
    LineChart graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_screen);

        Intent intent = getIntent();
        graph = findViewById(R.id.graph);

        if(intent.getStringExtra("type").matches("speed") || intent.getStringExtra("type").matches("rpm")){
            drawGraph(intent);
        }
        else{
            drawGraphRare(intent);
        }
    }

    public void drawGraph(Intent intent){

        ArrayList<Entry> dataList = new ArrayList<>();
        try {
            SQLiteDatabase database = openOrCreateDatabase("Data", MODE_PRIVATE, null);

            Cursor cursor = database.rawQuery("SELECT * FROM data", null);
            int dataix = cursor.getColumnIndex(intent.getStringExtra("type"));
            int index = 0;
            //cursor.moveToFirst();
            while (cursor.moveToNext()) {
                dataList.add(new Entry(index, cursor.getInt(dataix)));
                index++;
            }
            cursor.close();
            LineDataSet lineDataSet = new LineDataSet(dataList, intent.getStringExtra("type"));

            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setLineWidth(3);
            lineDataSet.setDrawCircles(false);

            ArrayList<ILineDataSet> dataSet = new ArrayList<>();
            dataSet.add(lineDataSet);

            LineData lineData = new LineData(dataSet);
            graph.setData(lineData);
            graph.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drawGraphRare(Intent intent){

        ArrayList<Entry> dataList = new ArrayList<>();
        try {
            SQLiteDatabase database = openOrCreateDatabase("Data", MODE_PRIVATE, null);

            Cursor cursor = database.rawQuery("SELECT * FROM rare", null);
            int dataix = cursor.getColumnIndex(intent.getStringExtra("type"));
            int index = 0;
            //cursor.moveToFirst();
            while (cursor.moveToNext()) {
                dataList.add(new Entry(index, cursor.getInt(dataix)));
                index++;
            }
            cursor.close();
            LineDataSet lineDataSet = new LineDataSet(dataList, intent.getStringExtra("type"));

            lineDataSet.setColor(Color.BLUE);
            lineDataSet.setLineWidth(3);
            lineDataSet.setDrawCircles(false);

            ArrayList<ILineDataSet> dataSet = new ArrayList<>();
            dataSet.add(lineDataSet);

            LineData lineData = new LineData(dataSet);
            graph.setData(lineData);
            graph.invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.hiz) {
            Intent intent = new Intent(GraphScreen.this,GraphScreen.class);
            intent.putExtra("type", "speed");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (item.getItemId() == R.id.rpm){
            Intent intent = new Intent(GraphScreen.this,GraphScreen.class);
            intent.putExtra("type", "rpm");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (item.getItemId() == R.id.volt){
            Intent intent = new Intent(GraphScreen.this,GraphScreen.class);
            intent.putExtra("type", "volt");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (item.getItemId() == R.id.fuel_l_km){
            Intent intent = new Intent(GraphScreen.this,GraphScreen.class);
            intent.putExtra("type", "fuel_l_km");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (item.getItemId() == R.id.fuel_km_h){
            Intent intent = new Intent(GraphScreen.this,GraphScreen.class);
            intent.putExtra("type", "fuel_km_h");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (item.getItemId() == R.id.totalKM){
            Intent intent = new Intent(GraphScreen.this,GraphScreen.class);
            intent.putExtra("type", "totalKM");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else if (item.getItemId() == R.id.coolant){
            Intent intent = new Intent(GraphScreen.this,GraphScreen.class);
            intent.putExtra("type", "coolant");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
