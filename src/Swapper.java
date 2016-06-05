import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Stack;

public class Swapper {
	
	int[][] goal = {{1,2,3,4},
					{5,6,7,8},
					{9,10,11,12},
					{13,14,15,16}};
	
	int[][] goal2 = {{4, 2, 6, 14},
					{8, 15, 3, 1},
					{10, 5, 16, 2},
					{7, 11, 9, 3}};
	
	int[][] initialState = 	{{4, 2, 6, 14},
							{15, 8, 3, 1},
							{10, 5, 9, 2},
							{7, 11, 16, 3}};

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

	public List<int[][]> getNextStates(int[][] currentState){
		
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
					//TOP LEFT
					if(j>0){
						copy = deepCopyArray(currentState);
						copy[i-1][j-1] = currentState[i][j];
						copy[i][j] = currentState[i-1][j-1];
						if(!deepListContains(nextStates, copy))
							nextStates.add(copy);
					}
					//TOP RIGHT	
					if(j<3){
						copy = deepCopyArray(currentState);
						copy[i-1][j+1] = currentState[i][j];
						copy[i][j] = currentState[i-1][j+1];
						if(!deepListContains(nextStates, copy))
							nextStates.add(copy);
						
					}
				}
				//BOTTOM
				if(i<3){
					copy=deepCopyArray(currentState);
					copy[i+1][j] = currentState[i][j];
					copy[i][j] = currentState[i+1][j];
					if(!deepListContains(nextStates, copy))
						nextStates.add(copy);
					//BOTTOM LEFT
					if(j>0){
						copy = deepCopyArray(currentState);
						copy[i+1][j-1] = currentState[i][j];
						copy[i][j] = currentState[i+1][j-1];
						if(!deepListContains(nextStates, copy))
							nextStates.add(copy);
					}
					//BOTTOM RIGHT	
					if(j<3){
						copy = deepCopyArray(currentState);
						copy[i+1][j+1] = currentState[i][j];
						copy[i][j] = currentState[i+1][j+1];
						if(!deepListContains(nextStates, copy))
							nextStates.add(copy);
					}
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
	

	
	public void breadthFirstSearch(){
		
		Map<int[][], List<int[][]>> stateMap = new HashMap<int[][], List<int[][]>>();
		Queue<int[][]> nextStatesQueue = new LinkedList<int[][]>();
		List<int[][]> nextStates = getNextStates(initialState);

		
		nextStatesQueue.addAll(nextStates);
		stateMap.put(initialState, nextStates);
		int[][] nextState = nextStatesQueue.poll();
		int duplicates = 0, counter = 0;
		
		while(true){
			if(!deepContainsKey(nextState, stateMap)){
				nextStates = getNextStates(nextState);
				if(deepListContains(nextStates, goal2)){
					System.out.println("Solution found");
					retracePath(nextState, stateMap);
					break;
				}
				else{
					stateMap.put(nextState, nextStates);
					nextStatesQueue.addAll(nextStates);
				}
			}
			else
				duplicates++;
			
			nextState = nextStatesQueue.poll();
			counter++;
			if(counter % 1000 == 0 && counter!= 0)
				System.out.println("Passed " + counter/1000 + "k iterations, No. Q elements are: "+ nextStatesQueue.size()+ ", Duplicates encountered: " + duplicates);

		}
		
		
		
	}

	private void retracePath(int[][] nextState, Map<int[][], List<int[][]>> stateMap) {
		Stack<int[][]> pathOfStates = new Stack<int[][]>();
		int[][] ancestor = nextState;
		
		//While the ancestor is not equal to the initial state
		while(!Arrays.deepEquals(ancestor, initialState)){
			//push the ancestor on to the stack
			pathOfStates.push(ancestor);
			//Then loop through the map to find the next ancestor
			for(Entry<int[][], List<int[][]>> entry : stateMap.entrySet()){
				if(deepListContains(entry.getValue(), ancestor)){
					ancestor = entry.getKey();
					break;
				}
			}
		}
		System.out.println("Size of path is:" + pathOfStates.size());
		List<String> instructions = new ArrayList<String>();
		//For each element in the stack
		for(int i=0; i<pathOfStates.size()-1; i++){
			int[][] state1 = pathOfStates.get(i);
			int[][] state2 = pathOfStates.get(i+1);
			assert(state1.length == state2.length) : "Mismatching array sizes";
			String instruction = getInstruction(state1,state2);
			instructions.add(instruction);
			
		}
		for(String instruction : instructions){
			System.out.println(instruction);
		}
	}
	private String getInstruction(int[][] state1, int[][] state2){
		//For each cell in the state
		for(int j=0; j<state1.length;j++){
			for(int k=0; k<state1.length;k++){
				if(state1[j][k] != state2[j][k]){
					//String pos1 = "(" + j + " " + k + ")";
					//String pos2 = "";
					return "Swap " + state1[j][k] +" with " + state2[j][k];
				}
			}
		}
		return null;
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
	
	//Deep checking of the hashmap
	private boolean deepContainsKey(int[][] key, Map<int[][], List<int[][]>> stateMap){
		for(int[][] k : stateMap.keySet()){
			if(Arrays.deepEquals(k, key))
				return true;
		}
		return false;
		
	}
}
