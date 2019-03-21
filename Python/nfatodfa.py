import queue
from collections import deque
from graphviz import Digraph
import pprint
import argparse
import os
from beautifultable import BeautifulTable

class NFA():
    def __init__(self, graph={}):
        self.graph = graph
    
    def getGraphFromFile(self,path):
        graph = {}
        route = {}
        alphabet = set()
        states_name = set()
        
        lines = [line.rstrip('\n').rstrip(" ") for line in open(path)]
        graph["start"] = lines[-2]
        graph["end"] = lines[-1].split(",")
        for line in lines:
            tmp = line.split()
            if len(tmp)<3:
                continue
            begin_state = tmp[0]
            symbol = tmp[1]
            end_states = tmp[2].split(",")
            
            if begin_state in route:
                state = route[begin_state] 
                state[symbol] = end_states
                route[begin_state] = state
            else :
                route[begin_state] = {symbol : end_states}
                
            alphabet.add(symbol)
            states_name.add(begin_state)
            states_name.update(end_states)
            
        graph["alphabet"] = sorted(alphabet)
        graph["states_name"] = sorted(states_name)
        graph["route"] = route
        
        self.graph = graph
    
    def transition(self, states, symbol):
        route = self.graph["route"]
        end_states = set()
        for state in states:
            if state in route:
                if symbol in route[state]:
                    end_states.update(route[state][symbol])
            
        return sorted(end_states)
    
    def getEclosure(self, states):
        route = self.graph["route"]
                           
        if (type(states)) != list:
            states = [states]
        my_queue = queue.Queue()
        target = [my_queue.put(state) for state in states]
        target = []
        while my_queue.empty() == False:
            state = my_queue.get()
            target.append(state)
            if state in route:
                if "_" in route[state]:
                    end_e = route[state]["_"]
                    tmp = [my_queue.put(state) for state in end_e if state not in target]
            
        return sorted(target)
    
    def toDFA(self):
        graph = self.graph
        route = graph["route"]
        
        my_queue = queue.Queue()
        my_queue.put([graph["start"]])
        res = []
        ecs = []
        new_graphs = {"start" : "Q0", "alphabet" : [symbol for symbol in graph["alphabet"] if symbol != "_"]}
        new_route = {}
        index = 0
        while my_queue.empty() == False:
            states = my_queue.get()
            res.append(states)
            eclosure = self.getEclosure(states)
            ecs.append(eclosure)
            
            for symbol in graph["alphabet"]:
                if symbol != "_":
                    target = self.transition(eclosure, symbol)
                    if str(states) not in new_route:
                        new_route[str(states)] = {symbol : target}
                    else:
                        end_states = new_route[str(states)]
                        end_states[symbol] = target
                        new_route[str(states)] = end_states
                   
                    if str(target) not in new_route:
                        new_route[str(target)] = {}
                        my_queue.put(target)
        end = []
        for i,ec in enumerate(ecs):
            for _ in graph["end"]:
                if _ in ec:
                    end.append("Q"+str(i))
        new_graphs["end"] = end
        new_graphs["states_name"] = ["Q"+str(i) for i in range(len(res))]
        tmp = dict(zip(new_route.keys() ,new_graphs["states_name"]))
        dfa = NFA(new_graphs)
        route2 = {}
        index = 0
        for k, v in new_route.items():
            t = {}
            for x, y in v.items():
                t[x] = tmp[str(new_route[k][x])]
            route2[new_graphs["states_name"][index]] = t
            index += 1
        new_graphs["route"] = route2
        return NFA(new_graphs)
    
    def printGraph(self):
        pp = pprint.PrettyPrinter()
        pp.pprint(self.graph)
    
    def printStateTable(self):
        table = BeautifulTable()
        alphabet = self.graph["alphabet"]
        table.column_headers = ["State"] + alphabet
        for k, v in self.graph["route"].items():
            row = [k]
            for symbol in alphabet:
                if symbol in v:
                    row.append(v[symbol])
                else:
                    row.append("_")
            table.append_row(row)
        
        print(table)
    
    def render(self, path, format="png"):
        dot = Digraph()
        dot.node("",shape="none")
        for end in self.graph["end"]:
            dot.node(end,shape="doublecircle")
        
        dot.edge("",self.graph["start"])
        route = self.graph["route"]
        for k, v in route.items():
            for x, y in v.items():
                if type(y) != list:
                    y = [y]
                for state in y:
                    dot.edge(k,state,label=x)
        dot.format = format
        dot.render(path)
        
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--nfa_path", required=True)
    
    args = vars(parser.parse_args())
    output_dir = "output/"
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)
    nfa = NFA()
    nfa.getGraphFromFile(args["nfa_path"])
    nfa.printGraph()
    nfa.printStateTable()
    nfa.render(output_dir+"nfa")
    dfa = nfa.toDFA()
    dfa.printGraph()
    dfa.printStateTable()
    dfa.render(output_dir+"dfa")