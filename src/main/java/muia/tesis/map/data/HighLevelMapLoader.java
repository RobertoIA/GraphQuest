package muia.tesis.map.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import muia.tesis.HighLevelGrammarBaseListener;
import muia.tesis.HighLevelGrammarParser;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HighLevelMapLoader extends HighLevelGrammarBaseListener {
	private static final Logger log = LoggerFactory
			.getLogger(HighLevelMapLoader.class);

	private Graph graph;
	private TreeMap<String, String[]> content;
	private int count = 0;
	private int[][] contentCounts;
	private List<List<String>> mainContents;
	private String style = "graph { padding: 50px; fill-color: rgb(240, 240, 240); }\n"
			+ "edge { size: 3px; }\n"
			+ "node { stroke-mode: plain; stroke-width: 3px;"
			+ "fill-color: rgb(240, 240, 240); shape: rounded-box; size-mode: fit;"
			+ "padding: 10px; text-padding: 2px; text-style: bold; text-size: 16px;"
			+ "shadow-mode: gradient-radial; }";
	static String[] colors = { "red", "green", "blue", "cyan", "magenta",
			"yellow", "aquamarine" };
	
	public HighLevelMapLoader(TreeMap<String, String[]> content) {
		log.info("Processing high level map");
		
		this.content = content;

		this.graph = new SingleGraph("");
		this.graph.setAutoCreate(true);
		this.graph.setStrict(false);

		this.graph.addEdge("1-2", "1", "2");
		this.graph.getNode("1").setAttribute("cons", "*,2");
		this.graph.getNode("2").setAttribute("cons", "1");

		this.graph.addAttribute("ui.stylesheet", this.style);
	}

	public Graph graph() {
		return this.graph;
	}
	
	public int[][] contentCounts() {
		return this.contentCounts;
	}
	
	public List<List<String>> mainContents() {
		return this.mainContents;
	}

	@Override
	public void enterConnect(HighLevelGrammarParser.ConnectContext ctx) {
		this.graph.addEdge(ctx.getText() + "-" + (this.count + 1),
				ctx.getText(), "" + (this.count + 1));

		Node thisNode = this.graph.getNode("" + (this.count + 1));
		Node otherNode = this.graph.getNode(ctx.getText());

		thisNode.setAttribute("cons", otherNode.getId());
		otherNode.setAttribute("cons", otherNode.getAttribute("cons") + ","
				+ thisNode.getId());
	}

	@Override
	public void enterContents(HighLevelGrammarParser.ContentsContext ctx) {
		int[] cont = new int[ctx.cont().size()];
		for (int i = 0; i < ctx.cont().size(); i++)
			cont[i] = Integer.parseInt(ctx.cont().get(i).getText());

		this.graph.getNode("" + (this.count + 1)).setAttribute("cont", cont);
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		if (node.getText().equals(";"))
			this.count++;
	}

	@Override
	public void exitMap(HighLevelGrammarParser.MapContext ctx) {
		log.info("Completed high level map graph with {} nodes", this.graph
				.getNodeSet().size());

		this.contentCounts = new int[this.graph.getNodeCount()][this.content.size()];
		this.mainContents = new ArrayList<>();
		for (int i = 0; i < this.graph.getNodeCount(); i++) {
			Node node = this.graph.getNode(i);
			node.addAttribute("ui.label", node.getId());
			node.setAttribute("ui.style", "stroke-color:"
					+ colors[i % colors.length] + ";");

			List<String> mCont = new ArrayList<String>();
			mCont.addAll(Arrays.asList(((String) node.getAttribute("cons"))
					.split(",")));
			if (i != this.graph.getNodeCount() - 1)
				mCont.add("key");
			int[] cont = node.getAttribute("cont");
			this.contentCounts[i] = cont;
			
			this.mainContents.add(mCont);
		}
	}
}
