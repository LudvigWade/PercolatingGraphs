import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class DFSSolver {
	private boolean[] prevPath;
	private boolean solutionExists;
	private boolean[] visited;
	private boolean[] removed;
	private int[][] adjList;
	private int gridSize;
	private double[] thetaP;
	private int simulations;
	
	public DFSSolver(int gridsize, int discre, int simulations) {
		gridSize = gridsize;
		thetaP = new double[discre];
		this.simulations = simulations;
	}

	public double[] solve() {
		
		Random rand = new Random();
		int centerNode = gridSize * gridSize / 2;
		// alla noder som motsvara att man är på kanten har en adjlist som är null.
		createAdj(gridSize);
		//ArrayList<Integer> randoms;
		for (int i = 0; i < simulations; i++) {
			//System.out.println(i);
			
			int end = gridSize*gridSize-1;
			int[] randNumbers = new int[gridSize*gridSize-1];
			for(int l=0; l<gridSize*gridSize; l++) {
				if (l != gridSize*gridSize/2) {
					randNumbers[l]=l;
				}
			}
		
			//randoms = random(gridSize);
			removed = new boolean[gridSize * gridSize];
			int nbrRemovedNodes = 0;
			solutionExists = true;
			prevPath = new boolean[gridSize*gridSize];
			for(int path=0; path<gridSize/2+1; path++) {
				prevPath[gridSize*gridSize/2 + path]= true;
			}
			while (solutionExists) {
				solutionExists = false;
				
				int r = rand.nextInt(end);
				int nodeToRemove = randNumbers[r];
				randNumbers[r]=randNumbers[end];
				end--;
				
				//int nodeToRemove = randoms.get(nbrRemovedNodes);
				nbrRemovedNodes++;
				removed[nodeToRemove] = true;

				if (prevPath[nodeToRemove]==false) {
					solutionExists = true;
					continue;
				}
				visited = new boolean[gridSize * gridSize];
				prevPath = new boolean[gridSize*gridSize];
				//välj rätt dfs
				itdfs(centerNode);
			}
			// hur många var öppna när det funkade
			int open = gridSize * gridSize - nbrRemovedNodes + 1;
			double p = (double) open / (gridSize * gridSize);

			int start = (int) Math.round(p * (thetaP.length - 1));

			for (int j = start; j < thetaP.length; j++) {
				thetaP[j]++;
			}
		}
		for (int i = 0; i < thetaP.length; i++) {
			thetaP[i] /= simulations;
		}
		return thetaP;
	}
	
	
	
	//testa vilken stack som ger bäst
	private void itdfs(int n) {
		int[] myStack = new int[gridSize*gridSize/2];
		int end = 0;
		myStack[0]=n;
		int[] parents = new int[gridSize*gridSize];
		int finalNode=gridSize*gridSize/2;
		
		while(end>=0) {
			//pop
			int mycurr = myStack[end];
			end--;
			visited[mycurr]=true;
			if(removed[mycurr]) {
				continue;
			}
			if(mycurr<gridSize || mycurr>=gridSize*(gridSize-1) || mycurr%gridSize==0 || (mycurr+1)%gridSize==0) {
				solutionExists = true;
				finalNode=mycurr;
				break;
			}
			if(!visited[mycurr+gridSize]) {
				//push
				myStack[end+1]=mycurr+gridSize;
				end++;
				parents[mycurr+gridSize]=mycurr;
			}
			if(!visited[mycurr+1]) {
				//push
				myStack[end+1]=mycurr+1;
				end++;
				parents[mycurr+1]=mycurr;
			}
			if(!visited[mycurr-gridSize]) {
				//push
				myStack[end+1]=mycurr-gridSize;
				end++;
				parents[mycurr-gridSize]=mycurr;
			}
			if(!visited[mycurr-1]) {
				//push
				myStack[end+1]=mycurr-1;
				end++;
				parents[mycurr-1]=mycurr;
			}
			
		}
		
		while(finalNode!=gridSize*gridSize/2) {
			prevPath[finalNode]=true;
			finalNode=parents[finalNode];
		}
		prevPath[gridSize*gridSize/2]=true;
		
	}

	private void recdfs(int n) {
		if (visited[n]) {
			return;
		}
		visited[n] = true;
		if (removed[n]) {
			return;
		}
		prevPath[n]= true;
		//om vi nått kanten
		if(n<gridSize || n>=gridSize*(gridSize-1) || n%gridSize==0 || (n+1)%gridSize==0) {
			solutionExists = true;
			return;
		}
		if(!visited[n+gridSize]& !removed[n+gridSize]) {
			recdfs(n+gridSize);
		}
		if (solutionExists) {
			return;
		}
		if(!visited[n+1]& !removed[n+1]) {
			recdfs(n+1);
		}
		if (solutionExists) {
			return;
		}
		if(!visited[n-gridSize]& !removed[n-gridSize]) {
			recdfs(n-gridSize);
		}
		if (solutionExists) {
			return;
		}
		if(!visited[n-1]& !removed[n-1]) {
			recdfs(n-1);
		}
		if (solutionExists) {
			return;
		}
		prevPath[n]=false;
	}

	private ArrayList<Integer> random(int gridSize) {
		ArrayList<Integer> randoms = new ArrayList<>();
		for (int i = 0; i < gridSize * gridSize; i++) {
			randoms.add(i);
		}
		Collections.shuffle(randoms);
		return randoms;
	}

	//null längs md kanterna
	private void createAdj(int gridSize) {
		adjList = new int[gridSize * gridSize][4];
		for (int col = 1; col < gridSize - 1; col++) {
			for (int row = 1; row < gridSize - 1; row++) {
				//ovanför
				adjList[col + row * gridSize][0] = col + (row - 1) * gridSize;
				//höger
				adjList[col + row * gridSize][1] = col + 1 + row * gridSize;
				//under
				adjList[col + row * gridSize][2] = col + (row + 1) * gridSize;
				//vänster
				adjList[col + row * gridSize][3] = col - 1 + row * gridSize;
			}
		}
	}

}
