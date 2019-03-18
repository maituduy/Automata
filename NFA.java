package automata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

public class NFA {
	private Set<String> states;
	private Set<String> alphabet;
	private String startState;
	private Set<String> endState;
	private Map<String, Map<String,Set<String>>> graph;
	
	public NFA() {
		this.graph = new TreeMap<String, Map<String, Set<String>>>();
		this.states = new HashSet<>();
		this.alphabet = new HashSet<>();
	}
	
	public NFA(Set<String> states, Set<String> alphabet, String startState, Set<String> endState,
			Map<String, Map<String, Set<String>>> graph) {
		super();
		this.states = states;
		this.alphabet = alphabet;
		this.startState = startState;
		this.endState = endState;
		this.graph = graph;
	}

	public Map<String, Map<String,Set<String>>> getDFA() {
		Map<String, Map<String, Set<String>>> dfaStates = new TreeMap<String, Map<String, Set<String>>>();
		Queue<Set<String>> queue = new LinkedList<>();
//		queue.add(this.getEclosure(this.startState));
		Set<String> init = new HashSet<String>();
		init.add(this.startState);
		queue.add(init);

		while (!queue.isEmpty()) {
			Set<String> set = queue.poll();
			Set<String> eclosure = this.getEclosure(set);
//			System.out.println(eclosure);
			for (String string : this.alphabet) {
				if (!string.equals("_")) {
//					Set<String> tmp = this.transition(set, string);
					Set<String> tmp = this.transition(eclosure, string);
					Map<String,Set<String>> map = new TreeMap<String,Set<String>>();
					if (dfaStates.containsKey(set.toString())) 
						map = dfaStates.get(set.toString());
					map.put(string, tmp);

					dfaStates.put(set.toString(), map);
					Set<String> newStates = dfaStates.get(set.toString()).get(string);
					if (!dfaStates.containsKey(newStates.toString())) {
						queue.add(newStates);
						
					}
						
				}		
			}
		}
		return dfaStates;
	}
	
	public Set<String> transition(Set<String> states, String symbol) {
		Set<String> res = new HashSet<String>();
		for (String state : states) {
			Set<String> targetStates = this.graph.get(state).get(symbol);
			if (targetStates != null)
				res.addAll(targetStates);
		}
		return res;
	}
	
	public void initGraph(String path) {
		try {
			List<String> list = Files.readAllLines(new File(path).toPath());
			for (int i = 0; i < list.size(); i++) {
				String line = list.get(i).trim();
				if (i==list.size()-1) {
					List<String> tmp = Arrays.asList(line.split(","));
					this.endState = new HashSet<String>(tmp);
				}
					
				else if (i == list.size()-2)
					this.startState = line;
				else {
					Map<String,Set<String>> temp = new TreeMap<String, Set<String>>();
					String[] str = line.split(" ");
					String state = str[0];
					this.states.add(state);
					String symbol = str[1];
					this.alphabet.add(symbol);
					Set<String> targetStates = new HashSet<>(Arrays.asList(str[2].split(",")));
					for (String string : targetStates) 
						this.states.add(string);
					temp.put(symbol, targetStates);
					
					if(!this.graph.containsKey(state)) 
						this.graph.put(state, temp);

					else {
						Map<String,Set<String>> tmp = this.graph.get(state);
						tmp.put(symbol, targetStates);
						this.graph.put(state, tmp);
					}
				}
					
			}
			for (String string : this.states)
				if (!this.graph.containsKey(string))
					this.graph.put(string,new TreeMap<String,Set<String>>());
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public Set<String> getEclosure(String state) {
		Set<String> eC = new HashSet<>();
		this.eClosure(state,eC);
		return eC;
	}
	
	public Set<String> getEclosure(Set<String> states) {
		if (states.contains(""))
			return states;
		Set<String> res = new HashSet<String>();
		for (String state : states) {
			Set<String> targetStates = this.getEclosure(state);
			if (targetStates != null)
				res.addAll(targetStates);
		}
		return res;
	}
	
	private void eClosure(String state, Set<String> temp) {
		if (temp.contains(state))
			return;
		temp.add(state);
		Set<String> tmp = this.graph.get(state).get("_");
		if (tmp == null) 
			return;
		else 
			for (String string : tmp) 
				eClosure(string, temp);
	}
	
	public void display() {
		System.out.print("States: ");
		for (String string : this.states) 
			System.out.print(string + " ");
		System.out.println();
		
		System.out.print("Alphabet: ");
		for (String string : this.alphabet) {
			System.out.print(string + " ");
		}
		this.displayGraph(this.graph);
		
//		System.out.println();
//		for (String string : this.states) 
//			System.out.println("E closure of " + string + ": "+ this.getEclosure(string));

	}
	
	public void displayGraph(Map<String, Map<String,Set<String>>> graph) {
		String temp = "States";
		for (String string : this.alphabet) 
			temp += "\t\t" + string;

		System.out.println("\n");
		
		System.out.println(temp);
		for (String string : graph.keySet()) {
			temp = string;
			for (String string2 : alphabet)
				temp += "\t\t"+ graph.get(string).get(string2);
			System.out.println(temp);
		}
	}
	
	public Map<String, Map<String,Set<String>>> string2Set(Map<String, Map<String,String>> graph) {
		Map<String, Map<String,Set<String>>> res = new TreeMap<String, Map<String,Set<String>>>();
		for (String string : graph.keySet()) {
			Map<String,Set<String>> map = new TreeMap<String,Set<String>>();
			for (String string2 : graph.get(string).keySet()) {
				Set<String> set = new HashSet<>();
				set.add(graph.get(string).get(string2));
				map.put(string2, set);
			}
			res.put(string, map);
		}
		return res;
	}
	
	public NFA toDFA() throws IOException {
		Map<String, Map<String,Set<String>>> dfa = this.getDFA();
//		System.out.println(dfa);
		Map<String,String> res = new TreeMap<>();
		res.put("Q0", "[" + this.startState + "]");
		int index = 1;
		List<String> endStates = new ArrayList<>();
		for (String key : dfa.keySet()) {
			for (String key2 : dfa.get(key).keySet()) 
				if (!res.containsValue(dfa.get(key).get(key2).toString())) 
					res.put("Q"+String.valueOf(index++), dfa.get(key).get(key2).toString());					
			
			// get EndStates
			String tmp = key.substring(1,key.length()-1);
			List<String> str = Arrays.asList(tmp.split(", "));
			Set<String> set = new HashSet<String>(str);
			set = this.getEclosure(set);
			for (String string : set) {
				if (this.endState.contains(string)) {
					endStates.add(key);
					break;
				}
			}
		}

		
		Map<String, Map<String,String>> dfa2 = new TreeMap<String,Map<String,String>>();
		dfa2.put("Q0",new TreeMap<String,String>());
		for (String key : dfa.keySet()) {
			String dfa2Key = null;
			for (String string : res.keySet()) {
				if (res.get(string).equals(key)) 
					dfa2Key = string;
			}
			for (String symbol : this.alphabet) {
				if (!symbol.equals("_")) {
					String symbolVal = dfa.get(key).get(symbol).toString();
//					System.out.println(symbolVal);
					for (String string : res.keySet()) {
						String val = res.get(string);
						if (val.equals(symbolVal)) {
							Map<String,String> map = new TreeMap<String,String>();
							if (dfa2.containsKey(dfa2Key))
								map = dfa2.get(dfa2Key);
							map.put(symbol, string);
							dfa2.put(dfa2Key, map);
						}
					}
				}
					
			}
			
		}
		
		Set<String> listEnd = new HashSet<String>();
		for (String string : endStates) {
			for (String string2 : res.keySet()) {
				if (string.equals(res.get(string2)))
					listEnd.add(string2);
			}
		}
		
		dfa = this.string2Set(dfa2);
		return new NFA(dfa.keySet(), this.alphabet, "Q0", listEnd, dfa);
	}
	
	public void toFile(String path) throws IOException {
		String res = "digraph G {\n\t\"\"[shape=none]\n\t";
		for (String string : this.endState) 
			res += string + " [shape=doublecircle]\n\t";
		res += "\n\t\"\" -> " + this.startState + "\n\t";
		for (String string : this.graph.keySet()) {
			for (String string2 : this.graph.get(string).keySet()) {
				for (String string3 : this.graph.get(string).get(string2)) {
					res += string + " -> " + string3 + " [label="+ string2 + "]\n\t";
				}
			}
		}
		res += "}";
		BufferedWriter writer = new BufferedWriter(new FileWriter(path));
	    writer.write(res);
	     
	    writer.close();
	}
	
	public Map<String, Map<String, Set<String>>> getGraph() {
		return graph;
	}
	
	public String getStartState() {
		return startState;
	}

	public Set<String> getEndState() {
		return endState;
	}

	public static void main(String[] args) throws IOException {
		NFA nfa = new NFA();
		nfa.initGraph("./src/test.txt");
		nfa.display();
		nfa.toFile("nfa.gv");
		NFA dfa = nfa.toDFA();
		dfa.display();
		dfa.toFile("dfa.gv");
	}
}
