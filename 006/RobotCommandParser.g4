//
// Formal Grammar Definition of TwoRobotsPushingButtons
// 
// @Author:  anonymous
// @Date:    2016.08.09
//
/////////////////////////////////////////////////////////////////////////////////////////

parser grammar RobotCommandParser;

options { tokenVocab=RobotCommandLexer; }

/////////////////////////////////////////////////////////////////////////////////////////
// Function DSL Parser Specification
/////////////////////////////////////////////////////////////////////////////////////////

file         :  nullStatement? testnumber testcase+ EOF ;

nullStatement: NEWLINE+ ;

testnumber   : INT NEWLINE+ # TestNumberStatement
             ;

testcase     : INT roboMove+ NEWLINE+ # TestCaseStatement
             ;

roboMove     : RI INT ;

