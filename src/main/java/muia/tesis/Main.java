package muia.tesis;

import grammar.GrammarException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import muia.tesis.gen.GeneticAlgorithm;
import muia.tesis.gen.Options;
import muia.tesis.gen.eval.Evaluator;
import muia.tesis.gen.eval.HighLevelContentEvaluator;
import muia.tesis.gen.eval.HighLevelRatioEvaluator;
import muia.tesis.map.data.HighLevelMap;

public class Main {

	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");

//		TreeMap<String, String[]> content = loadContent();

		// TODO: parser errors
		// MapBuilder.highLevelMap( new int[] { 3, 5, 2, 6, 4 }, content);

		// AlgorithmAttributes atts = new AlgorithmAttributes(6, 0.2, 0.3, 0.2,
		// 5);
		// GeneticAlgorithm ga = new GeneticAlgorithm(content, new int[] { 3, 6,
		// 4 },
		// atts, atts);
		// CompositeMap map = ga.optimize();
		// LowLevelMap m = map.getLLMaps()[map.getLLMaps().length - 2];
		// m.getGraph().display();
		// System.out.println(m);

		// ------------------------

		Options hlOptions = new Options(20, 10, 5, 7, 12);
		Options llOptions = new Options(6, 5 , 2, 3, 2);
		int[] rooms = new int[] { 5, 6, 7, 5, 3 };
		
		List<Evaluator<HighLevelMap>> evals = new ArrayList<>();
		evals.add(new HighLevelContentEvaluator("treasure", 12));
		evals.add(new HighLevelRatioEvaluator("treasure", "monster", 3, 5));
		
		GeneticAlgorithm ga = new GeneticAlgorithm(rooms, evals, loadContent());
		try {
			System.out.println(ga.run(hlOptions, llOptions));
		} catch (GrammarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// HighLevelBuilder hlBuilder = new HighLevelBuilder(
		// new int[] { 3, 4, 6 }, content);
		// LowLevelBuilder llBuilder = new LowLevelBuilder(6, Arrays.asList("*",
		// "E", "D"), new int[] { 4, 2 }, content);
		//
		// HighLevelMap[] hlMaps = hlBuilder.build(10);
		// LowLevelMap[] llMaps = llBuilder.build(10);
		//
		// Evaluator<HighLevelMap> hgEval = new HighLevelGenericEvaluator();
		// Evaluator<LowLevelMap> llEval = new LowLevelGenericEvaluator();
		//
		// Evaluator<HighLevelMap> amountEval = new HighLevelContentEvaluator(
		// "treasure", 3);
		// Evaluator<HighLevelMap> ratioEval = new HighLevelRatioEvaluator(
		// "monster", "treasure", 6, 8);
		//
		// DecimalFormat df = new DecimalFormat();
		// df.setMaximumFractionDigits(2);
		//
		// List<HighLevelMap> maps = Arrays.asList(hlMaps);
		// System.out.println("Original");
		// for (HighLevelMap m : maps)
		// System.out.println("\t" + m + " " + df.format(hgEval.eval(m)));
		// System.out.println();
		//
		// maps.sort((m1, m2) -> hgEval.eval(m1) < hgEval.eval(m2) ? 1 : -1);
		// System.out.println("Generic fitness");
		// for (HighLevelMap m : maps)
		// System.out.println("\t" + m + " " + df.format(hgEval.eval(m)));
		// System.out.println();
		//
		// maps.sort((m1, m2) -> amountEval.eval(m1) < amountEval.eval(m2) ? 1
		// : -1);
		// System.out.println("Treasure content = 3");
		// for (HighLevelMap m : maps)
		// System.out.println("\t" + m + " " + df.format(amountEval.eval(m)));
		// System.out.println();
		//
		// maps.sort((m1, m2) -> ratioEval.eval(m1) < ratioEval.eval(m2) ? 1 :
		// -1);
		// System.out.println("Treasure / Monster ratio = 0.75 (3/4)");
		// for (HighLevelMap m : maps)
		// System.out.println("\t" + m + " " + df.format(ratioEval.eval(m)));
		// System.out.println();
	}
	
	private static TreeMap<String, String[]> loadContent() {
		Properties prop = new Properties();
		TreeMap<String, String[]> content = new TreeMap<>();
		try {
			prop.load(Main.class.getClassLoader().getResourceAsStream(
					"content.properties"));

			for (Object key : prop.keySet()) {
				String value = prop.getProperty((String) key);
				content.put((String) key, value.split(","));
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return content;
	}
}
