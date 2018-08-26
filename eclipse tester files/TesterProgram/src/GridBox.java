

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GridBox {
    private Vertex location = new Vertex();
    private ArrayList<Double> average = new ArrayList<Double>();
    private ArrayList<Double> standardDeviations = new ArrayList<Double>();

    public void setLocation(Vertex newBox){
        location = newBox;
    }

    public void setAverage(ArrayList<Double> inputavg){
        average = inputavg;
    }

    public void setStandardDeviations(ArrayList<Double> stds){
        standardDeviations = stds;
    }

    public ArrayList<Double> getAverage() {
        return average;
    }

    public ArrayList<Double> getStandardDeviations() {
        return standardDeviations;
    }

    public Vertex getLocation(){
        return location;
    }
}
