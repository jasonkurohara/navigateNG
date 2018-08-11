package com.example.jasonkurohara.navigateng;

import java.util.ArrayList;

public class Graph{

    public ArrayList<Edge> allEdges = new ArrayList<Edge>();
    public ArrayList<Vertex> allVertex = new ArrayList<Vertex>();

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

    public Vertex findVertex(String vertName){
        for( Vertex current : allVertex ){
            if(vertName.equals(current.getName())){
                return current;
            }
        }
        return null;
    }
}
