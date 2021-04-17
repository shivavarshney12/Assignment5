package com.example.assignment5;
//I took reference from https://stackoverflow.com/questions/53337839/find-the-most-repeated-word-in-a-string

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;

    BarChart barChart;
    private List<ScanResult> result;
    private int i=0;
    TextView textView1;
    EditText editText1;
    boolean flag1=false,flag2=false,flag3=false;
    BroadcastReceiver wifiScanReceiver;
    Locations ls;
    float ap1=0,ap2=0,ap3=0;
    String s1="Hi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView)findViewById(R.id.textView1);
        editText1 = (EditText)findViewById(R.id.editText1);
        ls= Locations.getInstance(this);
        wifiManager = (WifiManager)
                getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "Wifi is disabled.",Toast.LENGTH_SHORT).show();
            //wifiManager.setWifiEnabled(true);
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 1);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ls.trainingDataDao().delete();

                                                        }
                                                    }

        );

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
                            s1 += " "+res.level;
                        }
                        if(res.SSID.equals("maggi")){
                            ap2=res.level;
                            flag2=true;
                            s1 += " "+res.level;
                        }
                        if(res.SSID.equals("redmi")){
                            ap3=res.level;
                            flag3=true;
                            s1 += " "+res.level;
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
    protected void onResume() {
        super.onResume();


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiScanReceiver);
    }


    public void button1(View v){
        wifiManager.startScan();


    }
    public void button2(View v){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            List<TrainingData> td1= ls.trainingDataDao().getAll();
                                                            int i=0;
                                                            double min=10000;double d=0;
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
}