package com.example.jasonkurohara.navigateng;

import java.util.ArrayList;

public class Vertex extends Graph{
    private int x;
    private int y;
    private String name;
    private double candidateDistance;

    private ArrayList<Vertex> neighbors;

    public Vertex() {
    	x = y = 0;
    	name = "";
    	candidateDistance = 0;
    }
    
    public int getX(){
        return x;
    }

    public String getName(){
        return name;
    }

    public int getY(){
        return y;
    }

    public void setName(String title){
        name = title;
    }

    public void setX(int xcoord){
        x = xcoord;
    }

    public void setY(int ycoord){
        y = ycoord;
    }

    public void addNeighbors(Vertex neighbor){
        if(neighbor != null) {
            `neighbors.add(neighbor);
        }
    }

    public void setCandidateDistance(double priority){
        candidateDistance = priority;
    }

    public double getPriority(){
        return candidateDistance;
    }
}
