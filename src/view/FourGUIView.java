package view;

import java.util.Observable;
import java.util.Observer;

import model.Board;
import model.Game;
import server.Client;
import server.chat.ChatFrame;
import server.model.ClientGame;

/**
 * A class that controls the frames for the Connect Four game.
 * @author Bas Hendrikse
 *
 */
public class FourGUIView extends Thread implements Observer {
	
	private Board board;
	private Game game;
	private FourFrame fourFrame;
	private LeaderboardFrame leaderboardFrame;
	private ChatFrame chatFrame;
	private Client client;

	
	public FourGUIView(Game g) {
		this.game = g;
		if (game != null) {
			this.board = game.getBoard();
		}
	}
	
	public FourGUIView(Game g, Client client) {
		this.game = g;
		if (game != null) {
			this.board = game.getBoard();
		}
		this.client = client;
	}
	
	public void run() {
		start();
	}
	
	public void start() {
		
		FourFrame tempFourFrame = new FourFrame(this);
		LeaderboardFrame tempLeaderboardFrame = new LeaderboardFrame(this);
		if (game instanceof ClientGame) {
			ChatFrame tempChatFrame = new ChatFrame(client.getChat(), client);
			tempChatFrame.setVisible(false);
			this.chatFrame = tempChatFrame;
		}
		tempFourFrame.setVisible(true);
		tempLeaderboardFrame.setVisible(false);
		
		this.fourFrame = tempFourFrame;
		this.leaderboardFrame = tempLeaderboardFrame;
		
	}
	
	public void update(Observable object, Object argument) {
		if (argument instanceof String) {
			if (argument.equals("setField")) {
				this.fourFrame.update(((Board) object).getFields());
			}	else if (argument.equals("noField")) {
				showError("This is not a field");
			}	else if (argument.equals("GameOver")) {
				this.fourFrame.setGameOverStatus();
				this.leaderboardFrame.update();
			}	else if (argument.equals("chatMessage")) {
				this.chatFrame.updateMessage();
			}	else if (argument.equals("reset")) {
				this.fourFrame.update(((Board) object).getFields());
				this.fourFrame.resetLabel();
			}
		}
		
	}

	public void showError(String error) {
		System.err.println(error);
	}
	
	public Board getBoard() {
		return this.board;
	}
	
	public Game getGame() {
		return this.game;
	}
	
	public FourFrame getFourFrame() {
		return this.fourFrame;
	}
	
	public LeaderboardFrame getLeaderboardFrame() {
		return this.leaderboardFrame;
	}
	
	public ChatFrame getChatFrame() {
		return this.chatFrame;
	}
	
	public Client getClient() {
		return this.client;
	}


}
