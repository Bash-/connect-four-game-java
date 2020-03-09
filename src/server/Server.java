package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.HumanPlayer;
import model.Mark;
import model.Player;
import server.model.ServerGame;
import server.protocol.ProtocolConstants;
import server.protocol.ProtocolControl;
import view.FourGUIView;

/**
 * A Connect Four server which creates and saves new instances of ClientHandlers
 * for connecting Clients. Hosted on local host and a given port.
 * 
 * @author Bas Hendrikse
 *
 */
public class Server extends Thread {

	private int port;
	private List<ClientHandler> connectedPlayers;
	private ArrayList<ArrayList<ClientHandler>> activePlayers;
	private ArrayList<ClientHandler> waitingPlayers;
	private Map<ArrayList<ClientHandler>, ArrayList<ClientHandler>> rematchMap;

	/**
	 * Constructor, initializes instance variables.
	 * 
	 * @param portArg
	 */
	public Server(int portArg) {
		port = portArg;
		connectedPlayers = new ArrayList<ClientHandler>();
		activePlayers = new ArrayList<ArrayList<ClientHandler>>();
	}

	/**
	 * Creates a new Server Socket and creates new ClientHandler instances for
	 * connecting Clients. Automatically starts a new game if a multiple of two
	 * clients are connected.
	 */
	public void run() {
		ServerSocket ssock = null;

		// create server socket
		try {
			ssock = new ServerSocket(port);

			System.out.println("Server started on port " + port + ".");

			waitingPlayers = new ArrayList<ClientHandler>();

			try {
				while (true) {
					Socket socket = ssock.accept();
					final ClientHandler handler = new ClientHandler(this, socket);
					boolean userNameInUse = false;

					if (!handler.getClientName().matches(ProtocolConstants.charRegex)) {
						handler.sendInvalidUsername();
						userNameInUse = true;
					}

					for (ClientHandler ch : getHandlers()) {
						if (handler.getClientName().equals(ch.getClientName())) {
							handler.sendUsernameInUse();
							userNameInUse = true;
						}
					}
					if (!userNameInUse) {
						System.out.println("Client " + handler.getClientName()
										+ " entered the server");
						addHandler(handler);
						handler.start();

						if (waitingPlayers.size() == 0) {
							handler.sendAcceptRequest(ProtocolConstants.yellow);
							waitingPlayers.add(handler);
						} else if (waitingPlayers.size() == 1) {
							handler.sendAcceptRequest(ProtocolConstants.red);
							waitingPlayers.add(handler);
						}

						if (waitingPlayers.size() >= 2) {
							Player p1 = new HumanPlayer(waitingPlayers.get(0).getClientName(),
											Mark.YELLOW);
							Player p2 = new HumanPlayer(waitingPlayers.get(1).getClientName(),
											Mark.RED);
							ServerGame game = new ServerGame(p1, p2);
							for (int i = 0; i < 2; i++) {
								ClientHandler ch = waitingPlayers.get(i);
								ch.setServerGame(game);
								ch.sendStartGame();
							}
							activePlayers.add(waitingPlayers);
							
							game.start();

							ArrayList<ClientHandler> tempList = new ArrayList<ClientHandler>();
							tempList.addAll(waitingPlayers);
							ClientHandler ch1 = waitingPlayers.get(0);
							ClientHandler ch2 = waitingPlayers.get(1);
							tempList.remove(ch1);
							tempList.remove(ch2);
							waitingPlayers = tempList;
						}
					}

					//Shows which Client are online
					int counter = 0;
					System.out.println("Players currently online:");
					for (ClientHandler c : getHandlers()) {
						System.out.println(++counter +". Client name: " + c.getClientName());
					}
				}
			} catch (IOException e) {
				System.out.println("Something wrong with a client handler");
			}
		} catch (IOException e) {
			System.err.println("The server cannot be started on port " + port + ".");
		}
		System.out.println("Stop with running...");
	}

	/**
	 * Calls method for all client handlers to send a move result and checks if
	 * the game is over after this move.
	 * 
	 * @param i
	 *            The index on which the client wants to do a move
	 * @param game
	 *            The current (Server) game
	 * @param clientHandler
	 *            The client handler which requests the move
	 */
	public void broadcastSuccesfulMoveResult(int i, ServerGame game, ClientHandler clientHandler) {
		if (connectedPlayers.isEmpty()) {
			System.out.println("There are no clients");
		} else {
			for (ClientHandler ch : getGameHandlers(clientHandler)) {
				ch.sendSuccesfulMoveResult(i);
			}
		}
		game.getBoard().setField(i, game.getCurrentPlayer().getMark());
		game.nextPlayer();
		if (game.getBoard().gameOver()) {
			broadcastEndgame(game, clientHandler);
			game.setEndTime();
			game.getDuration(); // divide by 1000000 to get milliseconds.
			System.out.println(game.getBoard().toString());
			game.printResult();
			System.out.println("This game lasted " + game.getDuration() + " milliseconds");
		}
	}

	/**
	 * Calls the EndGame method for all game handlers with the reason of the end
	 * game.
	 * 
	 * @param game
	 *            The current server game
	 * @param clientHandler
	 *            The clientHandler who did the final move
	 */
	public void broadcastEndgame(ServerGame game, ClientHandler clientHandler) {
		if (game != null && getGameHandlers(clientHandler) != null) {
			String reden;
			if (game.getBoard().isFull()) {
				reden = ProtocolConstants.draw;
			} else if (game.getBoard().hasWinner()) {
				reden = ProtocolConstants.winner;
			} else {
				reden = ProtocolConstants.connectionlost;
			}
			for (ClientHandler ch : getGameHandlers(clientHandler)) {
				ch.sendEndGame(reden);
			}
		} else {
			System.err.println("Cannot end a non-existing game.");
		}
	}

	/**
	 * Calls a failed move method for all handlers with the index which failed.
	 * 
	 * @param i
	 *            The index on which the handler tried to do a move
	 * @param clientHandler
	 *            The handler which tried to do a move
	 */
	public void broadcastFailedMoveResult(int i, ClientHandler clientHandler) {
		if (waitingPlayers.isEmpty()) {
			System.out.println("There are no clients");
		} else {
			for (ClientHandler ch : getGameHandlers(clientHandler)) {
				ch.sendFailedMoveResult(i);
			}
		}
	}

	/**
	 * Registers if the players want to play again against each other. The
	 * method registers a rematch request of handlers and resets the game state
	 * if all game players requested a rematch.
	 * 
	 * @param clientHandler
	 *            The handler which requested a rematch
	 * @return If all game players requested a rematch
	 */
	public boolean rematch(ClientHandler clientHandler) {
		if (rematchMap == null) {
			rematchMap = new HashMap<ArrayList<ClientHandler>, ArrayList<ClientHandler>>();
		}
		ArrayList<ClientHandler> list = getGameHandlers(clientHandler);
		if (rematchMap.containsKey(list)) {
			ArrayList<ClientHandler> thisMatch = rematchMap.get(list);
			if (!thisMatch.contains(clientHandler)) {
				thisMatch.add(clientHandler);
				rematchMap.put(list, thisMatch);
			}
		} else {
			ArrayList<ClientHandler> thisMatch = new ArrayList<ClientHandler>();
			thisMatch.add(clientHandler);
			rematchMap.put(list, thisMatch);
		}

		boolean rematch = false;
		if (rematchMap.get(list).size() == 2) {
			rematch = true;
			rematchMap.remove(list);
			for (ClientHandler ch : list) {
				ch.getServerGame().reset();
				ch.sendRematchConfirm();
			}
		}

		return rematch;
	}

	/**
	 * Sends a global message to all clients.
	 * 
	 * @param msg
	 *            The message
	 */
	public void broadcast(String msg) {
		if (connectedPlayers.isEmpty()) {
			System.out.println("There are no clients");
		} else {
			for (ClientHandler ch : connectedPlayers) {
				ch.sendMessage(msg);
			}
		}
	}

	public void broadcastChatMessage(String name, String msg) {
		if (connectedPlayers.isEmpty()) {
			System.out.println("There are no clients");
		} else {
			for (ClientHandler ch : connectedPlayers) {
				ch.sendMessage(ProtocolControl.broadcastMessage + ProtocolConstants.msgSeperator
								+ name + ProtocolConstants.msgSeperator + msg);
			}
		}
	}

	/**
	 * Retreives all handlers playing a game with the given clientHandler.
	 * 
	 * @param clientHandler
	 *            The handler playing a game
	 * @return An ArrayList with handlers in the game with the clientHandler
	 */
	public ArrayList<ClientHandler> getGameHandlers(ClientHandler clientHandler) {
		ArrayList<ClientHandler> result = null;

		for (ArrayList<ClientHandler> list : activePlayers) {
			if (list.contains(clientHandler)) {
				result = list;
			}
		}
		return result;
	}

	/**
	 * @return all handlers connected to the server
	 */
	public List<ClientHandler> getHandlers() {
		return connectedPlayers;
	}

	/**
	 * Adds a handler to the list with handlers connected to the server.
	 * 
	 * @param handler
	 */
	public void addHandler(ClientHandler handler) {
		connectedPlayers.add(handler);
	}

	/**
	 * Removes a handler from all lists containing handlers from the server.
	 * (Waiting handlers, active game handlers and global handler list)
	 * 
	 * @param handler
	 */
	public void removeHandler(ClientHandler handler) {
		if (getGameHandlers(handler) != null) {
			for (ClientHandler ch : getGameHandlers(handler)) {
				if (!waitingPlayers.contains(handler)) {
					waitingPlayers.add(ch);
				}
			}
			if (getGameHandlers(handler).size() == 2) {
				activePlayers.remove(getGameHandlers(handler));
			}
		}
		waitingPlayers.remove(handler);
		connectedPlayers.remove(handler);
	}
} // end of class Server
