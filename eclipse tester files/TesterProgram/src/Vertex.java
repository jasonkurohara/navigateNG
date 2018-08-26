import java.util.ArrayList;

public class Vertex extends Graph{
    private int x;
    private int y;
    private String name;
    private double candidateDistance;
    private Vertex previous;

    private ArrayList<Vertex> neighbors = new ArrayList<Vertex>();

    public Vertex() {
        x = y = 0;
        name = "";
        candidateDistance = 0;
    }

    public Vertex(int xcor, int ycor, String label){
        x = xcor;
        y = ycor;
        name = label;
        candidateDistance = 0;
    }

    public void setPrevious(Vertex prev){
        previous = prev;
    }

    public Vertex getPrevious(){
        return previous;
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

    public ArrayList<Vertex> getNeighbors(){
        return neighbors;
    }

    public void addNeighbor(Vertex neighbor){
        if(neighbor != null) {
            neighbors.add(neighbor);
        }
    }

    public void setCandidateDistance(double priority){
        candidateDistance = priority;
    }

    public double getPriority(){
        return candidateDistance;
    }
}
