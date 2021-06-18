grammar Expr;

prog : (decl | expr)+ EOF;

decl : ID ':' INT_TYPE '=' NUM;
/*
允许左递归
*/
expr : expr '*' expr
    | expr '+' expr
    | ID
    | NUM;

/*
Tokens
*/

INT_TYPE : 'int';

ID : [a-z][a-zA-Z0-9_]*;

NUM : '0' | '-'?[1-9][0-9]*;

COMMENT : '--' ~[\r\n]* -> skip;

WS : [ \r\n]+ -> skip;

