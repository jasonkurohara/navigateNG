package com.example.jasonkurohara.navigateng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.io.File;
import java.util.Scanner;

public class Graph{


    public Set<Edge> allEdges = new HashSet<Edge>();
    public Set<Vertex> allVertices = new HashSet<Vertex>();

    public void addEdge(Edge edge ){
        allEdges.add( edge );
    }

    public void addVertex( Vertex vertex){
        allVertices.add( vertex );
    }

    public void buildGraph(String fileName) throws Exception {
        File file = new File(fileName);
        Scanner sc = new Scanner(file);


        //Figure out how to read the text file with different delimiters
        //FOR TEXT UNDER VERTICES:
        String name = "";
        int x = 0, y = 0;
        //Get whatever info from the file
        addVertex(new Vertex(x,y,name));
        //FOR TEXT UNDER EDGES
        Vertex a = new Vertex(),b = new Vertex();
        Boolean afound = false, bfound = false;
        Iterator<Vertex> itr = allVertices.iterator();
        while (itr.hasNext()) {
            a = itr.next();
            if (a.getName().equals("pointa")) {
            itr.remove();
            afound = true;
            }
        }
        itr = allVertices.iterator();
        while (itr.hasNext()) {
            b = itr.next();
            if (b.getName().equals("pointb")){
                itr.remove();
                bfound = true;
            }

        }
        if (afound && bfound) {
            a.addNeighbor(b);
            b.addNeighbor(a);
        }
        else {/*connection doesn't exist*/}
    }

    public Set<Edge> getAllEdges(){
        return allEdges;
    }

    public Set<Vertex> getAllVertex() {
        return allVertices;
    }

    public Vertex findVertex(String vertName){ //any faster
        for( Vertex current : allVertices ){
            if(vertName.equals(current.getName())){
                return current;
            }
        }
        return null;
    }

}
