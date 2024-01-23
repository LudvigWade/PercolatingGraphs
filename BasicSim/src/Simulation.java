import java.util.*;
import java.io.*;


public class Simulation {
	private boolean[][] grid;
	private Map<Integer,BooleanSet> map;
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
		Simulation sim = new Simulation(size, nbr, printcount);
	}
	
	public Simulation(int size, int nbr, int printcount) {
		int[] successes = new int[size*size+1];
		list = new ArrayList<Integer>(size*size);
		workinglist = new ArrayList<Integer>(size*size);
		map = new HashMap<>();
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
				System.out.println(i + ": " + Arrays.toString(successes));
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
		grid[gridsize/2][gridsize/2] = true;
		map.clear();
		map.put(gridsize*gridsize/2, new BooleanSet());
		map.get(gridsize*gridsize/2).set.add(gridsize*gridsize/2);
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
			boolean[] a = checkNeigh(x,y);
			int b = 0;
			if (a[0]) {
				map.get(next-grid.length).set.add(next);
				map.put(next, map.get(next-grid.length));
				if (x == 0 || x == grid.length-1 || y == 0 || y == grid.length-1) {
					map.get(next).containsEdge = true;
				}
				b++;
			}
			if (a[1]) {
				if (b == 0) {
					map.get(next+grid.length).set.add(next);
					map.put(next, map.get(next+grid.length));
					if (x == 0 || x == grid.length-1 || y == 0 || y == grid.length-1) {
						map.get(next).containsEdge = true;
					}
				} else {
					map.get(next+grid.length).set.addAll(map.get(next).set);
					if (map.get(next).containsEdge) {
						map.get(next+grid.length).containsEdge = true;
					}
					for (Integer k : map.get(next).set) {
						map.put(k, map.get(next+grid.length));
					}
				}
				b++;
			}
			if (a[2]) {
				if (b == 0) {
					map.get(next-1).set.add(next);
					map.put(next, map.get(next-1));
					if (x == 0 || x == grid.length-1 || y == 0 || y == grid.length-1) {
						map.get(next).containsEdge = true;
					}
				} else {
					map.get(next-1).set.addAll(map.get(next).set);
					if (map.get(next).containsEdge) {
						map.get(next-1).containsEdge = true;
					}
					for (Integer k : map.get(next).set) {
						map.put(k, map.get(next-1));
					}
				}
				b++;
			}
			if (a[3]) {
				if (b == 0) {
					map.get(next+1).set.add(next);
					map.put(next, map.get(next+1));
					if (x == 0 || x == grid.length-1 || y == 0 || y == grid.length-1) {
						map.get(next).containsEdge = true;
					}
				} else {
					map.get(next+1).set.addAll(map.get(next).set);
					if (map.get(next).containsEdge) {
						map.get(next+1).containsEdge = true;
					}
					for (Integer k : map.get(next).set) {
						map.put(k, map.get(next+1));
					}
				}
				b++;
			}
			if (b == 0) {
				map.put(next, new BooleanSet());
				map.get(next).set.add(next);
				if (x == 0 || x == grid.length-1 || y == 0 || y == grid.length-1) {
					map.get(next).containsEdge = true;
				}
			}
			if (map.get(grid.length*grid.length/2).containsEdge) {
				break;
			}
		}
		for (int i = nbr; i<ret.length; i++) {
			ret[i]++;
		}
		return ret;
	}
	
	private class BooleanSet {
		Set<Integer> set;
		boolean containsEdge;
		
		public BooleanSet() {
			set = new HashSet<>();
			containsEdge = false;
		}
		
		public int hashCode() {
			return set.hashCode();
		}
	}

}
