package muia.tesis.gen.eval;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import muia.tesis.map.data.HighLevelMap;

import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Node;

public class HighLevelGenericEvaluator implements Evaluator<HighLevelMap> {

	@Override
	public double eval(HighLevelMap instance) {
		return branchingFitness(instance) + progressionFitness(instance) + backtrackingFitness(instance);
	}
	
	@Override
	public void printFitness(HighLevelMap instance) {
		System.out.println("\tbranchingFitness=" + branchingFitness(instance));
		System.out.println("\tprogressionFitness=" + progressionFitness(instance));
		System.out.println("\tbacktrackingFitness=" + backtrackingFitness(instance));
	}

	private static double branchingFitness(HighLevelMap instance) {
		AStar aStar = instance.getPathPlanner();
		int zones = instance.getRooms().length;

		int distance = 0;
		for (int i = 0; i < zones - 1; i++) {
			aStar.compute("" + (i + 1), "" + (i + 2));
			distance += aStar.getShortestPath().getEdgeCount();
		}

		return distance / (double) (zones - 1);
	}

	private static double progressionFitness(HighLevelMap instance) {
		int[] rooms = instance.getRooms();
		int[][] contentCounts = instance.getContentCounts();

		double fitness = 0.0;
		double partialFitness;
//		System.err.println("progression fitness for " + instance);

		for (int j = 0; j < contentCounts[0].length; j++) {
			for (int i = 0; i < rooms.length - 1; i++) {

//				System.err.print("|" + contentCounts[i][j] + "/" + rooms[i] + " - ");
//				System.err.print(contentCounts[i + 1][j] + "/" + rooms[i + 1] + " - " );
//				System.err.print("1/" + (rooms.length - 1) + "| + ");
				
				partialFitness = contentCounts[i][j] / (double) rooms[i];
				partialFitness -= contentCounts[i + 1][j]
						/ (double) rooms[i + 1];
				partialFitness -= 1 / (double) (rooms.length - 1);

				fitness += Math.abs(partialFitness);
			}
//			System.err.println();
		}

		return -(fitness / contentCounts[0].length) * (rooms.length - 1);
	}
	
	private static double backtrackingFitness(HighLevelMap instance) {
		AStar aStar = instance.getPathPlanner();

		Set<String> traversed = new TreeSet<>();
		List<Node> path;
		int bt = 0;
		for(int i = 1; i < instance.getRooms().length - 1; i++) {
			aStar.compute("" + (i + 1), "" + (i + 2));
			path = aStar.getShortestPath().getNodePath();
			path = path.subList(1, path.size() - 1);
			
			for(Node n : path) {
				if(traversed.contains(n.getId())) bt++;
				traversed.add(n.getId());
			}
			//TODO: no incluir origen y destino??
			//TODO: incluir 1 y 2 de base?
			
//			System.err.println((i + 1) + "->" + (i + 2) + " : " + path);
		}

		return -0.5 * bt;
	}
}
