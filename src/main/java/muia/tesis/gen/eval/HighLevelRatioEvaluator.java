package muia.tesis.gen.eval;

import java.util.ArrayList;
import java.util.List;

import muia.tesis.map.data.HighLevelMap;

public class HighLevelRatioEvaluator implements Evaluator<HighLevelMap> {

	private String firstKey;
	private String secondKey;
	private int targetNum;
	private int targetDen;

	public HighLevelRatioEvaluator(String firstKey, String secondKey,
			int targetNum, int targetDen) {
		this.firstKey = firstKey;
		this.secondKey = secondKey;

		// reduce fraction (6/8 -> 3/4)
		this.targetNum = targetNum;
		this.targetDen = targetDen;
		int temp;
		while (targetDen != 0) {
			temp = targetDen;
			targetDen = targetNum % targetDen;
			targetNum = temp;
		}
		this.targetNum /= targetNum;
		this.targetDen /= targetNum;
	}

	@Override
	public double eval(HighLevelMap instance) {
		int first = getContentCount(instance, firstKey);
		int second = getContentCount(instance, secondKey);
		int[] div = new int[] { first / targetNum, second / targetDen };

		int a = Math.abs(first - (targetNum * div[0]))
				+ Math.abs(second - (targetDen * div[0]));
		int b = Math.abs(first - (targetNum * div[1]))
				+ Math.abs(second - (targetDen * div[1]));

		return -Math.min(a, b);
	}

	@Override
	public void printFitness(HighLevelMap instance) {
	}

	private int getContentCount(HighLevelMap instance, String key) {
		List<String> keys = new ArrayList<>(instance.getContent().keySet());
		int idx = keys.indexOf(key);

		// TODO: error, key not found
		if (idx == -1) return 0;

		int count = 0;
		for (int[] c : instance.getContentCounts())
			count += c[idx];

		return count;
	}
}
