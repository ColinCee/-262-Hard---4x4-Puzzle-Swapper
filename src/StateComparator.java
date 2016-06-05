import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

public class StateComparator implements Comparator<int[][]>{
	
	private Swapper swapper;
	public StateComparator(Swapper swapper){
		this.swapper = swapper;
	}
	@Override
	public int compare(int[][] state1, int[][] state2) {
		int state1fScore = getfScore(state1);
		int state2fScore = getfScore(state2);
		
		if(state1fScore < state2fScore)
			return -1;
		else if(state1fScore > state2fScore)
			return 1;
		else
			return 0;
	}
	private int getfScore(int[][] state) {
		HashMap<int[][], Integer> fScore = swapper.getfScore();
		
		for(Entry<int[][], Integer> entry : fScore.entrySet()){
			if(Arrays.deepEquals(entry.getKey(), state))
				return entry.getValue();
		}
		System.out.println("ERROR");
		return -1;
	}

}
