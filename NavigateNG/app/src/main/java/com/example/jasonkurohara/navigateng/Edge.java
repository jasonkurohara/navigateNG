package com.example.jasonkurohara.navigateng;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Edge {

    private Vertex start;
    private Vertex end;
    private double cost;

    public void setStart(Vertex a){
        start = a;
    }

    public void setEnd(Vertex a){
        end = a;
    }

    public void setCost(double weight){
        cost = weight;
    }


    public ArrayList<Vertex> getConnections(){
        ArrayList<Vertex> connections = new ArrayList<Vertex>();
        connections.add(start);
        connections.add(end);
        return connections;
    }

    public Vertex getStart(){
        return start;
    }

    public Vertex getEnd() {
        return end;
    }

    public double getCost(){
        return cost;
    }
}
