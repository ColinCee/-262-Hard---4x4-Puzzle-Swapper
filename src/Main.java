public class Main {

	public static void main(String[] args) {
		Swapper swapper = new Swapper();
		long startTime = System.currentTimeMillis();
		swapper.breadthFirstSearch();		//code
		long endTime = System.currentTimeMillis();
		
		System.out.println("Took "+(endTime - startTime) + " ms"); 
	}

}
