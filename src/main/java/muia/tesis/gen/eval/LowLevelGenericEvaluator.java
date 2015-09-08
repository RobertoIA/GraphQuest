package muia.tesis.gen.eval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import muia.tesis.map.data.LowLevelMap;

import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Node;

public class LowLevelGenericEvaluator implements Evaluator<LowLevelMap> {

	@Override
	public double eval(LowLevelMap instance) {
		return criticalPathFitness(instance) + contentClustersFitness(instance);
	}
	
	@Override
	public void printFitness(LowLevelMap instance) {
		System.out.println("\tcriticalPathFitness=" + criticalPathFitness(instance));
		System.out.println("\tcontentClustersFitness=" + contentClustersFitness(instance));
	}

	private static double criticalPathFitness(LowLevelMap instance) {
		List<Node> mainNodes = new ArrayList<>();
		for (Node node : instance.getGraph()) {
			if (!node.getAttribute("ui.class").equals("zone")) {
				List<String> content = node.getAttribute("content");
				if ((content != null && content.contains("k"))
						|| node.hasAttribute("zoneConn")) mainNodes.add(node);
			} else mainNodes.add(node);
		}
		Set<Node> criticalPath = new HashSet<>();

		AStar aStar = instance.getPathPlanner();
		for (int i = 0; i < mainNodes.size() - 1; i++) {
			for (int j = i + 1; j < mainNodes.size(); j++) {
				aStar.compute(mainNodes.get(i).getId(), mainNodes.get(j)
						.getId());
				criticalPath.addAll(aStar.getShortestPath().getNodeSet());
			}
		}
		List<Node> optional = new ArrayList<>(instance.getGraph().getNodeSet());
		optional.removeAll(criticalPath);

		double fitness = 0.0;
		for (Node node : optional)
			if (!node.hasAttribute("content")) fitness -= 1;

		return fitness;
	}

	private static double contentClustersFitness(LowLevelMap instance) {
		// TODO: NaN ???
		List<String> contentNodes;

		double fitness = 0;
		for (String type : instance.getContent().keySet()) {
			contentNodes = new ArrayList<>();
			for (Node node : instance.getGraph()) {
				List<String> c = node.getAttribute("content");
				for (String typeContent : instance.getContent().get(type)) {
					if (c != null && c.contains(typeContent)) {
						contentNodes.add(node.getId());
						break;
					}
				}
			}

			// identifica nodos que contienen contenido de un tipo determinado
			AStar aStar = instance.getPathPlanner();
			double partialFitness = 0.0;
			for (int i = 0; i < contentNodes.size() - 1; i++) {
				String cNode = contentNodes.get(i);
				int minDist = Integer.MAX_VALUE;
				for (int j = 0; j < contentNodes.size(); j++) {
					if (cNode != contentNodes.get(j)) {
						aStar.compute(cNode, contentNodes.get(j));
						int dist = aStar.getShortestPath().getEdgeSet().size();
						minDist = Math.min(minDist, dist);
					}
				}
				partialFitness += minDist;
			}
			if (partialFitness > 0) partialFitness /= contentNodes.size() - 1;
			fitness += partialFitness;
		}

		return fitness / instance.getContent().size();
	}
}
