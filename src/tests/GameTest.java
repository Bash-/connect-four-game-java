package tests;

import static org.junit.Assert.*;
import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Mark;
import model.Player;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the class Game.
 * @author Martijn Gemmink
 *
 */
public class GameTest {
  
    Player p1 = new HumanPlayer("Martijn", Mark.YELLOW);
    Player p2 = new HumanPlayer("Bas", Mark.RED);
    Player p3 = new ComputerPlayer("Computer", Mark.RED);
    Player p4 = new ComputerPlayer("Computer", Mark.YELLOW);
  
    Game game1 = new Game(p1, p2);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testStart() {
        game1.start();
    }

    @Test
    public void testReset() {
        game1.reset(); 
    }

    @Test
    public void testDoMove() {
      // Test of doMove() naar behoren werkt.
        p1.doMove(game1, 38);
        p2.doMove(game1, 39);
        p1.doMove(game1, 37);
        p2.doMove(game1, 40);
        p1.doMove(game1, 36);
        p2.doMove(game1, 41);
        p1.doMove(game1, 35);
   
      // Test getWinner()
        assertEquals("Martijn is de winnaar", "Martijn", game1.getWinner().getName());
  
      // Try Printresult
        game1.printResult();
    }

    @Test
        public void testGetCurrentPlayer() {
        assertEquals("Martijn is als eerste aan de beurt.", 
            "Martijn", game1.getCurrentPlayer().getName());
    
        // Martijn doet een zet.
        p1.doMove(game1, 38);
    
        // Controleer of nu Bas aan zet is.
        assertEquals("Bas is nu aan de beurt.", "Bas", game1.getCurrentPlayer().getName());
    
        // Reset het spel.
        game1.reset();
    }

    @Test
        public void testNextPlayer() {
        assertEquals("Martijn is als eerste aan de beurt.", 
              "Martijn", game1.getCurrentPlayer().getName());  
    
        // Martijn is aan de beurt. Nu wordt nextPlayer() aangeroepen.
        game1.nextPlayer();
    
        // Controleer of nu Bas aan de beurt is.
        assertEquals("Bas is nu aan de beurt.", "Bas", game1.getCurrentPlayer().getName());
    
        // Reset het spel.
        game1.reset();
    }

    @Test
      public void testGetNextPlayer() {
        assertEquals("Na Martijn is Bas aan de beurt.", "Bas", game1.getNextPlayer().getName());
    }

    @Test
    public void testSetCurrentPlayer() {
        assertEquals("Martijn is als eerste aan de beurt.", "Martijn", 
            game1.getCurrentPlayer().getName());  
        game1.setCurrentPlayer(1);
    
        // Controleer of nu Bas aan de beurt is.
        assertEquals("Bas is nu aan de beurt.", "Bas", game1.getCurrentPlayer().getName());
    
        // Reset het spel.
        game1.reset();
    }


}
