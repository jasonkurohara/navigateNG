import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

public class Tester {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Graph sample = new Graph();
		sample = textParser();
		for( Vertex vert: sample.getAllVertex()) {
			System.out.println(vert.getName());
		}
		System.out.println("EDGES");
		for( Edge edg : sample.getAllEdges()) {
			System.out.println(edg.getStart().getName());
			System.out.println(edg.getEnd().getName());
		}
		
		Vertex start = new Vertex();
		start = sample.getAllVertex().get(1);
		System.out.println("POINT A: " + start.getName());
		
		Vertex end = new Vertex();
		end = sample.getAllVertex().get(24);
		System.out.println("POINT B: " + end.getName());
		
		astar(sample,start,end);
        Vertex curr = new Vertex();
        curr = end;
        while( (curr = curr.getPrevious()) != null){
            System.out.println(curr.getName());
        }
	}

    public static Graph textParser() throws Exception {
        String data = "";
        StringBuffer sbuffer = new StringBuffer();
        InputStream is = new FileInputStream("sample.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String mapName = "";
        Graph graph = new Graph();

        int state = 0;

        if (is != null) {
            try {
                while ((data = reader.readLine()) != null) {
                    if (data.equals("TITLE")) {
                        continue;
                    } else if (state == 0 && !data.equals("TITLE")) {
                        mapName = data;
                        state++;
                    } else if (state == 1) {
                        if (data.equals("VERTEX")) {
                            continue;
                        }

                        if (data.equals("EDGES")) {
                            state++;
                            continue;
                        }

                        if (!data.equals("EDGES")) {
                            int delimiter = 0;
                            Vertex newVert = new Vertex();
                            if ((delimiter = data.indexOf(";")) != -1) {
                                newVert.setName(data.substring(0, delimiter));
                            }
                            data = data.substring(delimiter + 1);
                            if ((delimiter = data.indexOf(",")) != -1) {
                                newVert.setX(Integer.parseInt(data.substring(0, delimiter)));
                            }

                            data = data.substring(delimiter + 1);
                            newVert.setY(Integer.parseInt(data));
                            graph.addVertex(newVert);
                        }
                    } else if (state == 2) {
                        int delimiter = 0;

                        if ((delimiter = data.indexOf(";")) != -1) {
                            //Finds corresponding vertex in allVertex based on name
                            String vert1name = data.substring(0, delimiter);
                            System.out.println(vert1name);
                            data = data.substring(delimiter + 1);
                            String vert2name = data;
                            System.out.println(vert2name);
                            
//                            //All grpah vertexes
//                    		for( Vertex vert: graph.getAllVertex()) {
//                    			System.out.println(vert.getName());
//                    			System.out.println(vert.getX());
//                    		}
                            Vertex start = new Vertex();
                            start = graph.findVertex(vert1name);
                            System.out.println(start.getName());
                            Vertex end = new Vertex();
                            end = graph.findVertex(vert2name);
             //               System.out.println("VERT2NAME:" + vert2name);

                           // System.out.println(end.getName());
                      
                //            System.out.println("Expected: "+vert1name+","+vert2name);
                            if (start != null && end != null) {
                //                System.out.println("Result: "+start.getName()+","+end.getName());
                                start.addNeighbor(end);
                                end.addNeighbor(start);
                                double distance = Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
                                Edge newEdge = new Edge(start,end,distance);
                                graph.addEdge(newEdge);
                            } 
                        }
                    }
                    sbuffer.append(data + "n");
                }
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return graph;
    }
    public static double distanceFunction(Vertex start, Vertex end){
        return Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
    }
    
    public static ArrayList<Vertex> astar ( Graph graph, Vertex start, Vertex end){


        PriorityComparator pq = new PriorityComparator();
        PriorityQueue<Vertex> pqueue = new PriorityQueue<Vertex>(25,pq);
        HashMap<Vertex,Double> priorities = new HashMap<Vertex,Double>();
        ArrayList<Vertex> path = new ArrayList<Vertex>();
        path.add(start);

        priorities.put(start,0.0); //Candidate distance is 0


        start.setCandidateDistance(distanceFunction(start,end));
        pqueue.add(start);
        priorities.put(start,0.0);

        Set<Vertex> confirmed = new HashSet<Vertex>(); //green
        ArrayList<Vertex> unexplored = new ArrayList<>(); //uncolored;
        unexplored = graph.getAllVertex();
        unexplored.remove(start);

        Set<Vertex> potential = new HashSet<Vertex>(); //yellow

        while( !pqueue.isEmpty() ) {
            Vertex point = new Vertex();
            point = pqueue.poll(); //dequeue

            confirmed.add(point);

            if (point.equals(end)) { //BASE CASE
                return path;
            }

            double candidateDistance = priorities.get(point);

            for (Vertex neighbor : point.getNeighbors() ) {
                double heuristic = distanceFunction(neighbor, end);

                if (unexplored.contains(neighbor)) {
                    unexplored.remove(neighbor);
                    potential.add(neighbor);

                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));

                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
                    pqueue.add(neighbor); //simply add
                    neighbor.setPrevious(point);
 
                } else if (potential.contains(neighbor) && priorities.get(neighbor) > candidateDistance + distanceFunction(point, neighbor)) {
                    priorities.remove(neighbor);
                    priorities.put(neighbor, candidateDistance + distanceFunction(point, neighbor));

                    neighbor.setPrevious(point);
                    pqueue.remove(neighbor);
                    neighbor.setCandidateDistance(priorities.get(neighbor) + heuristic);
                    pqueue.add(neighbor); //This is change of prioritiy, so may delete and re add neighbor
                }
            }
        }
        return path;
    }

    static public class PriorityComparator implements Comparator<Vertex>{
        public int compare(Vertex start, Vertex end){
            return (int)(start.getPriority() - end.getPriority());
        }
    }

    
    
}