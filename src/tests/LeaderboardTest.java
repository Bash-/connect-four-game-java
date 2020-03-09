package tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.LeaderBoard;
import model.Mark;
import model.Player;

import org.junit.Before;
import org.junit.Test;

public class LeaderboardTest {

	@Test (expected = FileNotFoundException.class)
	public void testFileNotExistent() throws FileNotFoundException, EOFException {
		
	// Test the leaderboard with a non existent file.
		Game game2 = new Game(new HumanPlayer("Martijn", Mark.YELLOW), 
						new HumanPlayer("Bas", Mark.RED));
		LeaderBoard l3 = new LeaderBoard(game2, "Banaan.txt");
	}
	
	@Test
	public void testLeaderBoardGame() throws FileNotFoundException, EOFException {
		Player p1 = new HumanPlayer("Martijn", Mark.YELLOW);
		Player p2 = new HumanPlayer("Bas", Mark.RED);
		Game game1 = new Game(p1, p2);
		LeaderBoard lb = new LeaderBoard(game1, "LeaderBoardTest.txt");

		game1.start();
		
        p1.doMove(game1, 38);
        p2.doMove(game1, 39);
        p1.doMove(game1, 37);
        p2.doMove(game1, 40);
        p1.doMove(game1, 36);
        p2.doMove(game1, 41);
        p1.doMove(game1, 35);  
        
        lb.addGame();
        
        ArrayList<ArrayList<Object>> leaderboard = lb.getLeaderBoard();
        System.out.println(leaderboard.get(0));
        
        // Check if Martijn is the winner according to the leaderboard.
        assertEquals("Martijn is de winnaar", "Martijn", leaderboard.get(0).get(2));      
        
        // Check if Martijn is Player 1 according to the leaderboard.
        assertEquals("Bas is player 1", "Martijn", leaderboard.get(0).get(0));   
  
        // Check if Martijn is Player 2 according to the leaderboard.
        assertEquals("Martijn is player 2", "Bas", leaderboard.get(0).get(1));   
        
        // Add game two more times
        lb.addGame();
        lb.addGame();
        
        // Check if there are three entries on the leaderboard.
        assertEquals("Martijn is de winnaar", "Martijn", leaderboard.get(0).get(2)); 
        assertEquals("Martijn is de winnaar", "Martijn", leaderboard.get(1).get(2));  
        assertEquals("Martijn is de winnaar", "Martijn", leaderboard.get(2).get(2));  
        
        // Empty 'LeaderboardTest.txt'
        PrintWriter writer = new PrintWriter("LeaderboardTest.txt");
        writer.print("");
        writer.close();
        
	}
}
