import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Swapper {
	
	public Swapper(){
		breadthFirstSearch();
	}
	private int[][] copyArray(int[][] original){
		int[][] copyOfArray = new int[original.length][];
		for(int i=0;i<original.length;i++){
			copyOfArray[i] = new int[original[i].length];
			for(int j=0; j<original[i].length; j++)
				copyOfArray[i][j] = original[i][j];
		}
		return copyOfArray;
		
	}
	public List<int[][]> getNextStates(int[][] currentState){
		
		List<int[][]> nextStates = new ArrayList<int[][]>();
		int[][] copy;
		//For each cell get all possible next states and add it to the list
		for(int i=0;i<4; i++){
			for(int j=0; j<4; j++){
				//TOP
				if(i>0){
					copy = copyArray(currentState);
					copy[i-1][j] = currentState[i][j];
					copy[i][j] = currentState[i-1][j];
					nextStates.add(copy);
					//TOP LEFT
					if(j>0){
						copy = copyArray(currentState);
						copy[i-1][j-1] = currentState[i][j];
						copy[i][j] = currentState[i-1][j-1];
						nextStates.add(copy);
					}
					//TOP RIGHT	
					if(j<3){
						copy = copyArray(currentState);
						copy[i-1][j+1] = currentState[i][j];
						copy[i][j] = currentState[i-1][j+1];
						nextStates.add(copy);
						
					}
				}
				//BOTTOM
				if(i<3){
					copy=copyArray(currentState);
					copy[i+1][j] = currentState[i][j];
					copy[i][j] = currentState[i+1][j];
					nextStates.add(copy);
					//BOTTOM LEFT
					if(j>0){
						copy = copyArray(currentState);
						copy[i+1][j-1] = currentState[i][j];
						copy[i][j] = currentState[i+1][j-1];
						nextStates.add(copy);
					}
					//BOTTOM RIGHT	
					if(j<3){
						copy = copyArray(currentState);
						copy[i+1][j+1] = currentState[i][j];
						copy[i][j] = currentState[i+1][j+1];
						nextStates.add(copy);
					}
				}
				//LEFT
				if(j>0){
					copy = copyArray(currentState);
					copy[i][j-1] = currentState[i][j];
					copy[i][j] = currentState[i][j-1];
					nextStates.add(copy);
				}
				//RIGHT
				if(j<3){
					copy = copyArray(currentState);
					copy[i][j+1] = currentState[i][j];
					copy[i][j] = currentState[i][j+1];
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
		int[][] initialState = 	{{4, 2, 6, 14},
								{15, 8, 3, 1},
								{10, 5, 9, 2},
								{7, 11, 16, 3}};
		int[][] goal = {{1,2,3,4},
						{5,6,7,8},
						{9,10,11,12},
						{13,14,15,16}};
		List<int[][]> initialStates = getNextStates(initialState);
		Queue<int[][]> nextStates = new LinkedList<int[][]>();
		nextStates.addAll(initialStates);
		int[][] polledState = nextStates.poll();
		int counter = 0, duplicates = 0;
		//List<int[][]> polledNextStates = getNextStates(polledState);
		

		while(!polledState.equals(goal)){
			List<int[][]> polledNextStates = getNextStates(polledState);
			for(int[][] state : polledNextStates){
				if(!nextStates.contains(state))
					nextStates.add(state);
				else
					duplicates++;
			}
			polledState = nextStates.poll();
			counter++;
			if(counter % 1000 == 0)
				System.out.println("Passed " + counter + " iterations, No. Q elements are: "+ nextStates.size() + ", Duplicates encountered: " + duplicates);
		}
		
	}
}
