package muia.tesis.gen.eval;

import java.util.ArrayList;
import java.util.List;

import muia.tesis.map.data.HighLevelMap;

public class HighLevelContentEvaluator implements Evaluator<HighLevelMap> {
	
	private String key;
	private double target;
	
	public HighLevelContentEvaluator(String key, double target) {
		this.key = key;
		this.target = target;
	}
	
	@Override
	public void printFitness(HighLevelMap instance) {
	}

	@Override
	public double eval(HighLevelMap instance) {
		List<String> keys = new ArrayList<>(instance.getContent().keySet());
		int idx = keys.indexOf(key);
		
		int count = 0;
		for (int[] c : instance.getContentCounts())
			count += c[idx];
		
		return -Math.abs(target - count);
	}
}
