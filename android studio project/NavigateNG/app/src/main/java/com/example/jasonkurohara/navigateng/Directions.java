package com.example.jasonkurohara.navigateng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

public class Directions {

    /**
     * @param args
     */

    public static ArrayList<String> directions =  new ArrayList<String>();


    public static void createDirections(ArrayList<Vertex> path) {
        String heading = "unknown";
        String direction = "forward";
        String destination =  "unknown";
        double distance = 0;
        DecimalFormat df = new DecimalFormat("#.#");
        Vertex prev = new Vertex();
        directions.clear();
        for (Vertex curr : path) {
            prev = curr.getPrevious();
            if (prev != null) {
                prev = curr.getPrevious();

                int xdiff = curr.getX() - prev.getX();
                int ydiff = curr.getY() - prev.getY();
                String newHeading;
                String newDirection;
                if (Math.abs(xdiff) > Math.abs(ydiff)) {
                    if (xdiff < 0) {
                        newHeading = "west";
                    } else {newHeading = "east";}
                } else {
                    if (ydiff < 0) {
                        newHeading = "north";
                    } else {newHeading = "south";}
                }

                if((heading == "west" && newHeading == "east") || (heading == "east" && newHeading == "west")
                        || (heading == "north" && newHeading == "south") || (heading == "south" && newHeading == "north"))
                {
                    newDirection = "backward";
                } else if ((heading == "west" && newHeading == "north") || (heading == "east" && newHeading == "south")
                        || (heading == "north" && newHeading == "east") || (heading == "south" && newHeading == "west"))
                {
                    newDirection = "right";
                } else if ((heading == "west" && newHeading == "south") || (heading == "east" && newHeading == "north") //change starting from this line
                        || (heading == "north" && newHeading == "west") || (heading == "south" && newHeading == "east"))
                {
                    newDirection = "left";
                } else {
                    newDirection = "forward";
                }


                if (direction != newDirection) {
                    directions.add("Go " + direction + " and travel " + df.format(distance) + "m toward " + destination);
                    while (distance > 1) {
                        distance--;
                        directions.add("Go " + direction + " and travel " + df.format(distance) + "m toward " + destination);
                    }
                    distance = 0;
                }
                distance += Math.sqrt( Math.pow(curr.getX() - prev.getX(),2) + Math.pow(curr.getY() - prev.getY(),2));
                destination = curr.getName();
                direction = newDirection;
                heading = newHeading;
            }
        }
        directions.add("Go " + direction + " and travel " + df.format(distance) + "m toward " + destination);
    }

    public ArrayList<String> retrieveDirections(){
        return directions;
    }

    public static void printDirections() {
        for (String s : directions) {
            System.out.println(s);
        }
    }


}
