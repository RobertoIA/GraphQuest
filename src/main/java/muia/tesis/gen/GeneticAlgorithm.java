package muia.tesis.gen;

import grammar.GrammarException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import muia.tesis.gen.eval.Evaluator;
import muia.tesis.gen.eval.HighLevelGenericEvaluator;
import muia.tesis.gen.eval.LowLevelGenericEvaluator;
import muia.tesis.map.Builder;
import muia.tesis.map.HighLevelBuilder;
import muia.tesis.map.LowLevelBuilder;
import muia.tesis.map.data.CompositeMap;
import muia.tesis.map.data.HighLevelMap;
import muia.tesis.map.data.LowLevelMap;

public class GeneticAlgorithm {

	private TreeMap<String, String[]> content;
	private int[] rooms;
	List<Evaluator<HighLevelMap>> evaluators;
	private Random random;

	public GeneticAlgorithm(int[] rooms,
			List<Evaluator<HighLevelMap>> evaluators,
			TreeMap<String, String[]> content) {
		this.rooms = rooms;
		this.evaluators = evaluators;
		this.evaluators.add(new HighLevelGenericEvaluator());
		this.content = content;
		this.random = new Random(System.currentTimeMillis());
	}

	public CompositeMap run(Options hlOptions, Options llOptions)
			throws GrammarException {
		Builder<HighLevelMap> hlBuilder = new HighLevelBuilder(this.rooms,
				this.content);
		HighLevelMap hlMap = optimize(hlOptions, hlBuilder, this.evaluators);

		LowLevelMap[] llMaps = new LowLevelMap[rooms.length];

		int[][] contentCounts = hlMap.getContentCounts();
		List<Evaluator<LowLevelMap>> ev = Arrays
				.asList(new LowLevelGenericEvaluator());
		Builder<LowLevelMap> builder;
		for (int i = 0; i < rooms.length; i++) {
			builder = new LowLevelBuilder(rooms[i], hlMap.getMainContent(i),
					contentCounts[i], this.content);
			llMaps[i] = optimize(llOptions, builder, ev);
		}

		return new CompositeMap(hlMap, llMaps);
	}

	private <T> T optimize(Options opt, Builder<T> builder,
			List<Evaluator<T>> evs) throws GrammarException {

		List<T> population = new ArrayList<>();
		population.addAll(Arrays.asList(builder.build(opt.population())));
		population.sort((a, b) -> eval(a, evs) <= eval(b, evs) ? 1 : -1);

		int conv = 0;
		T best = population.get(0);
		List<T> newPop = new ArrayList<>();

		while (conv < opt.convergence()) {
			// migration
			newPop.addAll(Arrays.asList(builder.build(opt.migration())));
			// mutation
			T mutant;
			for (int i = 0; i < opt.mutants(); i++) {
				mutant = population.get(random.nextInt(population.size()));
				// newPop.add(hlBuilder.mutate(mutant));
				mutant = builder.mutate(mutant);
			}
			// crossover
			for (int i = 0; i < opt.crossover(); i++)
				newPop.addAll(Arrays.asList(builder.crossover(
						tournament(population, evs),
						tournament(population, evs))));

			population.addAll(newPop);
			newPop.clear();
			population.sort((a, b) -> eval(a, evs) <= eval(b, evs) ? 1 : -1);
			population = population.subList(0, opt.population());

			// System.err
			// .println("[" + conv + "] " + eval(best, evs) + " " + best);

			if (best != population.get(0)) {
				conv = 0;
				best = population.get(0);
			} else {
				conv++;
			}
		}

		System.out.println(eval(best, evs) + " " + best);
		for (Evaluator<T> e : evs)
			e.printFitness(best);
		return best;
	}

	private <T> double eval(T instance, List<Evaluator<T>> evaluators) {
		double fitness = 0.0;
		for (Evaluator<T> e : evaluators)
			fitness += e.eval(instance);
		return fitness;
	}

	private <T> T tournament(List<T> population, List<Evaluator<T>> evs) {
		T a = population.get(random.nextInt(population.size()));
		T b = population.get(random.nextInt(population.size()));
		double evala = eval(a, evs);
		double evalb = eval(b, evs);

		if (random.nextDouble() < evala / (evala + evalb)) return a;
		return b;
	}
}
