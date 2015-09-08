package muia.tesis.map.data;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import muia.tesis.LowLevelGrammarBaseListener;
import muia.tesis.LowLevelGrammarParser;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LowLevelMapLoader extends LowLevelGrammarBaseListener {

	private static final Logger log = LoggerFactory
			.getLogger(LowLevelMapLoader.class);

	private Graph graph;
	private List<String> mainContents;
	private int count = 0;
	private String style = "graph { padding: 50px; fill-color: rgb(240, 240, 240); }\n"
			+ "node.zone { fill-color: rgb(120, 240, 120); size: 20px, 20px; "
			+ "text-padding: 5px; text-style: bold; text-size: 15px; }"
			+ "node.room { stroke-mode: plain; stroke-width: 2px; shape: box; "
			+ "size: 120px, 120px; fill-mode: image-scaled; }";
	private boolean isBlock = false;
	private List<Integer> blockPositions;

	public LowLevelMapLoader(List<String> mainContents) {
		log.info("Processing low level map with {} main contents", mainContents);

		this.mainContents = mainContents;
		this.graph = new DefaultGraph("");
		this.graph.setAutoCreate(true);
		this.graph.setStrict(false);

		this.graph.addEdge("1-2", "1", "2");
		for (Node node : this.graph) {
			// node.addAttribute("ui.label", node.getId());
			node.setAttribute("cons", 1);
			node.setAttribute("ui.class", "room");
		}
		this.graph.addAttribute("ui.stylesheet", this.style);
	}

	public Graph graph() {
		return this.graph;
	}

	private String image(int rooms, List<String> content) {
		File temp = null;
		try {
			BufferedImage image = ImageIO.read(getClass().getResourceAsStream(
					"/base.png"));
			image = compose(image, "" + rooms);

			if (content != null) {
				for (String c : content)
					image = compose(image, c);
			}

			temp = File.createTempFile("" + image.hashCode(), ".png");
			ImageIO.write(image, "png", temp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp.getAbsolutePath();
	}

	private BufferedImage compose(BufferedImage base, String overlayFilename)
			throws IOException {
		BufferedImage overlay = ImageIO.read(getClass().getResourceAsStream(
				"/" + overlayFilename + ".png"));

		int w = Math.max(base.getWidth(), base.getWidth());
		int h = Math.max(base.getHeight(), base.getHeight());
		BufferedImage combined = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = combined.getGraphics();
		g.drawImage(base, 0, 0, null);
		g.drawImage(overlay, 0, 0, null);

		return combined;
	}

	@Override
	public void enterBin(LowLevelGrammarParser.BinContext ctx) {
		log.info("Entering bin {}", ctx.getText());
		String[] conns = ctx.getText().split("");
		int node = conns.length + 1;

		Node n = this.graph.addNode("" + node);
		n.setAttribute("ui.class", "room");

		int consCount = 0;
		for (int i = 0; i < conns.length; i++) {
			if (conns[i].equals("1")) {
				this.graph.addEdge((i + 1) + "-" + node, "" + (i + 1), ""
						+ node);
				Node otherNode = this.graph.getNode("" + (i + 1));
				otherNode.setAttribute("cons",
						(int) otherNode.getAttribute("cons") + 1);
				consCount++;
			}
		}
		this.graph.getNode("" + node).setAttribute("cons", consCount);
	}

	@Override
	public void enterBlock(LowLevelGrammarParser.BlockContext ctx) {
		log.info("Entering block {}", ctx.getText());
		this.isBlock = true;
		this.blockPositions = new ArrayList<>();
	}

	@Override
	public void enterDec(LowLevelGrammarParser.DecContext ctx) {
		log.info("Entering dec {} with block {}", ctx.getText(), this.isBlock);

		if (!this.isBlock) {
			Node node = this.graph.getNode(ctx.getText());
			String mContent = this.mainContents.get(this.count);
			if (!mContent.equals("key")) {
				String tag = "zone " + mContent;
				node.setAttribute("cons", (int) node.getAttribute("cons") + 1);
				node.setAttribute("zoneConn");
				this.graph.addEdge(ctx.getText() + "-" + tag, ctx.getText(),
						tag);
				Node zoneNode = this.graph.getNode(tag);
				zoneNode.setAttribute("ui.label", mContent);
				zoneNode.setAttribute("ui.class", "zone");
			} else {
				List<String> content = new ArrayList<>();
				if (node.getAttribute("content") != null)
					content.addAll(node.getAttribute("content"));
				content.add("k");
				node.setAttribute("content", content);
			}
		} else {
			this.blockPositions.add(Integer.parseInt(ctx.getText()));
		}
		this.count++;
	}

	@Override
	public void enterContent(LowLevelGrammarParser.ContentContext ctx) {
		if (ctx.ID() != null) {
			log.info("Entering content id {} with block {}", ctx.getText(),
					this.isBlock);

			Node node = this.graph.getNode("" + this.blockPositions.remove(0));
			List<String> content = new ArrayList<>();
			if (node.getAttribute("content") != null)
				content.addAll(node.getAttribute("content"));
			content.add(ctx.getText());
			node.setAttribute("content", content);
		}
	}

	@Override
	public void exitMap(LowLevelGrammarParser.MapContext ctx) {
		for (Node node : this.graph) {
			if (node.getAttribute("ui.class").equals("room")) {
				List<String> content = node.getAttribute("content");
				int rooms = node.getAttribute("cons");
				String img = image(rooms, content);

				log.info("Building node {} with {} connections, {} contents",
						node.getId(), rooms, content);

				node.setAttribute("ui.style", "fill-image: url('" + img + "');");
			}
		}
		log.info("Completed low level map graph with {} nodes", this.graph
				.getNodeSet().size());
	}
}
