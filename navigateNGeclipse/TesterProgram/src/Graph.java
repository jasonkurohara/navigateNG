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