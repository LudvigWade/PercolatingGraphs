import java.util.*;
import java.io.*;


public class SimulationDFS {
	private boolean[][] grid;
	private List<Integer> list;
	private List<Integer> workinglist;

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.print("Gridsize (odd number): ");
		int size = s.nextInt();
		System.out.print("Number of iterations: ");
		int nbr  = s.nextInt();
		System.out.println("Print estimation every ___ iterations.");
		int printcount = s.nextInt();
		SimulationDFS sim = new SimulationDFS(size, nbr, printcount);
	}
	
	public SimulationDFS(int size, int nbr, int printcount) {
		int[] successes = new int[size*size+1];
		list = new ArrayList<Integer>(size*size);
		workinglist = new ArrayList<Integer>(size*size);
		for (int i = 0; i<size*size; i++) {
			list.add(i);
		}
		for (int i = 0; i<nbr; i++) {
			long startTime = System.currentTimeMillis();
			prepare(size);
			long afterPrepTime = System.currentTimeMillis();
			int[] res = sim();
			for (int j = 0; j<successes.length; j++) {
				successes[j] += res[j];
			}
			long endTime = System.currentTimeMillis();
			if (i%printcount == 0) {
				//System.out.println(i + ": " + Arrays.toString(successes));
				System.out.println("Time for prepare: " + (afterPrepTime-startTime));
				System.out.println("Total time for one iteration: " + (endTime - startTime));
			}
		}
		double[] prob = new double[successes.length];
		for (int i = 0; i<successes.length; i++) {
			prob[i] = ((double) successes[i])/(double) nbr;
		}
		System.out.println("Simulation finished: \n" + Arrays.toString(prob));
	}
	
	
	/** Checks neighbors to coordinate to see if they are true.
	 * 
	 * @return	boolean array representing each direction [N,S,W,E]
	 */
	private boolean[] checkNeigh(int x, int y) {
		boolean[] ret = new boolean[4];
		if (x > 0 && grid[x-1][y]) { //Above
			ret[0] = true;
		}
		if (x+1 < grid.length && grid[x+1][y]){ //Below
			ret[1] = true;
		}
		if (y > 0 && grid[x][y-1]) { //Left
			ret[2] = true;
		}
		if (y+1 < grid[x].length && grid[x][y+1]) { //Right
			ret[3] = true;
		}
		return ret;
	}
	
	private void prepare(int gridsize) {
		grid = new boolean[gridsize][gridsize];
		//grid[gridsize/2][gridsize/2] = true;
		workinglist.clear();
		workinglist.addAll(list);
		Collections.shuffle(workinglist);
	}
	
	private int[] sim() {
		boolean connected = false;
		int nbr = 0;
		int[] ret = new int[grid.length*grid.length+1];
		while (!connected) {
			int next = workinglist.remove(workinglist.size()-1);
			int x = next/grid.length;
			int y = next%grid.length;
			if (grid[x][y]) {
				continue;
			}
			nbr++;
			grid[x][y] = true;
			if (dfs()) {
				connected = true;
				break;
			}
			
		}
		for (int i = nbr; i<ret.length; i++) {
			ret[i]++;
		}
		return ret;
	}
	
	private boolean dfs() {
		if (!grid[grid.length/2][grid.length/2]) {
			return false;
		}
		Stack<Integer> stack = new Stack<>();
		boolean[] isVisited = new boolean[grid.length*grid.length];
		stack.push(grid.length*grid.length/2);
		while (!stack.isEmpty()) {
			int current = stack.pop();
			isVisited[current] = true;
			if (isEdge(current)) {
				return true;
			}
			if (grid[current/grid.length-1][current%grid.length] && !isVisited[current-grid.length]) {
				stack.push(current-grid.length);
			}
			if (grid[current/grid.length+1][current%grid.length] && !isVisited[current+grid.length]) {
				stack.push(current+grid.length);
			}
			if (grid[current/grid.length][current%grid.length-1] && !isVisited[current-1]) {
				stack.push(current-1);
			}
			if (grid[current/grid.length][current%grid.length+1] && !isVisited[current+1]) {
				stack.push(current+1);
			}
		}
		return false;
	}
	
	private boolean isEdge(int nbr) {
		int x = nbr/grid.length;
		int y = nbr%grid.length;
		if (x == 0 || x == grid.length-1 || y == 0 || y == grid.length-1) {
			return true;
		}
		return false;
	}

}
