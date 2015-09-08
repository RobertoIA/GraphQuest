package muia.tesis.map.data;

import grammar.Derivation;
import grammar.Grammar;
import grammar.GrammarException;

import java.util.List;
import java.util.TreeMap;

import muia.tesis.LowLevelGrammarLexer;
import muia.tesis.LowLevelGrammarParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Graph;

public class LowLevelMap {

	private int nNodes;
	private List<String> mainContents;
	private int[] nContent;
	private TreeMap<String, String[]> content;
	private String grammarRules;
	private Grammar grammar;
	private Derivation derivation;
	private Graph graph;
	private AStar pathPlanner;

	public LowLevelMap(int nNodes, List<String> mainContents, int[] nContent,
			TreeMap<String, String[]> content, String grammarRules,
			Grammar grammar, Derivation derivation) {
		this.nNodes = nNodes;
		this.mainContents = mainContents;
		this.nContent = nContent;
		this.content = content;
		this.grammarRules = grammarRules;
		this.grammar = grammar;
		this.derivation = derivation;

		this.graph = loadGraph();
		this.pathPlanner = new AStar(this.graph);
	}

	private Graph loadGraph() {
		LowLevelGrammarLexer lexer = new LowLevelGrammarLexer(
				new ANTLRInputStream(this.getWord()));
		LowLevelGrammarParser parser = new LowLevelGrammarParser(
				new CommonTokenStream(lexer));
		ParseTree tree = parser.map();

		ParseTreeWalker walker = new ParseTreeWalker();
		LowLevelMapLoader loader = new LowLevelMapLoader(this.mainContents);
		walker.walk(loader, tree);

		return loader.graph();
	}

	public void mutate() {
		try {
			this.derivation = this.derivation.mutate();
		} catch (GrammarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.graph = loadGraph();
	}

	public Derivation getDerivation() {
		return this.derivation;
	}

	public String getWord() {
		return this.derivation.getWord();
	}

	public Graph getGraph() {
		return this.graph;
	}

	public int getNodes() {
		return this.nNodes;
	}

	public List<String> getMainContents() {
		return this.mainContents;
	}

	public TreeMap<String, String[]> getContent() {
		return this.content;
	}

	public int[] getNContent() {
		return this.nContent;
	}

	public String getGrammarRules() {
		return this.grammarRules;
	}

	public Grammar getGrammar() {
		return this.grammar;
	}
	
	public AStar getPathPlanner() {
		return this.pathPlanner;
	}

	@Override
	public String toString() { // TODO:
		return "[LLMap] <#" + nNodes + " - " + this.getWord() + ">";
	}
}
