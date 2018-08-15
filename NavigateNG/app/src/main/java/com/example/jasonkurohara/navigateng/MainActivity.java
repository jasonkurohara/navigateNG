package com.example.jasonkurohara.navigateng;


import android.Manifest;
import android.app.Activity;
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
import android.nfc.Tag;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

public class MainActivity extends AppCompatActivity  {

    Graph graph = new Graph();
    Vertex startnode = new Vertex();
    Vertex endnode = new Vertex();
    private static final String TAG = "MainActivity";
   // ArrayList<Vertex> destination = new ArrayList<Vertex>();


    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    public BluetoothAdapter mBluetoothAdapter; //Allows discovery, instantiate a BluetoothDevice

    //Initialize button to navigate to separate window
    public Button btnGO;

    public void initLocalize(){
        btnGO = (Button) findViewById(R.id.btnGO);

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Vertex> path = new ArrayList<Vertex>();
        //        path = astar(graph,startnode,endnode);
                Intent localizeWindow = new Intent(MainActivity.this,LocalizeActivity.class);
                startActivity(localizeWindow);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLocalize(); //Initialize GO button
        textParser(); //Parse text to make nodes

        //Initialize bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Initiialzing buttons/drop down menus
        Button btnONOFF = (Button) findViewById(R.id.btnONOFF);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.sample2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        Vector<String> str = new Vector<String>();

        if( is != null){
            try{
                while( (data=reader.readLine()) != null){
                    str.add(data);
                }
                is.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }

        final Spinner pointA = (Spinner) findViewById(R.id.pointA);
        final ArrayAdapter<String> adapterA = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, str);

        adapterA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointA.setAdapter(adapterA);

        pointA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String startpoint = pointA.getSelectedItem().toString();
             //   Vertex start = new Vertex();
                startnode = graph.findVertex(startpoint);
                Log.d(TAG, startpoint);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final Spinner pointB = (Spinner) findViewById(R.id.pointB);
        ArrayAdapter<String> adapterB = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,str);
        adapterB.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointB.setAdapter(adapterB);
        pointB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String endpoint = pointB.getSelectedItem().toString();
                Log.d(TAG,endpoint);
            //    Vertex end = new Vertex();
                endnode = graph.findVertex(endpoint );
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device, multiple states of BT on device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
    }



    public void textParser(){
        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.sample);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String mapName = "";

        int state = 0;

        if( is != null){
            try{
                while( (data=reader.readLine()) != null){
                    if( data.equals("TITLE") ){
                        continue;
                    }
                    else if( state == 0 && !data.equals("TITLE") ){
                        mapName = data;
                        state++;
                    }
                    else if(   state == 1 && data.equals("VERTEX") ){
                        continue;
                    }
                    else if( state == 1 && !data.equals("VERTEX") ) {
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
                    else if( state == 2 && data.equals("EDGES")) {
                        continue;
                    }
                    else if( state == 2 && !data.equals("EDGES")) {
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

//    public ArrayList<Vertex> astar ( Graph graph, Vertex start, Vertex end){
//        Comparator<Vertex> comparator = new PriorityComparator();
//        PriorityQueue<Vertex> pqueue = new PriorityQueue<Vertex>(10,comparator);
//        HashMap<Vertex,Double> priorities = new HashMap<Vertex,Double>();
//        ArrayList<Vertex> path = new ArrayList<Vertex>();
//
//        priorities.put(start,0.0); //Candidate distance is 0
//
//        start.setCandidateDistance(distanceFunction(start,end));
//        pqueue.add(start);
//        priorities.put(start,0.0);
//
//        Set<Vertex> confirmed = new HashSet<Vertex>(); //green
//        Set<Vertex> unexplored = new HashSet<Vertex>(); //uncolored;
//        Set<Vertex> potential = new HashSet<Vertex>(); //yellow
//
//        while( !pqueue.isEmpty() ) {
//            Vertex point = new Vertex();
//            point = pqueue.peek(); //Once dequeued, set to confirmed and this is the shortest path (candidate distance cannot be changed)
//            pqueue.remove(point);
//            confirmed.add(point);
//
//            if (point.equals(end)) {
//                return path;
//            }
//
//            double candidateDistance = priorities.get(point);
//
//            for (Vertex neighbor : graph.getNeighbors(point)) {
//
//                double heuristic = distanceFunction(neighbor, end);
//
//                if (unexplored.contains(neighbor)) {
//                    unexplored.remove(neighbor);
//                    potential.add(neighbor);
//
//                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));
//
//                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
//                    pqueue.add(neighbor); //simply add
//                } else if (potential.contains(neighbor) && priorities.get(neighbor) > candidateDistance + distanceFunction(point, neighbor)) {
//                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));
//
//                    pqueue.remove(neighbor);
//                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
//                    pqueue.add(neighbor); //This is change of prioritiy, so may delete and re add neighbor
//
//                }
//            }
//        }
//        return path;
//    }
//
//    public class PriorityComparator implements Comparator<Vertex>{
//        @Override
//        public int compare(Vertex start, Vertex end){
//            return (int)(start.getPriority() - end.getPriority());
//        }
//    }
//
//    public double distanceFunction(Vertex start, Vertex end){
//        return Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
//    }
}


