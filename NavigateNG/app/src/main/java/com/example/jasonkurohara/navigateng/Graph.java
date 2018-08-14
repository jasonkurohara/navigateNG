package com.example.jasonkurohara.navigateng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Graph{


    public ArrayList<Edge> allEdges = new ArrayList<Edge>();
    public ArrayList<Vertex> allVertex = new ArrayList<>();

    public void addEdge( Edge edge ){
        allEdges.add( edge );
    }

    public void addVertex( Vertex vertex){
        allVertex.add( vertex );
    }

    public ArrayList< Vertex > getNeighbors( Vertex start ){

        ArrayList< Vertex > neighbors = new ArrayList<Vertex>();
        for( Vertex current : allVertex  ){
            if( start.isConnected(current) ) {
                neighbors.add(current);
            }
        }
        return neighbors;
    }

    public ArrayList<Edge> getAllEdges(){
        return allEdges;
    }

    public ArrayList<Vertex> getAllVertex() {
        return allVertex;
    }

    public Vertex findVertex(String vertName){ //any faster
        for( Vertex current : allVertex ){
            if(vertName.equals(current.getName())){
                return current;
            }
        }
        return null;
    }

    public void loadNeighbors(){

        for( Vertex currentVert: allVertex){ //Look for edges that contain currrent Vert
            for(Edge currentEdge: allEdges){
                ArrayList<Vertex> connections = new ArrayList<Vertex>();
                connections = currentEdge.getConnections();
                if(connections.contains(currentVert)){ //If contained, add opposite vertex as neighbor
                    if(currentVert.equals(currentEdge.getStart())){
                        currentVert.addNeighbor(currentEdge.getEnd());
                    }
                    else{
                        currentVert .addNeighbor(currentEdge.getStart());
                    }
                }
            }
        }
    }
}
