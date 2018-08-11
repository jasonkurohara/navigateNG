package com.example.jasonkurohara.navigateng;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Edge {

    private Vertex start;
    private Vertex end;
    private int cost;

    public void setStart(Vertex a){
        start = a;
    }

    public void setEnd(Vertex a){
        end = a;
    }

    public void setCost(int weight){
        cost = weight;
    }


    public ArrayList<Vertex> getConnections(){
        ArrayList<Vertex> connections = new ArrayList<Vertex>();
        connections.add(start);
        connections.add(end);
        return connections;
    }

    public int getCost(){
        return cost;
    }
}
