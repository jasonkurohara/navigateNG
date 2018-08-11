package com.example.jasonkurohara.navigateng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Graph{


    public Set<Edge> allEdges = new HashSet<Edge>();
    public Set<Vertex> allVertex = new HashSet<Vertex>();

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

    public Set<Edge> getAllEdges(){
        return allEdges;
    }

    public Set<Vertex> getAllVertex() {
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
}
