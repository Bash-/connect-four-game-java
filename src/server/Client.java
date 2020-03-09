package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import model.ComputerPlayer;
import model.HumanPlayer;
import model.Mark;
import model.NaiveStrategy;
import model.Player;
import model.SmartStrategy;
import server.chat.Chat;
import server.model.ClientGame;
import server.protocol.ProtocolConstants;
import server.protocol.ProtocolControl;
import view.FourGUIView;

/**
 * A Connect Four Client for the player to communicate with the Connect Four
 * Server.
 * 
 * @author Bas Hendrikse 
 * Based on the Client class from the chatbox exercise from week 7
 */
public class Client extends Thread {

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private Mark mark;
	private ClientGame game;
	private String playerType;
	private Chat chat;
	private long delay;

	/**
	 * Constructs a Client-object and tries to make a socket connection.
	 */
	public Client(String name, InetAddress address, int port, String playerType, long delay)
					throws IOException {
		clientName = name;
		sock = new Socket(address, port);
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.playerType = playerType;
		chat = new Chat();
		this.delay = delay;
	}

	/**
	 * Reads the messages formatted as agreed in the protocol from the socket
	 * connection.
	 */
	public void run() {
		String input = null;
		try {
			input = in.readLine();
		} catch (IOException e1) {
			System.err.println("Failed to read from input");
		}
		String[] inputSplit = input.split(ProtocolConstants.msgSeperator);
		if (inputSplit[0].equals(ProtocolConstants.invalidCommand)
						&& inputSplit[1].equals(ProtocolConstants.usernameInUse)) {
			System.err.println("That username is already in use.");
		} else if (inputSplit[0].equals(ProtocolConstants.invalidCommand)
						&& inputSplit[1].equals(ProtocolConstants.invalidUsername)) {
			System.err.println("That username contains invalid characters, "
							+ "only letters, numbers and a underscore allowed.");
		} else {
			boolean exit = false;
			while (!exit) {
				try {
					input = in.readLine();
					inputSplit = input.split(ProtocolConstants.msgSeperator);
					if (inputSplit[0].equals(ProtocolControl.acceptRequest) && inputSplit.length == 2) {
						if (inputSplit[1].equals(ProtocolConstants.yellow)) {
							mark = Mark.YELLOW;

						} else if (inputSplit[1].equals(ProtocolConstants.red)) {
							mark = Mark.RED;
						}
					} else if (inputSplit[0].equals(ProtocolControl.sendBoard)) {
						// Mark[] fields = new Mark[Board.HEIGTH*Board.WIDTH];
						for (int i = 1; i < inputSplit.length; i++) {
							if (inputSplit[i].equals(ProtocolConstants.yellow)) {
								// fields[i] = Mark.YELLOW;
								game.getBoard().forceSetField(i, Mark.YELLOW);
							} else if (inputSplit[i].equals(ProtocolConstants.red)) {
								// fields[i] = Mark.RED;
								game.getBoard().forceSetField(i, Mark.RED);
							} else if (inputSplit[i].equals(ProtocolConstants.empty)) {
								// fields[i] = Mark.EMPTY;
								game.getBoard().forceSetField(i, Mark.EMPTY);
							} else {
								System.err.println("That type field is unknown, "
												+ "we can not parse it into a valid field. Index "
												+ i);
							}
						}
						game.getBoard().updateBoard();
					} else if (inputSplit[0].equals(ProtocolControl.startGame) && inputSplit.length == 3) {
						if (inputSplit[1].equals(getClientName())) {

							if (playerType.equals("Human")) {
								game = new ClientGame(new HumanPlayer(inputSplit[1], Mark.YELLOW),
												new HumanPlayer(inputSplit[2], Mark.RED), this);
							} else if (playerType.equals("Computer-Naive")) {
								game = new ClientGame(new ComputerPlayer(getClientName(),
												Mark.YELLOW, new NaiveStrategy()), new HumanPlayer(
												inputSplit[2], Mark.RED), this);
							} else if (playerType.equals("Computer-Smart")) {
								game = new ClientGame(new ComputerPlayer(getClientName(),
												Mark.YELLOW, new SmartStrategy()), new HumanPlayer(
												inputSplit[2], Mark.RED), this);
							}
						} else if (inputSplit[2].equals(getClientName())) {
							if (playerType.equals("Human")) {
								game = new ClientGame(new HumanPlayer(inputSplit[1], Mark.YELLOW),
												new HumanPlayer(inputSplit[2], Mark.RED), this);
							} else if (playerType.equals("Computer-Naive")) {
								game = new ClientGame(new HumanPlayer(inputSplit[1], Mark.YELLOW),
												new ComputerPlayer(getClientName(), Mark.RED,
																new NaiveStrategy()), this);
							} else if (playerType.equals("Computer-Smart")) {
								game = new ClientGame(new HumanPlayer(inputSplit[1], Mark.YELLOW),
												new ComputerPlayer(getClientName(), Mark.RED,
																new SmartStrategy()), this);
							}
						}
						FourGUIView fgv = new FourGUIView(game, this);
						game.getBoard().addObserver(fgv);
						game.addObserver(fgv);
						chat.addObserver(fgv);
						fgv.start();
						game.start();
					} else if (inputSplit[0].equals(ProtocolControl.turn) && inputSplit.length == 2) {
						game.setClientToCurrentPlayer(inputSplit[1]);
					} else if (inputSplit[0].equals(ProtocolControl.moveResult) && inputSplit.length == 5) {
						int index = -1;
						try {
							index = Integer.parseInt(inputSplit[1]);
						} catch (NumberFormatException e2) {
							System.err.println("Cannot parse the second argument"
											+ " (index) of moveResult to an int");
						}
						String name = inputSplit[2];
						boolean valid = Boolean.parseBoolean(inputSplit[3]);

						String nextPlayer = inputSplit[4];

						if (valid) {
							game.setMove(index, name);
						} else {
							System.err.println("Move not valid please set a move again");
						}
						game.setClientToCurrentPlayer(nextPlayer);

						if (nextPlayer.equals(getClientName())) {
							game.getCurrentPlayer().requestMove(game);
						}
					} else if (inputSplit[0].equals(ProtocolControl.endGame) && inputSplit.length == 3) {
						String winner = inputSplit[1];
						String reason = inputSplit[2];
						game.setGameOver(winner, reason);
					} else if (inputSplit[0].equals(ProtocolControl.rematchConfirm)) {
						game.reset();
						game.getCurrentPlayer().requestMove(game);
					} else if (inputSplit[0].equals(ProtocolConstants.invalidCommand) ) {
						if (inputSplit.length == 2 && inputSplit[1].equals(ProtocolConstants.invalidCommand)) {
							System.err.println("You sent an unknown command to the server.");
						} else if (inputSplit.length == 2 && inputSplit[1].equals(ProtocolConstants.invalidMove)) {
							System.err.println("That move is invalid, try again.");
						} else if ((inputSplit.length == 2 || inputSplit.length == 3)&& inputSplit[1].equals(ProtocolConstants.invalidUserTurn)) {
							System.err.println("It is not your turn to do a move.");
						}
					} else if (inputSplit[0].equals(ProtocolControl.broadcastMessage) && inputSplit.length > 1) {
						String message = input.replace(ProtocolControl.broadcastMessage
										+ ProtocolConstants.msgSeperator + inputSplit[1]
										+ ProtocolConstants.msgSeperator, "");

						chat.addMessage(inputSplit[1], message);
					}

					synchronized (System.out) {
						System.out.println("[Server] " + input);
					}
				} catch (IOException e) {
					System.err.println("Server disconnected unexpectedly");
					exit = true;
				}
			}
		}
		shutdown();
	}

	/**
	 * Send a request to the server to connect to the server.
	 * 
	 * @param username
	 *            The name of the player.
	 */
	public void sendJoinRequest(String username) {
		try {
			out.write(ProtocolControl.joinRequest + ProtocolConstants.msgSeperator + username);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send joinRequest");
		}
	}

	/**
	 * Send a request to the server for the current board according to the
	 * server.
	 */
	public void sendGetBoard() {
		try {
			out.write(ProtocolControl.getBoard);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send getBoard");
		}
	}

	/**
	 * Send a request to the server for the player whose turn it currently is.
	 */
	public void sendGetPlayerTurn() {
		try {
			out.write(ProtocolControl.playerTurn);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send playerTurn");
		}
	}

	/**
	 * Send a request to the server to do a move on the specified index.
	 * 
	 * @param index
	 *            The index the move should be set.
	 */
	public void sendDoMove(int index) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			out.write(ProtocolControl.doMove + ProtocolConstants.msgSeperator + index);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send doMove");
		}
	}

	/**
	 * Send a request to the server to reset the game, to play again against the
	 * same player after the game.
	 */
	public void sendRematch() {
		try {
			out.write(ProtocolControl.rematch);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send rematch");
		}
	}

	/**
	 * Send a chat message to the server.
	 * 
	 * @param msg
	 *            The chat message the user would like to send.
	 */
	public void sendChatMessage(String msg) {
		try {
			out.write(ProtocolControl.sendMessage + ProtocolConstants.msgSeperator + msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send chat message");
		}
	}

	/**
	 * Send a message to the server.
	 * 
	 * @param msg
	 *            The message which the user would like to send.
	 */
	public void sendRandomMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send rematch");
		}
	}

	/**
	 * Close the socket connection.
	 */
	public void shutdown() {
		System.out.println("Disconnecting...");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.getMessage();
		}
		System.out.println("Disconnected");
		// System.exit(0);
	}

	/**
	 * @return Returns the Client's name.
	 */
	public String getClientName() {
		return this.clientName;
	}

	/**
	 * @return the current ClientGame.
	 */
	public ClientGame getClientGame() {
		return this.game;
	}

	/**
	 * Sets the current ClientGame.
	 * 
	 * @param game
	 *            The ClientGame which needs to be set.
	 */
	public void setClientGame(ClientGame game) {
		this.game = game;
	}

	/**
	 * @return The Client's Mark.
	 */
	public Mark getClientMark() {
		return mark;
	}

	/**
	 * @return The chat instance created by the Client.
	 */
	public Chat getChat() {
		return this.chat;
	}

}
