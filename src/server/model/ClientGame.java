package server.model;

import model.Game;
import model.Player;
import server.Client;
import server.protocol.ProtocolConstants;
import view.FourGUIView;

/**
 * @author Bas Hendrikse
 *
 */
public class ClientGame extends Game {

	private Client client;
	private Player winner;

	public ClientGame(Player s0, Player s1, Client client) {
		super(s0, s1);
		this.client = client;

	}

	public void start() {
		reset();
		getCurrentPlayer().requestMove(this);
	}

	@Override
	public void doMove(int index) {
		client.sendDoMove(index);

	}

	public void setMove(int index, String name) {

		for (Player p : getPlayers()) {
			if (p.getName().equals(name)) {
				getBoard().setField(index, p.getMark());

			}
		}
	}

	public void setClientToCurrentPlayer(String name) {
		for (int i = 0; i < getPlayers().length; i++) {
			if (getPlayers()[i].getName().equals(name)) {
				setCurrentPlayer(i);
			}
		}
	}

	public Player nameToPlayer(String name) {
		Player result = null;
		for (int i = 0; i < getPlayers().length; i++) {
			if (getPlayers()[i].getName().equals(name)) {
				result = getPlayers()[i];
			}
		}
		return result;
	}

	public void setConnectionLostWinner(Player winner) {
		this.winner = winner;
	}

	public Player getConnectionLostWinner() {
		return this.winner;
	}

	public void setGameOver(String name, String reason) {

		setEndTime();
		System.out.println(getBoard().toString());

		System.out.println("This game lasted " + getDuration() + " milliseconds"); 
		if (reason.equals(ProtocolConstants.connectionlost)) {
			setConnectionLostWinner(nameToPlayer(name));
			System.out.println("Player " + getConnectionLostWinner().getName() + "("
							+ getConnectionLostWinner().getMark()
							+ ") has won! The opponent has lost connection with the server.");
		} else {
			super.printResult();
		}
		setChanged();
		notifyObservers("GameOver");
	}

}
