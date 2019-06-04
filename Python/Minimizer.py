from Automata import Automata
import pprint

class Minimizer():

    def __init__(self, dfa):
        self.dfa = dfa
        self.table = {}
    
    def minimization(self):
        states = self.dfa.graph['states_name']
        end_states = self.dfa.graph['end']
        n_states = len(states)
        table = {}
        for i in range(1,n_states):
            status = {}
            for j in range(i):
                if any([states[i] in end_states and states[j] not in end_states,
                        states[j] in end_states and states[i] not in end_states]):
                    status[states[j]] = 1
                else:
                    status[states[j]] = 0
            table[states[i]] = status
        
        # print("iter1 table")
        # self.print_table()
        table = self.fill_table(table)
        # print("Last iter table")
        # self.print_table()
        new_states = self.get_new_states(table)
        new_states = self.combine_states(new_states)
        graph = self.create_graph(new_states)
        return Automata(graph)

    def create_graph(self, states):
        begin_state = self.dfa.graph['start']
        end_state = self.dfa.graph['end']
        graph = {'alphabet': self.dfa.graph['alphabet']}
        route = {}
        dfa_route = self.dfa.graph['route']
        new_states = ["Q"+str(i) for i in range(len(states))]
        new_start_state = [x for x in states if begin_state in x][0]
        new_end_state = [x for x in states if set(x).issubset(end_state)]
        for i, state in enumerate(states):
            for symbol in self.dfa.graph['alphabet']:
                out_state = dfa_route[state[0]][symbol]
                for s in states:
                    if out_state in s:
                        out_mi_state = s
                        break
                index_start = states.index(state)
                index_end = states.index(out_mi_state)
                if new_states[index_start] not in route:
                    route[new_states[index_start]] = {symbol : new_states[index_end]}
                else :
                    temp = route[new_states[index_start]]
                    temp[symbol] = new_states[index_end]
                    route[new_states[index_start]] = temp

        graph['route'] = route
        graph['states_name'] = new_states
        graph['start'] = new_states[states.index(new_start_state)]
        out_indexes = [states.index(x) for x in new_end_state]
        graph['end'] = [new_states[i] for i in out_indexes]
        return graph

    def fill_table(self, table):
        import copy

        while True:
            temp = copy.deepcopy(table)
            for k, v in table.items():
                for k1, v1 in v.items():
                    if v1 == 0:
                        for symbol in self.dfa.graph['alphabet']:
                            state1 = self.dfa.graph['route'][k][symbol]
                            state2 = self.dfa.graph['route'][k1][symbol]
                            val_state1 = state1.split("Q")[1]
                            val_state2 = state2.split("Q")[1]
                            if val_state1 < val_state2:
                                state1, state2 = state2, state1
                            if table[state1].get(state2) == 1:
                                table[k][k1] = 1
                                break
            
            if temp == table:
                break
        self.table = table
        return table

    def get_new_states(self, table):
        s = set()
        new_states = []
        all_states = self.dfa.graph['states_name']

        for k, v in table.items():
            for k1, v1 in v.items():
                if v1 == 0:
                    new_states.append([k,k1])
                    s.add(k)
                    s.add(k1)       
        for state in all_states:
            if state not in s:
                new_states.append([state])

        return new_states

    def combine_states(self, states):
        new_states = []
        for i in range(len(states)-1):
            if len(states[i]) == 2:
                for j in range(i+1,len(states)):
                    if len(states[j]) == 2:
                        if len(set(states[i]).intersection(states[j])) > 0:
                            new_states.append(list(set(states[i]+states[j])))
                        else:
                            new_states.append(states[i])
                    else:
                        new_states.append(states[i])
            else:
                new_states.append(states[i])
        if len(states[len(states)-1]) == 1:
            new_states.append(states[len(states)-1])
        
        unique_states = []
        for state in new_states:
            if sorted(state) not in unique_states:
                unique_states.append(sorted(state))
        return unique_states

    def print_table(self):
        pp = pprint.PrettyPrinter()
        pp.pprint(self.table)

if __name__ == "__main__":
    nfa = Automata()
    nfa.getGraphFromFile("test.txt")
    dfa = nfa.toDFA()
    minimizer = Minimizer(dfa)
    mdfa = minimizer.minimization()
    # minimizer.print_table()
    mdfa.printStateTable()
    mdfa.printGraph()
    # mdfa.render(path)

    