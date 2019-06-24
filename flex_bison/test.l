%option noyywrap

%{
#include <stdio.h>

#define YY_DECL int yylex()

#include "test.tab.h"
#include <string.h>
%}

%%

[[:blank:]\r]+ {return T_WS;}
(-)(--)*[1-9][0-9]* {return T_NEGATIVE;}
(--)*(0|[1-9][0-9]*) {
                        char c = '-';
                        char *pr = yytext, *pw = yytext;
                        while (*pr) {
                            *pw = *pr++;
                            pw += (*pw != c);
                        }
                        *pw = '\0';
                        yylval.ival= atoi(yytext); 
                        return T_POSITIVE;
                    }
","     {return T_COMMA;}
\n		{return T_NEWLINE;}
"("		{return T_LEFT;}
")"		{return T_RIGHT;}
"exit"		{return T_QUIT;}
. {return *yytext;}

%%
