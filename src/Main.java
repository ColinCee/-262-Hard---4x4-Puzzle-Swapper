import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		int[][] initialState = 	{{4, 2, 6, 14},
				{15, 8, 3, 1},
				{10, 5, 9, 2},
				{7, 11, 16, 3}};
		// TODO Auto-generated method stub
		Swapper swapper = new Swapper();
		swapper.breadthFirstSearch();
/*		List<int[][]> list = swapper.getNextStates(initialState);
		int counter = 0;
		for(int[][] state : list){
			System.out.println(Arrays.deepToString(state));
			counter++;
		}
		System.out.println("Number of next states: " + counter);*/
	}

}
