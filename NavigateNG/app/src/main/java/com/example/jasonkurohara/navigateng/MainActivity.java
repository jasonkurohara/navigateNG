package com.example.jasonkurohara.navigateng;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //Button to navigate to seperate window
    public Button navigate;
    public Button localize;
    public void initNav(){
        navigate = (Button) findViewById(R.id.navigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent navigateWindow = new Intent(MainActivity.this,NavigateActivity.class);
                startActivity(navigateWindow);
            }
        });
    }

    public void initLocalize(){
        localize = (Button) findViewById(R.id.localize);
        localize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localizeWindow = new Intent(MainActivity.this,LocalizeActivity.class);
                startActivity(localizeWindow);
            }
        });

    }

    private static final String TAG = "MainActivity";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNav(); //Initialize navigation window
        initLocalize();
    }

    public void textParser(){
        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.sample);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String mapName = "";
        Graph graph = new Graph();
        int state = 0;

        if( is != null){
            try{
                while((data=reader.readLine()) != null){
                    if(  state == 0 ){
                        if( data.equals("TITLE")) {
                            continue;
                        }
                        mapName = data;
                        state++;
                    }

                    if(   state == 1){
                        if( data.equals("VERTEX")) {
                            continue;
                        }

                        if( data.equals("EDGES")) {
                            state++;
                            continue;
                        }
                        //String of vertex name and position
                        int delimiter = data.indexOf(",");
                        Vertex newVert = new Vertex();
                        newVert.setName(data.substring(0,delimiter));

                        data = data.substring(delimiter+1);
                        delimiter = data.indexOf(";");
                        newVert.setX( Integer.parseInt(data.substring(0,delimiter)) );

                        data = data.substring(delimiter+1);
                        delimiter = data.indexOf(";");
                        newVert.setY( Integer.parseInt(data.substring(0,delimiter)) );

                        graph.addVertex(newVert);
                    }

                    else if (  state == 2){
                        int delimiter = data.indexOf(";");
                        Edge newEdge = new Edge();

                        //Finds corresponding vertex in allVertex based on name
                        String vert1name = data.substring(0,delimiter);
                        data = data.substring(delimiter+1);
                        delimiter = data.indexOf(";");
                        String vert2name = data.substring(0, delimiter);

                        Vertex start = graph.findVertex(vert1name);
                        Vertex end = graph.findVertex(vert2name);
                        newEdge.setEnd(end);
                        newEdge.setStart(start);

                        double distance = Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
                        newEdge.setCost(distance);
                        graph.addEdge(newEdge);
                    }
                    sbuffer.append(data + "n");
                }
                is.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}


