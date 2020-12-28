package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\f" 	{ }

"program"   { return new_symbol(sym.PROG, yytext()); }
"print" 	{ return new_symbol(sym.PRINT, yytext()); }
"return" 	{ return new_symbol(sym.RETURN, yytext()); }
"void" 		{ return new_symbol(sym.VOID, yytext()); }
"else"      { return new_symbol(sym.ELSE, yytext()); }
"if"        { return new_symbol(sym.IF, yytext()); }
"break"     { return new_symbol(sym.BREAK, yytext()); }
"class"     { return new_symbol(sym.CLASS, yytext()); }
"abstract"  { return new_symbol(sym.ABSTRACT, yytext()); }
"const"     { return new_symbol(sym.CONST, yytext()); }
"new"       { return new_symbol(sym.NEW, yytext()); }
"read"      { return new_symbol(sym.READ, yytext()); }
"for"       { return new_symbol(sym.FOR, yytext()); }
"extends"   { return new_symbol(sym.EXTENDS, yytext()); }
"continue"  { return new_symbol(sym.CONTINUE, yytext()); }

<YYINITIAL> "+" 		{ return new_symbol(sym.PLUS, yytext()); }
"-" 		{ return new_symbol(sym.MINUS, yytext()); }
"*" 		{ return new_symbol(sym.MUL, yytext()); }
"/" 		{ return new_symbol(sym.DIV, yytext()); }
"%" 		{ return new_symbol(sym.MOD, yytext()); }
"=" 		{ return new_symbol(sym.EQUAL, yytext()); }
"++" 		{ return new_symbol(sym.INC, yytext()); }
"--" 		{ return new_symbol(sym.DEC, yytext()); }
"." 		{ return new_symbol(sym.DOT, yytext()); }
"+="		{ return new_symbol(sym.PLUSEQUAL, yytext()); }
"-="		{ return new_symbol(sym.MINUSEQUAL, yytext()); }
"*="		{ return new_symbol(sym.MULEQUAL, yytext()); }
"/="		{ return new_symbol(sym.DIVEQUAL, yytext()); }
"%="		{ return new_symbol(sym.MODEQUAL, yytext()); }

";" 		{ return new_symbol(sym.SEMI, yytext()); }
"," 		{ return new_symbol(sym.COMMA, yytext()); }
"(" 		{ return new_symbol(sym.LPAREN, yytext()); }
")" 		{ return new_symbol(sym.RPAREN, yytext()); }
"[" 		{ return new_symbol(sym.LBRACKET, yytext()); }
"]" 		{ return new_symbol(sym.RBRACKET, yytext()); }
"{" 		{ return new_symbol(sym.LBRACE, yytext()); }
"}"			{ return new_symbol(sym.RBRACE, yytext()); }
"==" 		{ return new_symbol(sym.IF_EQUAL, yytext()); }
"!=" 		{ return new_symbol(sym.IF_NOT_EQUAL, yytext()); }
">" 		{ return new_symbol(sym.IF_GREATER, yytext()); }
">=" 		{ return new_symbol(sym.IF_GREATER_EQUAL, yytext()); }
"<" 		{ return new_symbol(sym.IF_LESS, yytext()); }
"<=" 		{ return new_symbol(sym.IF_LESS_EQUAL, yytext()); }
"&&" 		{ return new_symbol(sym.AND, yytext()); }
"||" 		{ return new_symbol(sym.OR, yytext()); }

<YYINITIAL> "//" 		     { yybegin(COMMENT); }
<COMMENT> .      { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }

"true"|"false" 	{ return new_symbol(sym.BOOL_CONST, new String(yytext())); }
[0-9]+  { return new_symbol(sym.NUMBER, new Integer (yytext())); }
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{return new_symbol (sym.IDENT, yytext()); }
"'"[\040-\176]"'" {return new_symbol (sym.CHAR_CONST, new Character (yytext().charAt(1)));} 

. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)); }






