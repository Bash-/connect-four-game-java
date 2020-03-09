package control;

import java.io.IOException;
import java.net.InetAddress;

import server.Client;
import server.protocol.ProtocolConstants;
import server.protocol.ProtocolControl;
import view.FourGUIView;
import view.StartFrame;
import model.ComputerPlayer;
import model.Game;
import model.HumanPlayer;
import model.Mark;
import model.NaiveStrategy;
import model.Player;
import model.SmartStrategy;

/**
 * A class to start an offline game or to start a Client to connect to a Connect Four server.
 * @author Bas Hendrikse
 *
 */
public class ConnectFour {
	
	private Client client;
	private StartFrame sf;
	
	
	public ConnectFour() {
		sf = new StartFrame(this);
		sf.setVisible(true);
	}
	
	/**
	 * Starts a new game with the given names and types for the players.
	 * Does not make use of the server.
	 * @param player1 The username of the first player
	 * @param playerType1 The type of player of the first player
	 * @param player2	The username of the second player
	 * @param playerType2	The type of player of the second player
	 */
	public void startOfflineGame(String player1, String playerType1, String player2, String playerType2) {
		
		Player p1 = null;
		Player p2 = null;
		
		if (playerType1.equals("Human")) {
			p1 = new HumanPlayer(player1, Mark.YELLOW);
		}	else if (playerType1.equals("Computer-Naive")) {
			p1 = new ComputerPlayer(player1, Mark.YELLOW, new NaiveStrategy());
		}	else if (playerType1.equals("Computer-Smart")) {
			p1 = new ComputerPlayer(player1, Mark.YELLOW, new SmartStrategy());
		}
		
		if (playerType2.equals("Human")) {
			p2 = new HumanPlayer(player2, Mark.RED);
		}	else if (playerType2.equals("Computer-Naive")) {
			p2 = new ComputerPlayer(player2, Mark.RED, new NaiveStrategy());
		}	else if (playerType2.equals("Computer-Smart")) {
			p2 = new ComputerPlayer(player2, Mark.RED, new SmartStrategy());
		}
		
		Game game = new Game(p1, p2);
		FourGUIView fgv = new FourGUIView(game);
		game.getBoard().addObserver(fgv);
		fgv.start();
		game.start();
		
	}
	
	/**
	 * Creates a new Client and connects to the given address and port.
	 * @param username The username of the client that wants to connect to the server.
	 * @param i	The internet address of the computer the server runs on.
	 * @param port The port on which the server runs on.
	 * @param playerType The type of player (Human, Computer-Naive or Computer-Smart).
	 */
	public void startOnlineGame(String username, InetAddress i, int port, String playerType, long delay) {
		try {
			client = new Client(username, i, port, playerType, delay);
			client.sendJoinRequest(username);
			client.start();
		} catch (IOException e) {
			System.err.println("Could not connect to the server");
		}
	}
	
	/**
	 * Returns the Client which can be created in this class.
	 * @return this.client
	 */
	public Client getClient() {
		return this.client;
	}
	
	public StartFrame getStartFrame() {
		return this.sf;
	}
	
	public static void main(String[] args) {
		new ConnectFour();
	}

}
