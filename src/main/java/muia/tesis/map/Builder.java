package muia.tesis.map;

import grammar.GrammarException;

public interface Builder<T> {
	public T build();

	public T[] build(int n);

	public T[] crossover(T a, T b) throws GrammarException;

	public T mutate(T original) throws GrammarException;
}
