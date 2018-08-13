package com.example.jasonkurohara.navigateng;
import android.util.Pair;
import java.util.ArrayList;

public class Vertex extends Graph{
    private int x;
    private int y;
    private String name;
    private ArrayList<Edge> neighbors;

    public Vertex() {
        x = 0;
        y = 0;
        name = "";
    }
    public Vertex(int x, int y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
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

    public void addNeighbor(Vertex neighbor){
        double distance = Math.hypot(this.x - neighbor.x ,this.y - neighbor.y);
        neighbors.add(new Edge(neighbor,distance));
        //compare coordinates to see edge's direction (NSEW)
    }

    public ArrayList<Edge> getNeighbors() {
        return neighbors;
    }
}
