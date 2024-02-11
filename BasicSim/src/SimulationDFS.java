import java.util.*;
import java.util.concurrent.*;
import java.io.*;


public class SimulationDFS {
	private boolean[][] grid;
	private List<Integer> list;
	private List<Integer> workinglist;
	private static int completed = 0;

	public static void main(String[] args) throws InterruptedException {
		Scanner s = new Scanner(System.in);
		System.out.print("Gridsize (odd number): ");
		int size = s.nextInt();
		System.out.print("Number of iterations: ");
		int nbr  = s.nextInt();
		System.out.println("Print estimation every ___ iterations.");
		int printcount = s.nextInt();
		//SimulationDFS sim = new SimulationDFS(size, nbr, printcount);
		
		
		//Multithread part
		ExecutorService pool = Executors.newFixedThreadPool(2);
		int together = 5;
		Set<Future<?>> tasks = new HashSet<>();
		
		// Jonatans version
		long startTime = System.currentTimeMillis();
		double[] prob = new double[100000];
		for (int i = 0; i<nbr/together; i++) {
			Future<?> ft = pool.submit(() -> {
				DFSSolver dfss = new DFSSolver(size,100000,together);
				double[] newprob = dfss.solve();
				increaseprob(newprob, prob, together);
			});
			tasks.add(ft);
		}
		pool.shutdown();
		while (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
			System.out.println("Waiting");
		}
		for (int i = 0; i<prob.length; i++) {
			prob[i] /= nbr/together;
		}
		//prob = newprob;
		long endTime = System.currentTimeMillis();
		System.out.println("Simulation finished: \n" + Arrays.toString(prob));
		System.out.println("Time taken: " + (endTime-startTime) + "; Tasks completed: " + completed);
		try (FileWriter out = new FileWriter(String.valueOf(size) + String.valueOf(nbr), true)) {
			out.write("Grid size: " + size + "; Iterations: " + nbr +"; Time taken: " + (endTime-startTime) + "\n" + Arrays.toString(prob) + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void increaseprob(double[] newprob, double[] oldprob, int together) {
		for (int i = 0; i<oldprob.length; i++) {
			oldprob[i] += newprob[i];
		}
		completed+=together;
		System.out.println(completed);
		return;
	}
	
	public SimulationDFS(int size, int nbr, int printcount) {
		long procStart = System.currentTimeMillis();
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
				System.out.println(i);
				//System.out.println(i + ": " + Arrays.toString(successes));
				System.out.println("Time for prepare: " + (afterPrepTime-startTime));
				System.out.println("Total time for one iteration: " + (endTime - startTime));
			}
		}
		double[] prob = new double[successes.length];
		for (int i = 0; i<successes.length; i++) {
			prob[i] = ((double) successes[i])/(double) nbr;
		}
		long procEnd = System.currentTimeMillis();
		System.out.println("Simulation finished: \n" + Arrays.toString(prob));
		System.out.println("Time taken: " + (procEnd-procStart));
		try (FileWriter out = new FileWriter(String.valueOf(size) + String.valueOf(nbr), true)) {
			out.write("Grid size: " + size + "; Iterations: " + nbr +"; Time taken: " + (procEnd-procStart) + "\n" + Arrays.toString(prob) + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
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
