package muia.tesis.gen;

public class Options {

	// Population size
	private int population;
	// Iterations without improvement to finish
	private int convergence;
	// New / random individuals per iteration
	private int migration;
	// New individuals produced by mutation
	private int mutants;
	// New individuals produced by crossover (x2)
	private int crossover;

	public Options(int population, int convergence, int migration, int mutants,
			int crossover) {
		this.population = population;
		this.convergence = convergence;
		this.migration = migration;
		this.mutants = mutants;
		this.crossover = crossover;
	}

	public int population() {
		return this.population;
	}

	public int convergence() {
		return this.convergence;
	}

	public int migration() {
		return this.migration;
	}

	public int mutants() {
		return this.mutants;
	}

	public int crossover() {
		return this.crossover;
	}
}
