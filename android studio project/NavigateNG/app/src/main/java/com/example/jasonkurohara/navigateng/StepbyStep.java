package com.example.jasonkurohara.navigateng;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class StepbyStep extends AppCompatActivity {

    public Button btnNEXT;
    public static Graph graph = new Graph();
    Vertex startnode = new Vertex();
    Vertex endnode = new Vertex();
    public static ArrayList<String> printedDirections = new ArrayList<String>();
    ArrayList<Vertex> fullpath = new ArrayList<Vertex>();
   // ArrayList<String> printedDirections = new ArrayList<String>();
    String TAG = "STEPBYSTEP ACTIVITY";

    //Path drawing
    private LineView2 nLineView2;
    private DrawPath2 ndrawPath2;
    public int i = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepby_step);
        btnNEXT = (Button)findViewById(R.id.btnNEXT);
        nLineView2 = (LineView2) findViewById(R.id.lineViewnew);
        nLineView2.bringToFront();
        ndrawPath2 = (DrawPath2) findViewById(R.id.drawpathnew);//Initialize drawPath
        ndrawPath2.bringToFront(); //Draw over background
        for(int i = 0; i < printedDirections.size(); i++){

        }
        btnNEXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initliazeGraph(graph);
                if(printedDirections.isEmpty() || fullpath.isEmpty()||fullpath.get(0).equals(endnode)  ){
                    //TextView directionBox = (TextView)findViewById(R.id.directionBox);
//                    directionBox.setText("You have reached your destination!");
//                    ndrawPath2.setPointPath(fullpath);
//                    ndrawPath2.draw();
                    return;
                }
                //startnode = fullpath.get(1); //GET NEXT IN PATH
                fullpath.remove(0);
                printedDirections.remove(0);


            //    fullpath.clear();
//                i++;
//                startnode = fullpath.get(i); //Goes to next node in path
//                printedDirections.get(i);
         //       Vertex curr = astar(graph, startnode, endnode);

//                fullpath.add(0,curr);
//                while(curr.getPrevious() != null){
//                    fullpath.add(0,curr.getPrevious());
//                    curr = curr.getPrevious();
//                }
//                Log.d(TAG,"PRINT PATH");
//                for(Vertex current: fullpath){
//                    Log.d(TAG, current.getName());
//                }

//                ArrayList<Vertex>directionpath = new ArrayList<Vertex>();
//                for(int i = 0; i < fullpath.size(); i++){
//                    if((i == 0 )|| (i == (fullpath.size()-1))){
//                        continue;
//                    }
//                    directionpath.add(fullpath.get(i));
//                }
//                Directions pathDirections = new Directions();
//                pathDirections.createDirections(fullpath);
//                ArrayList<String> printedDirections = new ArrayList<String>();
            //    printedDirections = pathDirections.retrieveDirections();
                for(String step : printedDirections){
                    Log.d(TAG,step);
                }
                TextView directionBox = (TextView)findViewById(R.id.directionBox);
                if(!printedDirections.isEmpty()){
                    directionBox.setText(printedDirections.get(0));
                }

                nLineView2.setStartX(fullpath.get(0).getX()*160+225);
                nLineView2.setStartY(fullpath.get(0).getY()*160+830 );
                //      nLineView2.draw();
                nLineView2.setEndX((endnode.getX()*160+225));
                nLineView2.setEndY(endnode.getY()*160+830);
                nLineView2.draw();

                if(fullpath.get(0).equals(endnode)){
                    //TextView directionBox = (TextView)findViewById(R.id.directionBox);
                    directionBox.setText("You have reached your destination!");
                    ndrawPath2.setPointPath(fullpath);
                    ndrawPath2.draw();
                    return;
                }
                ndrawPath2.setPointPath(fullpath);
                ndrawPath2.draw();
            }
        });
        Intent intent = getIntent();
        String endnodename = intent.getStringExtra(MainActivity.EXTRA_END_NODE);
        String startnodename = intent.getStringExtra(MainActivity.EXTRA_START_NODE);
        System.out.println("DATA TRANSFERRRRRRRRRRRRRR");
        System.out.println(endnodename);


        TextView directionBox = (TextView)findViewById(R.id.directionBox);

        graph = textParser(); //Parse text to make nodes
        startnode = graph.findVertex(startnodename);
        endnode = graph.findVertex(endnodename);
       // graph = textParser();
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
      //  ArrayList<String> printedDirections = new ArrayList<String>();
        printedDirections = pathDirections.retrieveDirections();
        for(String step : printedDirections){
            Log.d(TAG,step);
        }
        directionBox.setText(printedDirections.get(0));
        nLineView2 = (LineView2) findViewById(R.id.lineViewnew);
        nLineView2.bringToFront();
        nLineView2.setStartX(startnode.getX()*160+225);
        nLineView2.setStartY(startnode.getY()*160+830);
  //      nLineView2.draw();
        nLineView2.setEndX((endnode.getX()*160+225));
        nLineView2.setEndY(endnode.getY()*160+830);
        nLineView2.draw();


        ndrawPath2 = (DrawPath2) findViewById(R.id.drawpathnew);//Initialize drawPath
        ndrawPath2.bringToFront(); //Draw over background
        ndrawPath2.setPointPath(fullpath);
        ndrawPath2.draw();

      //  ndrawPath2.setPointPath(fullpath);
       // ndrawPath2.draw();

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
    public void initliazeGraph(Graph graph){
        for(Vertex vert: graph.getAllVertex()){
            vert.setPrevious(null);
            vert.setCandidateDistance(0.0);
        }
    }

    /********************************************ALGORITHM*********************************************/
    public  Vertex astar ( Graph graph, Vertex start, Vertex end){
        //Initialization
        MainActivity.PriorityComparator pq = new MainActivity.PriorityComparator();
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

    static public class PriorityComparator implements Comparator<Vertex> {
        @Override
        public int compare(Vertex start, Vertex end){
            return (int)(start.getPriority() - end.getPriority());
        }
    }

    public static double distanceFunction(Vertex start, Vertex end){
        return Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
    }

}
