package com.example.assignment5;
//I took reference from https://stackoverflow.com/questions/53337839/find-the-most-repeated-word-in-a-string

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.net.wifi.WifiManager;
import android.net.wifi.ScanResult;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private WifiManager wifiManager;

    BarChart barChart;
    private List<ScanResult> result;
    private int i=0;
    TextView textView1,textView2;
    EditText editText1;
    boolean flag1=false,flag2=false,flag3=false;
    BroadcastReceiver wifiScanReceiver;
    Locations ls;
    float ap1=0,ap2=0,ap3=0;
    float ax=0,ay=0,az=0,mx=0,my=0,mz=0,gx=0,gy=0,gz=0;
    String s1="Hi";
    private SensorManager mSensorManager;
    private Sensor mAcc,magnetic_field,gyroscope;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        editText1 = (EditText)findViewById(R.id.editText1);
        ls= Locations.getInstance(this);
        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);

        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetic_field = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if(!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Wifi is disabled.",Toast.LENGTH_SHORT).show();
            //wifiManager.setWifiEnabled(true);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 1);
        /*AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ls.trainingDataDao().delete();

                                                        }
                                                    }

        );*/

        //BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {

                result = wifiManager.getScanResults();
                //unregisterReceiver(this);

                if(result== null){
                    Toast.makeText(MainActivity.this,"No Wifi is found.",Toast.LENGTH_LONG).show();


                }

                else {
                    //Toast.makeText(this, "Total wifi:", Toast.LENGTH_LONG).show();
                    i=0;
                    flag1=false;
                    flag2=false;
                    flag3=false;
                    String loc= editText1.getText().toString();
                    String wifiNames="";
                    for(ScanResult res:result) {
                        wifiNames += res.SSID + " ";
                    }

                    Toast.makeText(MainActivity.this,"Total wifi:"+ result.size()+" & "+wifiNames+"& "+loc,Toast.LENGTH_LONG).show();

                    BarChart barChart = (BarChart) findViewById(R.id.barchart);

                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<String> labels = new ArrayList<String>();
                    for(ScanResult res:result) {
                        entries.add(new BarEntry(res.level, i++));
                        labels.add(res.SSID);
                        if(res.SSID.equals("note8")) {
                            ap1 = res.level;
                            flag1=true;
                            //s1 += " "+res.level;
                        }
                        if(res.SSID.equals("maggi")){
                            ap2=res.level;
                            flag2=true;
                            //s1 += " "+res.level;
                        }
                        if(res.SSID.equals("redmi")){
                            ap3=res.level;
                            flag3=true;
                            //s1 += " "+res.level;
                        }
                    }

                    //Toast.makeText(MainActivity.this,s1,Toast.LENGTH_SHORT).show();
                    //if(flag1 && flag2 && flag3) {
                    if(true) {
                        final TrainingData td=new TrainingData(ap1,ap2,ap3,loc);
                        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            ls.trainingDataDao().insertAll(td);


                                                                        }
                                                                    }

                        );

                    }

                    //Toast.makeText(MainActivity.this,"Total wifi:"+ result.size(),Toast.LENGTH_LONG).show();
                    BarDataSet bardataset = new BarDataSet(entries, "Cells");

                    BarData data = new BarData(labels, bardataset);
                    barChart.setData(data); // set the data and list of labels into chart

                    barChart.setDescription(" Bar Chart Description Here");  // set the description
                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                    //barChart.animateY(5000);\
                    barChart.animate();
                    barChart.invalidate();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //registerReceiver(wifiScanReceiver, intentFilter);
        registerReceiver(wifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        boolean success = wifiManager.startScan();


    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy){
        //sensor accuracy changes
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public final void onSensorChanged(SensorEvent event){

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mx = event.values[0];
            my = event.values[1];
            mz = event.values[2];
        }
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx = event.values[0];
            gy = event.values[1];
            gz = event.values[2];
        }
        String loc= editText1.getText().toString();
        final ImuTrainingData itd=new ImuTrainingData(ax,ay,az,mx,my,mz,gx,gy,gz,loc);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ls.imuTrainingDataDao().insertAll(itd);
                                                            //Toast.makeText(MainActivity.this,"Sensor data inserted",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAcc != null) {
            mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }
        if (magnetic_field != null) {
            mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }
        if (gyroscope != null) {
            mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Error: No Accelerometer.", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiScanReceiver);
    }

    //Scan wifi
    public void button1(View v){
        wifiManager.startScan();

    }
    //test location
    public void button2(View v){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            List<TrainingData> td1= ls.trainingDataDao().getAll();
                                                            int i=0;
                                                            double min=Double.POSITIVE_INFINITY;
                                                            double d=0;
                                                            String location="null";
                                                            Map<Double,String> map=new HashMap<Double,String>();
                                                            for( ;i<td1.size();i++) {
                                                                d=Math.sqrt(Math.pow(ap1-td1.get(i).ap1,2)+Math.pow(ap2-td1.get(i).ap2,2)+Math.pow(ap3-td1.get(i).ap3,2));
                                                                map.put(d,td1.get(i).loc);
                                                                if(min>d) {
                                                                    min=d;
                                                                    location=td1.get(i).loc;
                                                                }
                                                            }
                                                            TreeMap<Double,String> sortedKey=new TreeMap<>();
                                                            sortedKey.putAll(map);
                                                            int k=3;
                                                            i=1;
                                                            String s="";
                                                            for(Map.Entry<Double,String> entry : sortedKey.entrySet()) {
                                                                if(i>k)
                                                                    break;
                                                                s += " "+entry.getValue();
                                                                i++;
                                                            }

                                                            //s="room1 room2 room1 room1";
                                                            String[] splited = s.split(" ");
                                                            Arrays.sort(splited);
                                                            System.out.println(Arrays.toString(splited));
                                                            int max = 0;
                                                            int count= 1;
                                                            String word = splited[0];
                                                            String curr = splited[0];
                                                            for( i = 1; i<splited.length; i++){
                                                                if(splited[i].equals(curr)){
                                                                    count++;
                                                                }
                                                                else{
                                                                    count =1;
                                                                    curr = splited[i];
                                                                }
                                                                if(max<count){
                                                                    max = count;
                                                                    word = splited[i];
                                                                }
                                                            }
                                                           //System.out.println(max + " x " + word);

                                                            final String loc1=location;
                                                            final String loc2=word;
                                                            runOnUiThread(new Runnable(){
                                                                @Override
                                                                public void run(){
                                                                    textView1.setText("You are in "+loc1+"\n or You are in "+loc2);
                                                                    textView1.setVisibility(View.VISIBLE);
                                                                }
                                                            });

                                                            //Toast.makeText(MainActivity.this, "data: "+list, Toast.LENGTH_LONG).show();
                                                        }
                                                    }

        );

    }
    //check database
    public void button3(View v){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            List<TrainingData> td1= ls.trainingDataDao().getAll();
                                                            int i=0;
                                                            String data="";
                                                            for( ;i<td1.size();i++) {
                                                                data += td1.get(i).id +" "+td1.get(i).ap1 +" "+td1.get(i).ap2 +" "+td1.get(i).ap3+" "+td1.get(i).loc + "\n";
                                                            }
                                                            final String datas=data;
                                                            runOnUiThread(new Runnable(){
                                                                @Override
                                                                public void run(){
                                                                    Toast.makeText(MainActivity.this,""+datas,Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                            //Toast.makeText(MainActivity.this, "data: "+list, Toast.LENGTH_LONG).show();
                                                        }
                                                    }

        );

    }
    //test IMU location
    public void button4(View v){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            List<ImuTrainingData> td1= ls.imuTrainingDataDao().getAll();
                                                            int i=0;
                                                            double min=Double.POSITIVE_INFINITY;
                                                            double d=0,d1=0,d2=0,d3=0;
                                                            String location="test";

                                                            for( ;i<td1.size();i++) {
                                                                d1=(Math.pow(ax-td1.get(i).ax,2)+Math.pow(ay-td1.get(i).ay,2)+Math.pow(az-td1.get(i).az,2));
                                                                d2=(Math.pow(mx-td1.get(i).mx,2)+Math.pow(my-td1.get(i).my,2)+Math.pow(mz-td1.get(i).mz,2));
                                                                d3=(Math.pow(gx-td1.get(i).gx,2)+Math.pow(gy-td1.get(i).gy,2)+Math.pow(gz-td1.get(i).gz,2));
                                                                d=Math.sqrt(d1+d2+d3);
                                                                if(min>d) {
                                                                    min=d;
                                                                    location=td1.get(i).loc;
                                                                }
                                                            }

                                                            final String loc1=location;
                                                            runOnUiThread(new Runnable(){
                                                                @Override
                                                                public void run(){
                                                                    textView2.setText("You are in "+loc1);
                                                                    textView2.setVisibility(View.VISIBLE);
                                                                }
                                                            });

                                                            //Toast.makeText(MainActivity.this, "data: "+list, Toast.LENGTH_LONG).show();
                                                        }
                                                    }

        );

    }
    //check IMU database
    public void button5(View v){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            List<ImuTrainingData> td1= ls.imuTrainingDataDao().getAll();
                                                            int i=0;
                                                            String data="";

                                                            for( ;i<td1.size();i++) {
                                                                data += td1.get(i).id +" "+td1.get(i).ax +" "+td1.get(i).ay +" "+td1.get(i).az+" ";
                                                                data += td1.get(i).mx +" "+td1.get(i).my +" "+td1.get(i).mz+" ";
                                                                data += td1.get(i).gx +" "+td1.get(i).gy +" "+td1.get(i).gz+" "+td1.get(i).loc + "\n";
                                                            }
                                                            final String datas=data;
                                                            runOnUiThread(new Runnable(){
                                                                @Override
                                                                public void run(){
                                                                    Toast.makeText(MainActivity.this,"Table size:"+td1.size(),Toast.LENGTH_SHORT).show();
                                                                    Toast.makeText(MainActivity.this,""+datas,Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                            //Toast.makeText(MainActivity.this, "data: "+list, Toast.LENGTH_LONG).show();
                                                        }
                                                    }

        );

    }
}