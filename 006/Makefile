export CLASSPATH=".:$(ANTLR4_HOME)/lib/antlr-4.4-complete.jar:$CLASSPATH"

antlr4 := java -jar $(ANTLR4_HOME)/lib/antlr-4.4-complete.jar
# Grammar Run
grun   := java -cp $(ANTLR4_HOME)/lib/antlr-4.4-complete.jar:RobotCommandParser.jar
# Grammar Run Testrig
grunt  := $(grun) org.antlr.v4.runtime.misc.TestRig

SHELL = /bin/bash

all: gen_lexer gen_parser compile_parser bundle_parser

gen_lexer: RobotCommandLexer.g4
	$(antlr4) -package robotCommand.parser -Werror -o robotCommand/parser $^

gen_parser: RobotCommandParser.g4
	$(antlr4) -package robotCommand.parser -Werror -o robotCommand/parser $^

compile_parser:
	@mkdir -p lib
	javac -d lib robotCommand/parser/*.java robotCommand/RobotCommander.java RobotCommanderLogic.java

bundle_parser:
	jar -cf robotCommandParser.jar robotCommand
	jar -uf robotCommandParser.jar -C lib robotCommand
	cd lib; jar -uf ../robotCommandParser.jar RobotCommanderLogic*.class

test_tokens:
	$(grunt) robotCommand.parser.RobotCommand file -tokens A-small-practice.in.txt

test_tree:
	$(grunt) robotCommand.parser.RobotCommand file -tree A-small-practice.in.txt

test_gui:
	$(grunt) robotCommand.parser.RobotCommand file -gui A-small-practice.in.txt

test_formal_reader:
	$(grun) robotCommand/RobotCommander A-small-practice.in.txt

run_small:
	@$(grun) RobotCommanderLogic A-small-practice.in.txt

run_small_verbose:
	@$(grun) RobotCommanderLogic -v A-small-practice.in.txt

run_large:
	@$(grun) RobotCommanderLogic A-large-practice.in.txt

clean:
	@rm -rf lib/robotCommand
	@rm -rf lib/*.class
	@rm -rf robotCommand/parser
	@rm -rf robotCommandParser.jar

