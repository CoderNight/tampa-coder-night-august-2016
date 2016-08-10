//
// Formal Grammar Definition of TwoRobotsPushingButtons files
// 
// @Author:  anonymous
// @Date:    2016.08.09
//
/////////////////////////////////////////////////////////////////////////////////////////

lexer grammar RobotCommandLexer;

/////////////////////////////////////////////////////////////////////////////////////////
// Function DSL Lexer Specification
/////////////////////////////////////////////////////////////////////////////////////////

NEWLINE      : '\r'? '\n' ;
WS           : [ \t]+ -> skip ; // match 1-or-more whitespace but discard
LINE_COMMENT : ('//'|'#') .*? NEWLINE+ -> skip ;
COMMENT      : '/*' .*? '*/' NEWLINE* -> skip ;

RI           : 'O' | 'B' ;

INT          :   '0' | [1-9] [0-9]* ; // no leading zeros

