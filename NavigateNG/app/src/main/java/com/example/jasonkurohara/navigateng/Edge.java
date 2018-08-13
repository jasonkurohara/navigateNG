package com.example.jasonkurohara.navigateng;

import java.lang.reflect.Array;
import java.util.ArrayList;

enum Direction {N,S,E,W};

public class Edge {

    private Vertex dest;
    private double cost;
    private Direction direction;

    public Edge(Vertex destination, double distance) {
        dest = destination;
        cost = distance;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Vertex getDest() {
        return dest;
    }

    public double getCost() {
        return cost;
    }

    public Direction getDirection() {
        return direction;
    }
}
