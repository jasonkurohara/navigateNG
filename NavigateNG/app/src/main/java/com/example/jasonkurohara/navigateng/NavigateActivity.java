package com.example.jasonkurohara.navigateng;

import android.os.health.PackageHealthStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NavigateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        PhotoView map = (PhotoView) findViewById(R.id.photo_view);
        map.setImageResource(R.drawable.map);
    }


    public void readText(){

        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = this.getResources().openRawResource(R.raw.sample);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String mapName = "";
        Graph graph = new Graph();
        int state = 0;

        if( is != null){

            try{
                while((data=reader.readLine()) != null){
                    if( data.equals( "TITLE" ) && state == 0 ){
                        mapName = data;
                        state++;
                    }

                    if( data.equals("VERTICES")  state == 1){
                        //String of vertex name and position
                        int delimiter = data.indexOf(";");
                        Vertex newVert = new Vertex();
                        newVert.setName(data.substring(0,delimiter));

                        data = data.substring(delimiter+1);
                        delimiter = data.indexOf(";");
                        newVert.setX( Integer.parseInt(data.substring(0,delimiter)) );

                        data = data.substring(delimiter+1);
                        delimiter = data.indexOf(";");
                        newVert.setY( Integer.parseInt(data.substring(0,delimiter)) );

                        graph.addVertex(newVert);

                    }

                    else if ( data.equals("EDGES") && state == 2){
                        int delimiter = data.indexOf(";");
                        Edge newEdge = new Edge();

                        //Finds corresponding vertex in allVertex based on name

                        String vert1name = data.substring(0,delimiter);
                        data = data.substring(delimiter+1);
                        delimiter = data.indexOf(";");
                        String vert2name = data.substring(0, delimiter);

                        newEdge.setEnd(graph.findVertex(vert1name));
                        newEdge.setStart(graph.findVertex(vert2name));

                        graph.addEdge(newEdge);
                    }
                    sbuffer.append(data + "n");


                }
                is.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }


    }
}

