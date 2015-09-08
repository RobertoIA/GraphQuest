package muia.tesis.map;

import grammar.Derivation;
import grammar.Grammar;
import grammar.GrammarException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import muia.tesis.map.data.HighLevelMap;

public class HighLevelBuilder implements Builder<HighLevelMap> {
	private int nNodes;
	private int[] rooms;
	private TreeMap<String, String[]> content;
	private String grammarRules;
	private Grammar grammar;

	public HighLevelBuilder(int[] rooms, TreeMap<String, String[]> content) {
		this.nNodes = rooms.length;
		this.rooms = rooms;
		this.content = content;
		this.grammarRules = rules();
		this.grammar = grammar(this.grammarRules);
	}

	public HighLevelMap build() {
		Derivation derivation = derivation(this.grammar);
		return new HighLevelMap(this.nNodes, this.rooms, this.content,
				this.grammarRules, this.grammar, derivation);
	}

	public HighLevelMap[] build(int n) {
		HighLevelMap[] maps = new HighLevelMap[n];
		for (int i = 0; i < n; i++)
			maps[i] = build();
		return maps;
	}

	public HighLevelMap[] crossover(HighLevelMap a, HighLevelMap b)
			throws GrammarException {
		// TODO: check compatibility
		HighLevelMap[] crossover = new HighLevelMap[2];
		List<Derivation> derivations = a.getDerivation().crossoverWX(
				b.getDerivation());
		for (int i = 0; i < 2; i++)
			crossover[i] = new HighLevelMap(this.nNodes, this.rooms,
					this.content, this.grammarRules, this.grammar,
					derivations.get(i));
		return crossover;
	}

	public HighLevelMap mutate(HighLevelMap original) throws GrammarException {
		// TODO: check compatibility
		return new HighLevelMap(this.nNodes, this.rooms, this.content,
				this.grammarRules, this.grammar, original.getDerivation()
						.mutate());
	}

	private String rules() {
		StringBuilder grammar = new StringBuilder();
		grammar.append("#A# S" + "\n"); // Axiom

		grammar.append("#N# C1 C2 "); // Non-terminals
		for (int i = 3; i <= nNodes; i++)
			grammar.append("Z" + i + " C" + i + " ");
		grammar.append("\n");

		grammar.append("#T# : ; "); // Terminals
		for (int i = 0; i < 10; i++) {
			grammar.append(i);
			if (i != 9) grammar.append(" ");
			else grammar.append("\n");
		}

		grammar.append("S ::= "); // P - Axiom
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < this.content.size(); j++) {
				grammar.append("C" + (i + 1) + " ");
				if (j != this.content.size() - 1) grammar.append(": ");
			}
			if (i == 0) grammar.append("; ");
		}
		if (nNodes > 2) grammar.append("; ");

		for (int i = 3; i <= nNodes; i++) {
			grammar.append("Z" + i + " : ");
			for (int j = 0; j < this.content.size(); j++) {
				grammar.append("C" + i + " ");
				if (j != this.content.size() - 1) grammar.append(": ");
			}
			if (i < nNodes) grammar.append("; ");
		}
		grammar.append("\n");

		for (int i = 3; i <= nNodes; i++) { // P - Zone connections
			grammar.append("Z" + i + " ::= ");
			for (int j = 1; j < i; j++) {
				grammar.append(j + " ");
				if (j != i - 1) grammar.append("| ");
			}
			grammar.append("\n");
		}

		for (int i = 1; i <= nNodes; i++) {
			grammar.append("C" + i + " ::= ");
			for (int j = 0; j <= rooms[i - 1]; j++) {
				grammar.append(j + " ");
				if (j != rooms[i - 1]) grammar.append("| ");
			}
			grammar.append("\n");
		}

		//System.out.println(grammar);
		return grammar.toString();
	}

	private Grammar grammar(String rules) {
		Grammar grammar = null;
		try {
			File temp = File.createTempFile("grammar", ".gr");
			BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
			writer.write(rules);
			writer.close();

			grammar = new Grammar(new BufferedReader(new FileReader(temp)));
		} catch (IOException | GrammarException e) {
			e.printStackTrace();
		}
		return grammar;
	}

	private static Derivation derivation(Grammar grammar) {
		Derivation derivation = null;
		try {
			derivation = new Derivation(grammar, 10); // TODO: why 10?
		} catch (GrammarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return derivation;
	}
}
