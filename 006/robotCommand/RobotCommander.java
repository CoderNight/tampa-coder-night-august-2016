package robotCommand;

import robotCommand.parser.RobotCommandParser;
import robotCommand.parser.RobotCommandLexer;
import robotCommand.parser.RobotCommandParserBaseListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RobotCommander extends RobotCommandParserBaseListener { 

   public enum Robot {
      O,
      B
   }

   public class RoboMove {
      public final Robot robo;
      public final int  pos;
      RoboMove(Robot r,int p) {
        robo = r;
        pos = p;
      }
   }

   public interface Callback {

      void makeNewTestCase();
      void addMove(RoboMove rm);
   }

   Callback mCallback;

   public RobotCommander() {}
   public RobotCommander(Callback callback) {
      mCallback = callback;
   }

   @Override
   public void enterTestCaseStatement(RobotCommandParser.TestCaseStatementContext ctx) {
      if(mCallback != null) mCallback.makeNewTestCase();
      else System.out.print("\nParsing test case: ");
   }
 
   @Override
   public void exitRoboMove(RobotCommandParser.RoboMoveContext ctx) {
      RoboMove rm = new RoboMove(Robot.valueOf(ctx.RI().getText()),
                                 Integer.valueOf(ctx.INT().getText()));
      if(mCallback != null) mCallback.addMove(rm);
      else System.out.print(rm.robo + " " + rm.pos + " ");
   }


   /**
    * Class to capture parse errors and report information. Thanks to Stackoverflow
    * member: Mouagip.
    * 
    * @author rfogarty
    * @credit http://stackoverflow.com/questions/18132078/handling-errors-in-antlr4?lq=1
    */
   static private class ThrowingErrorListener extends BaseErrorListener {

      public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

      @Override
      public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
         throws ParseCancellationException {
         throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg);
      }
   }
   
   /**
    * FlowGraphParseException records information of where the parser
    * had trouble parsing te graph file.
    * 
    * @author rfogarty
    */
   static public class RobotCommanderParseException extends Exception {
      private static final long serialVersionUID = 1L;

      public RobotCommanderParseException(String message, Throwable cause) {
         super(message, cause);
     }
   }


   /**
    * @throws FileNotFoundException
    * @throws IOException
    */
   public static void loadRobotCommands(File roboMoveFile,Callback cb) 
         throws FileNotFoundException,IOException,RobotCommanderParseException {

      // Recipe for ANTLR parsing taken from The Definitive ANTLR 4 Reference (by T. Parr)
      InputStream is = new FileInputStream(roboMoveFile);
      ANTLRInputStream input = new ANTLRInputStream(is);
      RobotCommandLexer lexer = new RobotCommandLexer(input);
      lexer.removeErrorListeners();
      lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      RobotCommandParser parser = new RobotCommandParser(tokens);
      parser.removeErrorListeners();
      parser.addErrorListener(ThrowingErrorListener.INSTANCE);
      try {
         ParseTree tree = parser.file(); // parse; start at prog
         ParseTreeWalker walker = new ParseTreeWalker();
         RobotCommander listener = new RobotCommander(cb);
         walker.walk(listener, tree);
      }
      catch(ParseCancellationException pce) {
         throw new RobotCommanderParseException("ERROR in function <" + roboMoveFile + "> "+ pce.getMessage(),pce);
      }
   }


   public static void main(String[] args) {

      if (args.length != 1) {
         System.out.println("Please run with argument: <RobotCommandFile>");
         return;
      }

      File defFile = new File(args[0]);

      // Test implementation of the FunctionDefinitionReader interface
      Callback cb = new Callback() {
         public void makeNewTestCase() { 
            System.out.print("\nParsing test case: ");
         }
         public void addMove(RoboMove rm) {
            System.out.print(rm.robo + " " + rm.pos + " ");
         }
      };
      
      try {
         loadRobotCommands(defFile,cb);
         System.out.println("\nSuccessfully parsed " + defFile);
      }
      catch(FileNotFoundException fnfe) {
         System.out.println(fnfe);
      }
      catch(IOException ioe) {
         System.out.println(ioe);
      }
      catch(RobotCommanderParseException ffpe) {
         System.out.println(ffpe.getMessage());
      }
   }
}

