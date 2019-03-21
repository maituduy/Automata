# Automata

`nfa.initGraph("./src/test.txt");` // Khởi tạo graph từ file

`nfa.display();` // Hiển thị graph dưới dạng bảng trong console

`nfa.toFile("nfa.gv");` // Lưu graph vào file Graphviz

`NFA dfa = nfa.toDFA();` // Tạo DFA từ NFA

`dot -T png nfa.gv -O` // run file Graphviz (output là file png hiển thị graph)

### Update Python code
Requirements:
  * graphviz==0.8.4
  * beautifultable==0.7.0

Run: `python nfatodfa.py --nfa_path=test.txt` 

Example: 

NFA
![NFA](https://github.com/maituduy/Automata/blob/master/Example/nfa.gv.png?raw=true)

DFA
![DFA](https://github.com/maituduy/Automata/blob/master/Example/dfa.gv.png?raw=true)
