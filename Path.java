import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;


public class Path {
	private ArrayList<Vertex> checkpoints;
	private Graph map;
	
	public Path() {
		checkpoints = new ArrayList<Vertex>();
		map = new Graph();
	}
	
	public ArrayList<Vertex> getCheckpoints() {
		return checkpoints;
	}
    
    public void createPath() throws Exception {
    	map = textParser();
    	
        Vertex start = new Vertex();
        start = map.findVertex("Room 412D");
        System.out.println(start);
        Vertex end = new Vertex();
        end = map.findVertex("Elevator");
        
    	Vertex curr = new Vertex();
    	curr = astar(map,start,end);
    	checkpoints.add(0,curr);
    	while(curr.getPrevious() != null) {
    		checkpoints.add(0,curr.getPrevious());
    		curr = curr.getPrevious();
    	}
    }
    
    public void printPath() {
        System.out.println("Print path");
    	for(Vertex curr : checkpoints) {
            System.out.println(curr.getName());
    	}
    }
	
    public Graph textParser() throws Exception {
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
                            int delimiter = 0;
                            
                            Vertex newVert = new Vertex();
                            if ((delimiter = data.indexOf(",")) != -1) {
                                newVert.setName(data.substring(0, delimiter));
                            }
                            data = data.substring(delimiter + 1);
                            if ((delimiter = data.indexOf(",")) != -1) {
                                newVert.setX(Integer.parseInt(data.substring(0, delimiter)));
                            }

                            data = data.substring(delimiter + 1);
                            newVert.setY(Integer.parseInt(data));
                            graph.addVertex(newVert);
//                    		System.out.println("Name: " + newVert.getName());
//                        	System.out.println(newVert.getX());
//                          System.out.println(newVert.getY());
                    } else if (state == 2) {
                        int delimiter = 0;

                        if ((delimiter = data.indexOf(",")) != -1) {
                            //Finds corresponding vertex in allVertex based on name
                            String vert1name = data.substring(0, delimiter);
                            data = data.substring(delimiter + 1);
                            String vert2name = data;

                            Vertex start = new Vertex();
                            Vertex end = new Vertex();
                            start = graph.findVertex(vert1name);
                            end = graph.findVertex(vert2name);

                            if (start != null && end != null) {
                            	start.addNeighbor(end);
                            	end.addNeighbor(start);
                                double distance = distanceFunction(start, end);
                                Edge newEdge = new Edge(start, end, distance);
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

    public Vertex astar ( Graph graph, Vertex start, Vertex end){

        //Initialization
        PriorityComparator pq = new PriorityComparator();
        PriorityQueue<Vertex> pqueue = new PriorityQueue<Vertex>(25,pq);
        HashMap<Vertex,Double> priorities = new HashMap<Vertex,Double>();

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
                System.out.println("Found Path.");
            	return end;
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
                    pqueue.add(neighbor); //This is change of priority, so may delete and re add neighbor
                }
            }
        }
        return end;
    }

    static public class PriorityComparator implements Comparator<Vertex>{
        @Override
        public int compare(Vertex start, Vertex end){
            return (int)(start.getPriority() - end.getPriority());
        }
    }

    public static double distanceFunction(Vertex start, Vertex end){
        return Math.sqrt( Math.pow(start.getX() - end.getX(),2) + Math.pow(start.getY() - end.getY(),2));
    }
}
