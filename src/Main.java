public class Main {

	public static void main(String[] args) {
		
		int[][] goal = {{1,2,3,4},
						{5,6,7,8},
						{9,10,11,12},
						{13,14,15,16}};
		
		int[][] goal2 = {{4, 6, 2, 14},
						{15, 8, 13, 1},
						{10, 5, 9, 12},
						{7, 11, 16, 3}};
		
		int[][] initialState = 	{{4, 6, 2, 14},
								{15, 8, 13, 1},
								{10, 5, 9, 12},
								{7, 11, 16, 3}};
		
		Swapper swapper = new Swapper();
		long startTime = System.currentTimeMillis();
		
		if(!swapper.AStarSearch(initialState, goal))
			System.out.println("Solution not found");
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Took "+(endTime - startTime) + " ms"); 
	}

}
