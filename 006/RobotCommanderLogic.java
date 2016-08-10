
import robotCommand.RobotCommander;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RobotCommanderLogic {

   enum Action {
      BACKWARD,
      FORWARD,
      DO_NOTHING,
      PUSH_BUTTON
   }

   private static class Move {
      public final Action action;
      public final int pos;
      Move(Action ac,int p) { action = ac; pos = p; }
   }

   static String padN(int num) {
      if(num < 10) return "   " + num;
      else if(num < 100) return "  " + num;
      else if(num < 1000) return " " + num;
      else return "" + num;
   }

   static void printMove(Move m) {
      if((m.action == Action.FORWARD) || (m.action == Action.BACKWARD))
         System.out.print(" Move to button " + padN(m.pos));
      else if(m.action == Action.DO_NOTHING)
         System.out.print(" Stay at button " + padN(m.pos));
      else
         System.out.print(" Push button    " + padN(m.pos));
   }

   private void solveMoves(boolean verbose) {

      int oPos = 1;
      int bPos = 1;

      int oMoves = 0;
      int bMoves = 0;

      if(verbose) {

         // Method 1 (constructing action commands for each atom of time)
         // This solution while not as efficient has the advantage of 
         // being able to print all of the steps

         LinkedList<Move> OMoves = new LinkedList<Move>();
         LinkedList<Move> BMoves = new LinkedList<Move>();
         
         for(RobotCommander.RoboMove rm:moves) {
            if(rm.robo == RobotCommander.Robot.O) {
               if(oPos <= rm.pos) for(;oPos != rm.pos;++oPos,--bMoves) OMoves.add(new Move(Action.FORWARD,oPos+1));
               else               for(;oPos != rm.pos;--oPos,--bMoves) OMoves.add(new Move(Action.BACKWARD,oPos-1));
               if(bMoves <= 0) oMoves += -bMoves;
               for(;bMoves > 0;--bMoves) OMoves.add(new Move(Action.DO_NOTHING,oPos));
               OMoves.add(new Move(Action.PUSH_BUTTON,oPos));
               ++oMoves;
               bMoves = 0;
            }
            else {
               if(bPos <= rm.pos) for(;bPos != rm.pos;++bPos,--oMoves) BMoves.add(new Move(Action.FORWARD,bPos+1));
               else               for(;bPos != rm.pos;--bPos,--oMoves) BMoves.add(new Move(Action.BACKWARD,bPos-1));
               if(oMoves <= 0) bMoves += -oMoves;
               for(;oMoves > 0;--oMoves) BMoves.add(new Move(Action.DO_NOTHING,bPos));
               BMoves.add(new Move(Action.PUSH_BUTTON,bPos));
               ++bMoves;
               oMoves = 0;
            }
         }
   
         while(OMoves.size() > BMoves.size()) BMoves.add(new Move(Action.DO_NOTHING,bPos));     
         while(OMoves.size() < BMoves.size()) OMoves.add(new Move(Action.DO_NOTHING,oPos)); 
   
         System.out.println(OMoves.size());

         System.out.println("Time  | Orange               | Blue");
         System.out.println("------+----------------------+----------------------");
         // (e.g.)               1 | Move to button   100 | Push button        1
         for(int i = 0;i < OMoves.size();++i) {
            System.out.print(" " + padN(i+1) + " | ");
            Move om = OMoves.get(i);
            Move bm = BMoves.get(i);
            printMove(om); System.out.print(" | ");
            printMove(bm); System.out.println("");
         }
      }
      else {
         int totalMovesO = 0;
         int totalMovesB = 0;
         for(RobotCommander.RoboMove rm:moves) {
            if(rm.robo == RobotCommander.Robot.O) {
               // Method 3 (essentially the same logic as Method2 below but without iterations)
               int moves = Math.abs(rm.pos - oPos);
               oPos = rm.pos;
               bMoves -= moves;
               totalMovesO += moves + 1;
               if(bMoves < 0) oMoves += 1 - bMoves;
               else { ++oMoves; totalMovesO += bMoves; }
               bMoves = 0;
            }
            else {
               // Method 2 (personally, I think this is the most readable approach)
               if(bPos <= rm.pos) for(;bPos != rm.pos;++bPos,--oMoves) ++totalMovesB;
               else               for(;bPos != rm.pos;--bPos,--oMoves) ++totalMovesB;
               if(oMoves < 0) bMoves += -oMoves; // update how many moves we are past O
               for(;oMoves > 0;--oMoves) ++totalMovesB; // need to wait because O has outstanding moves
               ++totalMovesB; // Increment once for actual push of the button
               ++bMoves;      //  ditto...
               oMoves = 0;    // reset oMoves because we've already "synced" up with it
            }
         }
         System.out.println(Math.max(totalMovesO,totalMovesB));
 
      }

      moves.clear();
   }

   LinkedList<RobotCommander.RoboMove> moves = new LinkedList<RobotCommander.RoboMove>();
   int testCase = 0;

   public static void main(String[] args) {

      boolean verbose = false;
      String filename = "";

      if (args.length != 1 && args.length != 2) {
         System.out.println("Please run with argument: [-v] <RobotCommandFile>");
         return;
      }

      for(int i = 0; i < args.length; ++i) {
         if (args[i].equals("-v")) verbose = true;
         else filename = args[i];
      }

      File file = new File(filename);

      final RobotCommanderLogic logic = new RobotCommanderLogic();
      final boolean verboseFlag = verbose;

      // ANTLR derived parser callback
      RobotCommander.Callback cb = new RobotCommander.Callback() {
         public void makeNewTestCase() {
            if(logic.moves.size() > 0) logic.solveMoves(verboseFlag);
            System.out.print("Case #" + ++logic.testCase + ": ");
         }
         public void addMove(RobotCommander.RoboMove rm) {
            logic.moves.add(rm);
         }
      };
      
      try {
         RobotCommander.loadRobotCommands(file,cb);
         logic.solveMoves(verboseFlag);
      }
      catch(FileNotFoundException fnfe) {
         System.out.println(fnfe);
      }
      catch(IOException ioe) {
         System.out.println(ioe);
      }
      catch(RobotCommander.RobotCommanderParseException ffpe) {
         System.out.println(ffpe.getMessage());
      }

   }

}

