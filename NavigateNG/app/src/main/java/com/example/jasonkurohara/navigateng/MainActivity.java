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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    //Button to navigate to seperate window
    public Button navigate;
    public Button localize;

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
        // initNav(); //Initialize navigation window
        initLocalize();
    //    textParser();
      //  Log.d(TAG, Double.toString(graph.getAllEdges().get(1).getCost()));
    }

    public ArrayList<Vertex> astar ( Graph graph, Vertex start, Vertex end){
        Comparator<Vertex> comparator = new PriorityComparator();
        PriorityQueue<Vertex> pqueue = new PriorityQueue<Vertex>(10,comparator);
        HashMap<Vertex,Double> priorities = new HashMap<Vertex,Double>();
        ArrayList<Vertex> path = new ArrayList<Vertex>();

        priorities.put(start,0.0); //Candidate distance is 0

        start.setCandidateDistance(distanceFunction(start,end));
        pqueue.add(start);
        priorities.put(start,0.0);

        Set<Vertex> confirmed = new HashSet<Vertex>(); //green
        Set<Vertex> unexplored = new HashSet<Vertex>(); //uncolored;
        Set<Vertex> potential = new HashSet<Vertex>(); //yellow

        while( !pqueue.isEmpty() ) {
            Vertex point = new Vertex();
            point = pqueue.peek(); //Once dequeued, set to confirmed and this is the shortest path (candidate distance cannot be changed)
            pqueue.remove(point);
            confirmed.add(point);

            if (point.equals(end)) {
                return path;
            }

            double candidateDistance = priorities.get(point);

            for (Vertex neighbor : graph.getNeighbors(point)) {

                double heuristic = distanceFunction(neighbor, end);

                if (unexplored.contains(neighbor)) {
                    unexplored.remove(neighbor);
                    potential.add(neighbor);

                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));

                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
                    pqueue.add(neighbor); //simply add
                } else if (potential.contains(neighbor) && priorities.get(neighbor) > candidateDistance + distanceFunction(point, neighbor)) {
                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));

                    pqueue.remove(neighbor);
                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
                    pqueue.add(neighbor); //This is change of prioritiy, so may delete and re add neighbor

                }
            }
        }
        return path;
    }


    public class PriorityComparator implements Comparator<Vertex>{
        @Override
        public int compare(Vertex start, Vertex end){
            return (int)(start.getPriority() - end.getPriority());
        }
    }

    public double distanceFunction(Vertex start, Vertex end){
        return Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));    )
    }

}


