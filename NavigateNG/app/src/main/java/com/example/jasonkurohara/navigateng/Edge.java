package com.example.jasonkurohara.navigateng;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Edge {

    private Vertex start;
    private Vertex end;
    private double cost;

    public Edge() {
        start = null;
        end = null;
        cost = 0;
    }

    public Edge(Vertex a, Vertex b, double dist) {
        start = a;
        end = b;
        cost = dist;
    }

    public void setStart(Vertex a){
        start = a;
    }

    public void setEnd(Vertex a){
        end = a;
    }

    public void setCost(double weight){
        cost = weight;
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