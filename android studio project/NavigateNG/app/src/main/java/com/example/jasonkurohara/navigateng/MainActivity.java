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
import android.graphics.PointF;
import android.media.Image;
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
import java.lang.reflect.Array;
import java.security.cert.Certificate;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

public class MainActivity extends AppCompatActivity  {

/*********************************************CONSTANTS********************************************/
    private static final String TAG = "MainActivity";
    public static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    public static final String EXTRA_START_NODE = "com.example.jasonkurohara.navigateng.START_NODE";
    public static final String EXTRA_END_NODE = "com.example.jasonkurohara.navigateng.END_NODE";



/*********************************************OBJECTS**********************************************/
    //Stores data from text files
    public static Graph graph = new Graph();
    Vertex startnode = new Vertex();
    Vertex endnode = new Vertex();
    public static ArrayList<String> printedDirections = new ArrayList<String>();

    ArrayList<Vertex> fullpath = new ArrayList<Vertex>();

    //BT modules
    public BluetoothAdapter mBluetoothAdapter; //Allows discovery

    //Path drawing
    private LineView nLineView;
    private DrawPath ndrawPath;

    //UI
    public Button btnGO;
    public Button btnStepbyStep;

    //INitialize button to go to step by step instrucitons
    public void initStepbyStep(){
        btnStepbyStep = (Button) findViewById(R.id.btnStepbyStep);
        btnStepbyStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                graph = textParser();
                fullpath.clear();
                Vertex curr = astar(graph, startnode, endnode);

                fullpath.add(0,curr);
                while(curr.getPrevious() != null){
                    fullpath.add(0,curr.getPrevious());
                    curr = curr.getPrevious();
                }
                Log.d(TAG,"PRINT PATH");
                for(Vertex current: fullpath){
                    Log.d(TAG, current.getName());
                }

                Directions pathDirections = new Directions();
                pathDirections.createDirections(fullpath);
                ArrayList<String> printedDirections = new ArrayList<String>();
                printedDirections = pathDirections.retrieveDirections();
                for(String step : printedDirections){
                    Log.d(TAG,step);
                }
                ndrawPath.setPointPath(fullpath);
                ndrawPath.draw();
                openStepbyStep();
            }
        });
    }

    public void openStepbyStep(){

        Intent intent = new Intent(this,StepbyStep.class);
      //  intent.putExtra("",printedDirections);

        intent.putExtra(EXTRA_START_NODE,startnode.getName());
        intent.putExtra(EXTRA_END_NODE,endnode.getName());
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            System.out.println(getEndnode().getName());
        }
    }
    //Initialize button to navigate to separate window
    public void initLocalize(){
        btnGO = (Button) findViewById(R.id.btnGO);

        ndrawPath = (DrawPath) findViewById(R.id.drawpath);//Initialize drawPath
        ndrawPath.bringToFront(); //Draw over background

        btnGO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent localizeWindow = new Intent(MainActivity.this,LocalizeActivity.class);
                startActivity(localizeWindow);
            }
        });
    }
/*********************************************ON CREATE*****************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView img = (ImageView)findViewById(R.id.imageView);
        img.setImageResource(R.drawable.grid);

        initLocalize(); //Initialize GO button
        initStepbyStep();

        graph = textParser(); //Parse text to make nodes
        startnode = graph.getAllVertex().get(0);
        endnode = graph.getAllVertex().get(0);
        //Initialize bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Initializing buttons/drop down menus
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
        InputStream is = this.getResources().openRawResource(R.raw.vertexes);
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
/********************************************SPINNER************************************************/
        final Spinner pointA = (Spinner) findViewById(R.id.pointA);
        final ArrayAdapter<String> adapterA = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, str);

        adapterA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointA.setAdapter(adapterA);

        pointA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String startpoint = pointA.getSelectedItem().toString();
       //         graph = textParser();
                initliazeGraph(graph);
                startnode = graph.findVertex(startpoint);
                Log.d(TAG,"START");
                Log.d(TAG, startnode.getName());
                nLineView = (LineView) findViewById(R.id.lineView);
                nLineView.bringToFront();
                nLineView.setStartX(startnode.getX()*180+187);
                nLineView.setStartY(startnode.getY()*180+550);
                nLineView.draw();

                Vertex curr = astar(graph, startnode, endnode);
                ArrayList<Vertex> fullpath = new ArrayList<Vertex>();
                fullpath.add(0,curr);
                while(curr.getPrevious() != null){
                    fullpath.add(0,curr.getPrevious());
                    curr = curr.getPrevious();
                }
                Log.d(TAG,"PRINT PATH");
                for(Vertex current: fullpath){
                    Log.d(TAG, current.getName());
                }
                Directions pathDirections = new Directions();
                pathDirections.createDirections(fullpath);
                //ArrayList<String> printedDirections = new ArrayList<String>();
                printedDirections = pathDirections.retrieveDirections();
                for(String step : printedDirections){
                    Log.d(TAG,step);
                }
                ndrawPath.setPointPath(fullpath);
                ndrawPath.draw();
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

                Log.d(TAG,"end");
             //   graph = textParser();
                initliazeGraph(graph);
                endnode = graph.findVertex(endpoint );
                Log.d(TAG,endnode.getName());
                nLineView = (LineView) findViewById(R.id.lineView);
                nLineView.bringToFront();
                nLineView.setEndX(endnode.getX()*180+187);
                nLineView.setEndY(endnode.getY()*180+550);
                nLineView.draw();

                Log.d(TAG,"START: " + startnode.getName());
                Log.d(TAG,"END: " + endnode.getName());
                Vertex curr = astar(graph, startnode, endnode);
                ArrayList<Vertex> fullpath = new ArrayList<Vertex>();
                fullpath.add(0,curr);
                while(curr.getPrevious() != null){
                    fullpath.add(0,curr.getPrevious());
                    curr = curr.getPrevious();
                }
                Log.d(TAG,"PRINT PATH");
                for(Vertex current: fullpath){
                    Log.d(TAG, current.getName());
                }

                Directions pathDirections = new Directions();
                pathDirections.createDirections(fullpath);
                ArrayList<String> printedDirections = new ArrayList<String>();
                printedDirections = pathDirections.retrieveDirections();
                for(String step : printedDirections){
                    Log.d(TAG,step);
                }
                ndrawPath.setPointPath(fullpath);
                ndrawPath.draw();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

/********************************************ALGORITHM*********************************************/
    public  Vertex astar ( Graph graph, Vertex start, Vertex end){
        //Initialization
        PriorityComparator pq = new PriorityComparator();
        PriorityQueue<Vertex> pqueue = new PriorityQueue<Vertex>(25,pq);
        HashMap<Vertex,Double> priorities = new HashMap<Vertex,Double>();
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        path.add(start);

        priorities.put(start,0.0); //Candidate distance is 0

        start.setCandidateDistance(distanceFunction(start,end));
        pqueue.add(start);
        priorities.put(start,0.0);

        Set<Vertex> confirmed = new HashSet<Vertex>(); //green
        ArrayList<Vertex> unexplored = new ArrayList<>(); //uncolored;
        for(Vertex vert: graph.getAllVertex()){
            unexplored.add(vert);
        }
        unexplored.remove(start);

        Set<Vertex> potential = new HashSet<Vertex>(); //yellow

        while( !pqueue.isEmpty() ) {
            Vertex point = new Vertex();
            point = pqueue.poll(); //dequeue

            confirmed.add(point);

            if (point.equals(end)) { //BASE CASE
                return end;
            }

            double candidateDistance = priorities.get(point);

            for (Vertex neighbor : point.getNeighbors() ) {
                double heuristic = distanceFunction(neighbor, end);

                if (unexplored.contains(neighbor)) {
                    unexplored.remove(neighbor);
                    potential.add(neighbor);

                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));

                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
                    pqueue.add(neighbor); //simply add
                    neighbor.setPrevious(point);
                } else if (potential.contains(neighbor) && priorities.get(neighbor) > candidateDistance + distanceFunction(point, neighbor)) {
                    priorities.remove(neighbor);
                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));

                    neighbor.setPrevious(point);
                    pqueue.remove(neighbor);
                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
                    pqueue.add(neighbor); //This is change of prioritiy, so may delete and re add neighbor
                }
            }
        }
        return end;
    }

    static public class PriorityComparator implements Comparator<Vertex>{
        @Override
        public int compare(Vertex start, Vertex end){
            return (int)(start.getPriority() - end.getPriority());
        }
    }

    public static double distanceFunction(Vertex start, Vertex end){
        return Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
    }

/*******************************************TEXT PARSING*******************************************/
    public Graph textParser() {
        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.vertandedges);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String mapName = "";
        Graph graph = new Graph();
        int state = 0;

        if (is != null) {
            try {
                while ((data = reader.readLine()) != null) {
                    if (data.equals("TITLE")) {
                        continue;
                    } else if (state == 0 && !data.equals("TITLE")) {
                        mapName = data;
                        state++;
                    } else if (state == 1) {
                        if (data.equals("VERTEX")) {
                            continue;
                        }

                        if (data.equals("EDGES")) {
                            state++;
                            continue;
                        }

                        if (!data.equals("EDGES")) {
                            int delimiter = 0;
                            Vertex newVert = new Vertex();
                            if ((delimiter = data.indexOf(";")) != -1) {
                                newVert.setName(data.substring(0, delimiter));
                            }
                            data = data.substring(delimiter + 1);
                            if ((delimiter = data.indexOf(",")) != -1) {
                                newVert.setX(Integer.parseInt(data.substring(0, delimiter)));
                            }

                            data = data.substring(delimiter + 1);
                            newVert.setY(Integer.parseInt(data));
                            graph.addVertex(newVert);
                        }
                    } else if (state == 2) {
                        int delimiter = 0;

                        if ((delimiter = data.indexOf(";")) != -1) {
                            //Finds corresponding vertex in allVertex based on name
                            String vert1name = data.substring(0, delimiter);
                            System.out.println(vert1name);
                            data = data.substring(delimiter + 1);
                            String vert2name = data;
                            System.out.println(vert2name);

                            Vertex start = new Vertex();
                            start = graph.findVertex(vert1name);
                            System.out.println(start.getName());
                            Vertex end = new Vertex();
                            end = graph.findVertex(vert2name);

                            if (start != null && end != null) {
                                start.addNeighbor(end);
                                end.addNeighbor(start);
                                double distance = Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
                                Edge newEdge = new Edge(start,end,distance);
                                graph.addEdge(newEdge);
                            }
                        }
                    }
                    sbuffer.append(data + "n");
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return graph;
    }

/**********************************************BLUETOOTH MODULES***************************************/
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

    public static Graph getGraph(){
        return graph;
    }

    public void initliazeGraph(Graph graph){
        for(Vertex vert: graph.getAllVertex()){
            vert.setPrevious(null);
            vert.setCandidateDistance(0.0);
        }
    }

    public Vertex getStartnode(){
        return startnode;
    }

    public Vertex getEndnode(){
        return endnode;
    }

    public static ArrayList<String> getPrintedDirections() {
        return printedDirections;
    }
}


