package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import model.Board;
import model.Mark;
import server.model.ServerGame;
import server.protocol.ProtocolConstants;
import server.protocol.ProtocolControl;

/**
 * A handler for a client connected to the server. 
 * The handler receives incoming messages from the Client and can send messages to the Client.
 * @author Bas Hendrikse
 *
 */
public class ClientHandler extends Thread {

	private Server server;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;
	private ServerGame game;

	/**
	 * Constructor, initializes the instance variables.
	 * @param serverArg The server the client is connected to.
	 * @param sockArg	The socket to the Client on which messages are written.
	 * @throws IOException	
	 */
	public ClientHandler(Server serverArg, Socket sockArg) throws IOException {
		if (serverArg == null || sockArg == null) {
			System.err.println("A server and socket are needed to create a handler.");
		} else {
			sock = sockArg;
			server = serverArg;
		}
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		announce();
	}

	/**
	 * Send a message to all users that a new Client is connected.
	 * @throws IOException
	 */
	public void announce() throws IOException {
		String read = in.readLine();
		if (read.startsWith("joinRequest ")) {
			clientName = read.replaceAll("joinRequest ", "");
			server.broadcast("[" + clientName + " has entered]");
		}
	}

	/**
	 * Reads the messages formatted as agreed in the protocol from the socket
	 * connection.
	 */
	public void run() {
		String stream = null;
		boolean exit = false;
		while (!exit) {
			try {
				stream = in.readLine();
				String[] splitStream = stream.split(ProtocolConstants.msgSeperator);

				if (splitStream[0].equals(ProtocolControl.joinRequest) && splitStream.length == 2) {
					clientName = splitStream[1];
				} else if (splitStream[0].equals(ProtocolControl.getBoard)) {
					sendBoard();
				} else if (splitStream[0].equals(ProtocolControl.playerTurn)) {
					sendTurn();
				} else if (splitStream[0].equals(ProtocolControl.doMove)) {
					try {
						boolean isValid = !game.getBoard().gameOver()
										&& game.getBoard().isField(game.getBoard().
										getHighestField(Board.indexToCol((Integer.parseInt(splitStream[1]))))); 
					
						if (isValid && game.getCurrentPlayer().getName().equals(clientName)) {
							server.broadcastSuccesfulMoveResult(Integer.parseInt(splitStream[1]),
											game, this);
						} else if (isValid &&
										!game.getCurrentPlayer().getName().equals(clientName)) {
							sendInvalidUserTurn();
						} else {
							server.broadcastFailedMoveResult(Integer.parseInt(splitStream[1]),
											this);
						}
					} catch (NumberFormatException e) {
						sendMessage("The client should provide a number as index");
						System.err.println("The client should provide a number as index");
					}
				} else if (splitStream[0].equals(ProtocolControl.rematch)) {
					server.rematch(this);
				} else if (splitStream[0].equals(ProtocolControl.sendMessage)) {
					String message = stream.replace(ProtocolControl.sendMessage
									+ ProtocolConstants.msgSeperator, "");
					server.broadcastChatMessage(getClientName(), message);
				} else {
					sendInvalidCommand();
				}
			} catch (IOException e) {
				System.err.println("The client is disconnecting now");
				exit = true;
				try {
					sock.close();
				} catch (IOException e1) {
					System.err.println("Unable to close socket.");
				}
			}
		}
		shutdown();
	}

	/**
	 * Sends a confirmation the the Client that it may connect to the server.
	 * @param mark The mark which the Client is going to play with
	 */
	public void sendAcceptRequest(String mark) {
		try {
			out.write(ProtocolControl.acceptRequest + ProtocolConstants.msgSeperator + mark);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Clients to start the game with the given players.
	 */
	public void sendStartGame() {
		String namep1 = game.getPlayers()[0].getName();
		String namep2 = game.getPlayers()[1].getName();
		try {
			out.write(ProtocolControl.startGame + ProtocolConstants.msgSeperator + namep1
							+ ProtocolConstants.msgSeperator + namep2);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends the current Board parsed a String of the game to the Client.
	 */
	public void sendBoard() {
		String boardString = "";
		for (Mark field : game.getBoard().getFields()) {
			if (field.equals(Mark.RED)) {
				boardString += ProtocolConstants.msgSeperator + ProtocolConstants.red;
			} else if (field.equals(Mark.YELLOW)) {
				boardString += ProtocolConstants.msgSeperator + ProtocolConstants.yellow;
			} else if (field.equals(Mark.EMPTY)) {
				boardString += ProtocolConstants.msgSeperator + ProtocolConstants.empty;
			} else {
				System.err.println("This field cannot be parsed into a valid mark. Field: " + field);
			}
		}
		try {
			out.write(ProtocolControl.sendBoard + boardString);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends the name of the player whose turn it currently is.
	 */
	public void sendTurn() {
		try {
			out.write(ProtocolControl.turn + ProtocolConstants.msgSeperator
							+ game.getCurrentPlayer().getName());
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client that the requested move was succesful including the
	 * player's name who did the move and the next player.
	 * @param index The index on which the move is set.
	 */
	public void sendSuccesfulMoveResult(int index) {
		try {
			out.write(ProtocolControl.moveResult + ProtocolConstants.msgSeperator + game.getBoard().getHighestField(Board.indexToCol(index))
							+ ProtocolConstants.msgSeperator + game.getCurrentPlayer().getName()
							+ ProtocolConstants.msgSeperator + true
							+ ProtocolConstants.msgSeperator + game.getNextPlayer().getName());
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client that the requested move failed including the
	 * player's name who did the move and the next player which is 
	 * the player who tried to do a move.
	 * @param index
	 */
	public void sendFailedMoveResult(int index) {
		try {
			out.write(ProtocolControl.moveResult + ProtocolConstants.msgSeperator + index
							+ ProtocolConstants.msgSeperator + game.getCurrentPlayer().getName()
							+ ProtocolConstants.msgSeperator + false
							+ ProtocolConstants.msgSeperator + game.getCurrentPlayer().getName());
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client that the game is over, 
	 * including the winner of the game and the reason of endgame.
	 * @param reason
	 */
	public void sendEndGame(String reason) {
		String winnerString = game.getWinner() == null ? getClientName() : game.getWinner()
						.getName();
		try {
			out.write(ProtocolControl.endGame + ProtocolConstants.msgSeperator + winnerString
							+ ProtocolConstants.msgSeperator + reason);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Could not send the endGame message to " + getClientName());
		}
	}

	/**
	 * Sends a message to the Client who tried to do a move, but whose turn it isn't.
	 */
	public void sendInvalidUserTurn() {
		try {
			out.write(ProtocolConstants.invalidCommand + ProtocolConstants.msgSeperator
							+ ProtocolConstants.invalidUserTurn + ProtocolConstants.msgSeperator);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client that the command isn't known by the server.
	 */
	public void sendInvalidCommand() {
		try {
			out.write(ProtocolConstants.invalidCommand + ProtocolConstants.msgSeperator
							+ ProtocolConstants.invalidCommand);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client that the name with
	 *  which the Client is trying to connect is already in use.
	 */
	public void sendUsernameInUse() {
		try {
			out.write(ProtocolConstants.invalidCommand + ProtocolConstants.msgSeperator
							+ ProtocolConstants.usernameInUse);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client that the move which is requested is not valid.
	 */
	public void sendInvalidMove() {
		try {
			out.write(ProtocolConstants.invalidCommand + ProtocolConstants.msgSeperator
							+ ProtocolConstants.invalidMove);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client that the name with 
	 * which the user is trying to connect contains invalid characters.
	 */
	public void sendInvalidUsername() {
		try {
			out.write(ProtocolConstants.invalidCommand + ProtocolConstants.msgSeperator
							+ ProtocolConstants.invalidUsername);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a confirmation to the Client that the match can
	 *  be reset and the Client is playing against the same oponent again.
	 */
	public void sendRematchConfirm() {
		try {
			out.write(ProtocolControl.rematchConfirm);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sends a message to the Client.
	 * @param msg The message the server wants to send.
	 */
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * Sets the server game.
	 * @param game
	 */
	public void setServerGame(ServerGame game) {
		this.game = game;
	}

	/**
	 * @return The server game.
	 */
	public ServerGame getServerGame() {
		return this.game;
	}

	/**
	 * @return The name of the Client which this ClientHandler belongs to.
	 */
	public String getClientName() {
		return this.clientName;
	}

	/**
	 * Broadcasts that the game is over and removes the handler from all lists in Server.
	 */
	private void shutdown() {
		if (game != null && !game.getBoard().gameOver()) {
			server.broadcastEndgame(game, this);
		}
		server.removeHandler(this);
		server.broadcast("[" + clientName + " has left]");
	}

}
