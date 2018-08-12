package com.example.jasonkurohara.navigateng;

import java.util.ArrayList;

public class Vertex extends Graph{
    private int x;
    private int y;
    private String name;

    private ArrayList<Vertex> neighbors;

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
        neighbors.add(neighbor);
    }

    public boolean isConnected(  Vertex end ){
        for( Edge currentEdge: allEdges ){
            ArrayList<Vertex> connections = new ArrayList<Vertex>();
            connections = currentEdge.getConnections();
            if( connections.contains(this) && connections.contains(end) ){
                return true;
            }
        }
        return false;
    }

    public void addNeighbors(Vertex neighbor){
        neighbors.add(neighbor);
    }
}
