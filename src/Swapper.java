import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

public class Swapper {

	private HashMap<int[][], Integer> fScore;

	public Swapper(){
		
		try {
			PrintWriter keyWriter = new PrintWriter("keys.txt");
			PrintWriter valuesWriter = new PrintWriter("values.txt");
			keyWriter.print("");
			keyWriter.close();
			
			valuesWriter.print("");
			valuesWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public HashMap<int[][], Integer> getfScore() {
		return fScore;
	}
	
	public boolean AStarSearch(int[][] start, int[][] goal){
		// The set of nodes already evaluated.
		List<int[][]> closedList = new ArrayList<int[][]>();
		
		// The set of currently discovered nodes still to be evaluated.
		StateComparator comparator = new StateComparator(this);
		PriorityQueue<int[][]> openList = new PriorityQueue<int[][]>(11, comparator);

        // For each node, which node it can most efficiently be reached from.
        // If a node can be reached from many nodes, cameFrom will eventually contain the
        // most efficient previous step.
        Map<int[][], int[][]> cameFrom = new HashMap<int[][],int[][]>();
        // For each node, the cost of getting from the start node to that node.
        Map<int[][], Integer> gScore = new HashMap<int[][], Integer>();
        // The cost of going from start to start is zero.
    	gScore.put(start, 0);
    	// For each node, the total cost of getting from the start node to the goal
        // by passing by that node. That value is partly known, partly heuristic.
    	fScore = new HashMap<int[][], Integer>();
        // For the first node, that value is completely heuristic.
        fScore.put(start, heuristicEstimate(start, goal));
        // Initially, only the start node is known.
        openList.add(start);
        
        double counter = 0;
        while(!openList.isEmpty()){
        	int[][] current = openList.poll();
        	if(Arrays.deepEquals(current, goal)){
        		reconstructPath(cameFrom,current);
        		return true;
        	}
        	
        	openList.remove(current);
        	closedList.add(current);
        	
        	for(int[][] neighbor : getNextStates(current)){
        		boolean addNewNode = false;
        		// Ignore the neighbor which is already evaluated.
        		if(deepListContains(closedList, neighbor))
        			continue;
        		// The distance from start to a neighbor
    			int tentative_gScore = deepGetHashMap(gScore, current) + 1;
    			
    			if(!deepPQContains(openList,neighbor))
    				addNewNode = true;
    			else if(tentative_gScore >= deepGetHashMap(gScore, neighbor))
    				continue;	// This is not a better path.
    			
    			// This path is the best until now. Record it!
				cameFrom.put(neighbor, current);
				gScore.put(neighbor, tentative_gScore);
				fScore.put(neighbor, deepGetHashMap(gScore, neighbor) + heuristicEstimate(neighbor,goal));
				if(addNewNode)
					openList.add(neighbor);		// Discover a new node
			
        	}
        	counter++;
        	if(counter % 50 == 0 && counter!= 0)
				System.out.println("Passed " + counter + " iterations, ClosedList Size is: " + closedList.size() + ", OpenList size is: "+ openList.size());

        }
        return false;
		
	}

	private int heuristicEstimate(int[][] state, int[][] goal) {
		int distance = 0;
		assert(state.length == goal.length) : "Mismatch array sizes";
		for(int i=0;i<state.length; i++){
			for(int j=0;j<state.length; j++){
				if(state[i][j] != goal[i][j]){
					distance += getManhattanDistance(state[i][j],goal,i,j);
				}
			}
		}
		return distance;
	}
	private int getManhattanDistance(int match, int[][] goal, int a, int b){
		int distance = 0;
		for(int i=0;i<goal.length;i++){
			for(int j=0;j<goal.length;j++){
				if(match == goal[i][j])
					distance = Math.abs(a-i) + Math.abs(b-j);
			}
		}
		return distance;
	}
	private void reconstructPath(Map<int[][],int[][]> cameFrom, int[][] current){
		Stack<int[][]> path = new Stack<int[][]>();
		path.push(current);
		
		while(cameFrom.containsKey(current)){
			current = cameFrom.get(current);
			path.push(current);
		}
		
		
		//Print out the instructions
		int size = path.size()-1;
		System.out.println("Solution found with: " + size + " steps.");
		while(!path.isEmpty()){
			System.out.println(Arrays.deepToString(path.pop()));
		}
		
	}
	
	private List<int[][]> getNextStates(int[][] currentState){
		
		int[][] copy;
		List<int[][]> nextStates = new ArrayList<int[][]>();
		
		//For each cell get all possible next states and add it to the list
		for(int i=0;i<4; i++){
			for(int j=0; j<4; j++){
				//TOP
				if(i>0){
					copy = deepCopyArray(currentState);
					copy[i-1][j] = currentState[i][j];
					copy[i][j] = currentState[i-1][j];
					if(!deepListContains(nextStates, copy))
						nextStates.add(copy);
				}
				//BOTTOM
				if(i<3){
					copy=deepCopyArray(currentState);
					copy[i+1][j] = currentState[i][j];
					copy[i][j] = currentState[i+1][j];
					if(!deepListContains(nextStates, copy))
						nextStates.add(copy);
				}
				//LEFT
				if(j>0){
					copy = deepCopyArray(currentState);
					copy[i][j-1] = currentState[i][j];
					copy[i][j] = currentState[i][j-1];
					if(!deepListContains(nextStates, copy))
						nextStates.add(copy);
				}
				//RIGHT
				if(j<3){
					copy = deepCopyArray(currentState);
					copy[i][j+1] = currentState[i][j];
					copy[i][j] = currentState[i][j+1];
					if(!deepListContains(nextStates, copy))
						nextStates.add(copy);
				}
					
			}
/*			System.out.println("Original: " +Arrays.deepToString(currentState));
			for(int[][] state : nextStates){
				System.out.println(Arrays.deepToString(state));
			}*/
		}
		return nextStates;

	}
	//Requires this due to shallow copying of .clone method
	private int[][] deepCopyArray(int[][] original){
		int[][] copyOfArray = new int[original.length][];
		for(int i=0;i<original.length;i++){
			copyOfArray[i] = new int[original[i].length];
			for(int j=0; j<original[i].length; j++)
				copyOfArray[i][j] = original[i][j];
		}
		return copyOfArray;
		
	}
	//Requires this due to shallow checking of .equals method on multidimensional arrays
	private boolean deepListContains(List<int[][]> nextStates, int[][] copy) {
		for(int i=0; i<nextStates.size(); i++){
			if(Arrays.deepEquals(nextStates.get(i), copy))
				return true;
		}
		return false;
	}
	
	private boolean deepPQContains(PriorityQueue<int[][]> PQ, int[][] state){

		for(int[][] element : PQ){
			if(Arrays.deepEquals(element, state))
				return true;
		}
		return false;
	}
	
	private Integer deepGetHashMap(Map<int[][], Integer> gScore, int[][] key){
		for(Entry<int[][], Integer> entry : gScore.entrySet()){
			if(Arrays.deepEquals(entry.getKey(),key))
				return entry.getValue();
		}
		return null;
	}
	
}
