package muia.tesis.gen.eval;

public interface Evaluator<T> {
	public double eval(T instance);
	public void printFitness(T instance);
}
