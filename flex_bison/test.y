%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
extern int yylex();
extern int yyparse();
extern FILE* yyin;
void yyerror(const char* s);
%}

%union {
	int ival;
}

%token<ival> T_POSITIVE
%token T_COMMA T_NEGATIVE T_LEFT T_RIGHT T_NEWLINE T_QUIT T_WS

%type<ival> arr cmi

%start calculation

%%

calculation:
	   | calculation line
;

line: T_LEFT  arr T_RIGHT T_NEWLINE { printf("\tResult: %d\n", $2);}
    | T_QUIT T_NEWLINE { printf("bye!\n"); exit(0); }
;

arr: {$$ = 0;}
    | T_WS { $$ = 0;}
    | T_POSITIVE cmi { $$ = $1 + $2; }
    | T_NEGATIVE cmi { $$ = $2; }
;

cmi: { $$ = 0; }
    | T_COMMA T_POSITIVE cmi { $$ = $2 + $3;}
    | T_COMMA T_NEGATIVE cmi { $$ = $3; }
;


%%
 
int main() {
//     FILE *myfile = fopen("test.file", "r");
//     
//     if (!myfile) {
//         printf("Can't open test.file");
//         return -1;
//     }
//     
//     yyin = myfile;
    yyin = stdin;
	do {
		yyparse();
	} while(!feof(yyin));
	return 0;
}

void yyerror(const char* s) {
	fprintf(stderr, "Parse error: %s\n", s);
	exit(1);
}
