package muia.tesis.map.data;

import grammar.Derivation;
import grammar.Grammar;

import java.util.List;
import java.util.TreeMap;

import muia.tesis.HighLevelGrammarLexer;
import muia.tesis.HighLevelGrammarParser;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Graph;

public class HighLevelMap {

	private int nNodes;
	private int[] rooms;
	private TreeMap<String, String[]> content;
	private String grammarRules;
	private Grammar grammar;
	private Derivation derivation;
	private Graph graph;
	private int[][] contentCounts;
	private List<List<String>> mainContents;
	private AStar pathPlanner;

	public HighLevelMap(int nNodes, int[] rooms,
			TreeMap<String, String[]> content, String grammarRules,
			Grammar grammar, Derivation derivation) {
		this.nNodes = nNodes;
		this.rooms = rooms;
		this.content = content;
		this.grammarRules = grammarRules;
		this.grammar = grammar;
		this.derivation = derivation;

		HighLevelGrammarLexer lexer = new HighLevelGrammarLexer(
				new ANTLRInputStream(derivation.getWord()));
		HighLevelGrammarParser parser = new HighLevelGrammarParser(
				new CommonTokenStream(lexer));
		ParseTree tree = parser.map();

		ParseTreeWalker walker = new ParseTreeWalker();
		HighLevelMapLoader loader = new HighLevelMapLoader(content);
		walker.walk(loader, tree);

		this.graph = loader.graph();
		this.contentCounts = loader.contentCounts();
		this.mainContents = loader.mainContents();
		this.pathPlanner = new AStar(this.graph);
	}

	public int[] getRooms() {
		return this.rooms;
	}

	public TreeMap<String, String[]> getContent() {
		return this.content;
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

	public int[][] getContentCounts() {
		return this.contentCounts;
	}

	public List<String> getMainContent(int i) {
		return this.mainContents.get(i);
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
		return "[HLMap] <#" + nNodes + " - " + this.getWord() + ">";
	}
}
