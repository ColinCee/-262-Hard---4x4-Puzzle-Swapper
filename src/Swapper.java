import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

public class Swapper {
	
	public Swapper(){
		PrintWriter keyWriter;
		PrintWriter valuesWriter;
		
		try {
			keyWriter = new PrintWriter("keys.txt");
			valuesWriter = new PrintWriter("values.txt");
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
	
	public List<List<int[][]>> extendPath(List<int[][]> path){
		
		List<List<int[][]>> nextPaths = new ArrayList<List<int[][]>>();
		List<int[][]> nextStates = getNextStates(path.get(path.size()-1));
		
		for(int i=0; i<nextStates.size();i++){
			List<int[][]> pathClone = new ArrayList<int[][]>();
			pathClone.addAll(path);
			pathClone.add(nextStates.get(i));
			nextPaths.add(pathClone);
		}
		
		return nextPaths;
		
	}
	public void breadthFirstSearch(){
		int[][] goal = {{1,2,3,4},
				{5,6,7,8},
				{9,10,11,12},
				{13,14,15,16}};
		
		int[][] goal2 = 	{{2, 4, 6, 14},
							{15, 8, 3, 1},
							{10, 5, 9, 2},
							{7, 11, 16, 3}};
		
		int[][] initialState = 	{{4, 2, 6, 14},
								{15, 8, 3, 1},
								{10, 5, 9, 2},
								{7, 11, 16, 3}};
		
		Map<int[][], List<int[][]>> stateMap = new HashMap<int[][], List<int[][]>>();
		List<int[][]> nextStates = getNextStates(initialState);
		Queue<int[][]> nextStatesQueue = new LinkedList<int[][]>();
		
		nextStatesQueue.addAll(nextStates);
		putInitialStates(initialState, nextStates);
		//paths.put(initialState, nextStates);
		int[][] nextState = nextStatesQueue.poll();
		double counter = 0;
		
		while(!Arrays.deepEquals(nextState, goal)){
			
			if(!containsKey(nextState) && !stateMap.containsKey(nextState)){
				List<int[][]> children = getNextStates(nextState);
				
				if(counter % 100000 == 0){
					for(Entry<int[][], List<int[][]>> key : stateMap.entrySet()){
						putIntoTextFile(key.getKey(), key.getValue());
					}
					stateMap.clear();
				}
				else
					stateMap.put(nextState, children);
				
				nextStatesQueue.addAll(children);
			}
			nextState = nextStatesQueue.poll();
			
			if(counter % 1000 == 0)
				System.out.println("Passed " + counter/1000 + "k iterations, No. Q elements are: "+ nextStatesQueue.size());
			
			counter++;
		}
		System.out.println("Solution found");
	}
	private void putInitialStates(int[][] state, List<int[][]> nextStates){
		 try {
			ObjectOutputStream oosKeys = new ObjectOutputStream(new FileOutputStream("keys.txt", true));
			ObjectOutputStream oosValues = new ObjectOutputStream(new FileOutputStream("values.txt", true));
			
			oosKeys.writeObject(state);
			oosValues.writeObject(nextStates);

			oosKeys.close();
			oosValues.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void putIntoTextFile(int[][] state, List<int[][]> nextStates){
		 try {
			ObjectOutputStream oosKeys = new AppendingObjectOutputStream(new FileOutputStream("keys.txt", true));
			ObjectOutputStream oosValues = new AppendingObjectOutputStream(new FileOutputStream("values.txt", true));
			
			oosKeys.writeObject(state);
			oosValues.writeObject(nextStates);

			oosKeys.close();
			oosValues.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private boolean containsKey(int[][] key){
		ObjectInputStream oosKeys = null;
		int[][] object = null;
		 try {
			 	oosKeys = new ObjectInputStream(new FileInputStream("keys.txt"));

			 	while ((object = (int[][]) oosKeys.readObject()) != null) {
		 			if(Arrays.deepEquals((int[][]) object, key))
		 				return true;
			 	}
			} catch(EOFException e){
		 		//End of file reached
		 	} catch(IOException e){
				e.printStackTrace();
		 	} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally{
				//Finally close the stream
				try {
					if(oosKeys!=null)
						oosKeys.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		return false;
		
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
		boolean contains = false;
		for(int i=0; i<nextStates.size(); i++){
			if(Arrays.deepEquals(nextStates.get(i), copy)){
				contains = true;
				break;
			}
		}
		return contains;
	}
}
