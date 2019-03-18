# Automata

`nfa.initGraph("./src/test.txt");` // Khởi tạo graph từ file

`nfa.display();` // Hiển thị graph dưới dạng bảng trong console

`nfa.toFile("nfa.gv");` // Lưu graph vào file Graphviz

`dot -T png nfa.gv -O` // run file Graphviz (output là file png hiển thị graph)

Example: 
