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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import muia.tesis.map.data.LowLevelMap;

public class LowLevelBuilder implements Builder<LowLevelMap> {
	private int nNodes;
	private List<String> mainContents;
	private int[] nContent;
	private TreeMap<String, String[]> content;
	private String grammarRules;
	private Grammar grammar;

	public LowLevelBuilder(int nNodes, List<String> mainContents,
			int[] nContent, TreeMap<String, String[]> content) {
		this.nNodes = nNodes;
		this.mainContents = mainContents;
		this.nContent = nContent;
		this.content = content;
		this.grammarRules = rules();
		this.grammar = grammar(this.grammarRules);
	}

	public LowLevelMap build() {
		Derivation derivation = derivation(this.grammar);
		return new LowLevelMap(this.nNodes, this.mainContents, this.nContent,
				this.content, this.grammarRules, this.grammar, derivation);
	}

	public LowLevelMap[] build(int n) {
		LowLevelMap[] maps = new LowLevelMap[n];
		for (int i = 0; i < n; i++)
			maps[i] = build();
		return maps;
	}

	public LowLevelMap[] crossover(LowLevelMap a, LowLevelMap b)
			throws GrammarException {
		// TODO: check compatibility
		LowLevelMap[] crossover = new LowLevelMap[2];
		List<Derivation> derivations = a.getDerivation().crossoverWX(
				b.getDerivation());
		for (int i = 0; i < 2; i++)
			crossover[i] = new LowLevelMap(this.nNodes, this.mainContents,
					this.nContent, this.content, this.grammarRules,
					this.grammar, derivations.get(i));
		return crossover;
	}

	public LowLevelMap mutate(LowLevelMap original) throws GrammarException {
		// TODO: check compatibility
		return new LowLevelMap(this.nNodes, this.mainContents, this.nContent,
				this.content, this.grammarRules, this.grammar, original
						.getDerivation().mutate());
	}

	private String rules() {
		StringBuilder grammar = new StringBuilder();
		grammar.append("#A# S" + "\n"); // Axiom

		grammar.append("#N# R Z "); // Non-terminals
		for (int i = 3; i <= nNodes; i++)
			grammar.append("R" + i + " ");
		int count = 0;
		for (String key : content.keySet()) {
			if (nContent[count] > 0) grammar.append(key + "s " + key + " ");
			count++;
		}
		grammar.append("\n");

		grammar.append("#T# ; : - 0 1"); // Terminals
		for (int i = 2; nNodes > 1 && i <= nNodes; i++)
			grammar.append(" " + i);
		count = 0;
		for (String key : content.keySet()) {
			if (nContent[count] > 0)
				grammar.append(" " + String.join(" ", content.get(key)));
			count++;
		}
		grammar.append("\n");

		grammar.append("S ::= "); // P - Axiom
		if (nNodes > 2) grammar.append("R ");
		if (this.mainContents.size() > 0) grammar.append("; Z ");
		count = 0;
		for (String key : content.keySet()) {
			if (nContent[count] > 0) grammar.append("; " + key + "s ");
			count++;
		}
		grammar.append("\n");

		if (nNodes > 2) { // P - Room connections
			grammar.append("R ::= ");
			for (int i = 3; i <= nNodes; i++) {
				grammar.append("R" + i);
				if (i != nNodes) grammar.append(" : ");
			}
			grammar.append("\n");

			for (int i = 3; i <= nNodes; i++) {
				grammar.append("R" + i + " ::= ");

				List<String> connections = roomConnections(i);
				for (int j = 0; j < connections.size(); j++) {
					grammar.append(String.join(" ",
							String.join(" ", connections.get(j).split(""))));
					if (j != connections.size() - 1) grammar.append(" | ");
				}
				grammar.append("\n");
			}
		}

		if (this.mainContents.size() > 0) { // P - Main Contents
			grammar.append("Z ::= ");

			List<String> combs = new ArrayList<>();
			List<Integer> conns = new ArrayList<>();
			for (int i = 1; i <= nNodes; i++)
				conns.add(i);
			for (List<String> comb : combinations(conns,
					this.mainContents.size(), true)) {
				combs.add(String.join(" : ", comb));
			}
			grammar.append(String.join((char) 32 + "|" + (char) 32, combs)
					+ "\n");
		}

		count = 0;
		for (String key : content.keySet()) { // P - Contents
			if (nContent[count] > 0) {
				grammar.append(key + "s ::= ");

				List<String> combs = new ArrayList<>();
				List<Integer> conns = new ArrayList<>();
				for (int i = 1; i <= nNodes; i++)
					conns.add(i);
				for (List<String> comb : combinations(conns, nContent[count],
						false)) {
					String cont = String.join(" : ", comb);
					cont += " ;";
					for (int i = 1; i <= nContent[count]; i++) {
						cont += " " + key;
						if (i != nContent[count]) cont += " :";
					}
					combs.add(cont);
				}
				grammar.append(String.join((char) 32 + "|" + (char) 32, combs));
				grammar.append("\n");

				grammar.append(key + " ::= "); // P - Contents (term)
				grammar.append(String.join((char) 32 + "|" + (char) 32,
						content.get(key)));
				grammar.append("\n");
			}

			count++;
		}
		
//		System.out.println(grammar.toString());
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

	private static List<List<String>> combinations(List<Integer> n, int k,
			boolean repeat) {
		if (repeat) {
			return repeating_combinations(n, k);
		} else {
			List<List<String>> comb = new ArrayList<>();
			for (Set<String> s : unique_combinations(n, k)) {
				comb.add(new ArrayList<>(s));
			}
			return comb;
		}
	}

	private static List<List<String>> repeating_combinations(List<Integer> n,
			int k) {
		List<List<String>> comb = new ArrayList<>();
		if (k == 1) {
			for (int i : n)
				comb.add(Arrays.asList("" + i));
			return comb;
		}
		for (int i : n) {
			List<List<String>> combs;
			combs = repeating_combinations(n, k - 1);
			for (List<String> c : combs) {
				c = new ArrayList<>(c);
				c.add("" + i);
				comb.add(c);
			}
		}
		return comb;
	}

	private static Set<Set<String>> unique_combinations(List<Integer> n, int k) {
		Set<Set<String>> comb = new HashSet<>();
		if (k == 1) {
			for (int i : n)
				comb.add(new HashSet<>(Arrays.asList("" + i)));
			return comb;
		}
		for (int i : n) {
			Set<Set<String>> combs;
			List<Integer> subN = new ArrayList<>(n);
			subN.remove(subN.indexOf(i));
			combs = unique_combinations(subN, k - 1);
			for (Set<String> c : combs) {
				c = new HashSet<>(c);
				c.add("" + i);
				comb.add(c);
			}
		}
		return comb;
	}

	private static List<String> roomConnections(int n) {
		List<String> connections = new ArrayList<>();

		for (int bin = 1; bin < Math.pow(2, n - 1); bin++) {
			String conn = Integer.toBinaryString(bin);
			while (conn.length() < n - 1)
				conn = "0" + conn;
			connections.add(conn);
		}
		return connections;
	}
}
