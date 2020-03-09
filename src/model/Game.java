package model;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.util.Observable;
import java.util.Scanner;

import server.model.ClientGame;

/**
 * This class controls the state of the game, makes sure players get turns.
 * @author Bas Hendrikse en Martijn Gemmink
 */
public class Game extends Observable {

	// -- Instance variables -----------------------------------------

	public static final int NUMBER_PLAYERS = 2;

	private LeaderBoard leaderBoard;

	private Board board;

	private Player[] players;

	private int current;

	private long startTime;
	private long endTime;


	
	// -- Constructors -----------------------------------------------

	/**
	 * Constructor initialises variables.
	 * @param s0 The first Player.
	 * @param s1 The second Player.
	 */
	/*
	 * @requires s0 != null && s1 != null;
	 * @ensures this.getDelay() == d;
	 * @ensures current = 0;
	 */
	public Game(Player s0, Player s1) {
		try {
			leaderBoard = new LeaderBoard(this, "Leaderboard.txt");
		} catch (FileNotFoundException | EOFException e) {
			e.printStackTrace();
		}

		board = new Board();
		players = new Player[NUMBER_PLAYERS];
		players[0] = s0;
		players[1] = s1;
		current = 0;
	}

	// -- Commands ---------------------------------------------------

	/**
	 * Starts the game by resetting the Board and requesting the move from the first player.
	 */
	/*
	 * @ensures \forall 0 <= i < Board.HEIGHT * Board.WIDTH; board.getField(i) == Mark.EMPTY;
	 */
	public void start() {
		reset();
		getCurrentPlayer().requestMove(this);
	}

	/**
	 * Sets the start time, sets the current player and resets the board.
	 */
	/*
	 * @ensures \forall 0 <= i < Board.HEIGHT * Board.WIDTH; board.getField(i) == Mark.EMPTY;
	 */
	public void reset() {
		setStartTime();
		current = 0;
		board.reset();
	}

	/**
	 * Requests the current player to do a move.
	 */
	public void requestMove() {
		getCurrentPlayer().requestMove(this);
	}

	/**
	 * Sets a move on the board on the given index, and gives the next player the turn.
	 * If the game is over, the end time is set and the game result is printed on System.out. 
	 * The game is added to the Leaderboard too.
	 * @param i The index the move should be set.
	 */
	/*
	 * @requires Board.isField(i);
	 * @ensures board.getField(i) != Mark.EMPTY;
	 */
	public void doMove(int i) {

		board.setField(i, getCurrentPlayer().getMark());

		if (!board.gameOver()) {
			nextPlayer();
			System.out.println(board.toString());

			getCurrentPlayer().requestMove(this);
		} else {
			endTime = System.nanoTime();
			System.out.println(board.toString());
			printResult();
			System.out.println("This game lasted " + getDuration()
							+ " milliseconds"); // divide by 1000000 to get
												// milliseconds.
			getLeaderboard().addGame();

		}
	}

	/**
	 * Prints the current board state to System.out.
	 */
	public void update() {
		System.out.println("\ncurrent game situation: \n\n" + board.toString()
						+ "\n");
	}

	/**
	 * Prints the game result to System.out.
	 */
	public void printResult() {
		if (board.hasWinner()) {
			Player winner = this.getWinner();
			System.out.println("Player " + winner.getName() + " ("
							+ winner.getMark() + ") has won!");
		} else {
			System.out.println("Draw. There is no winner!");
		}
	}

	/**
	 * @return The array of the players in the game.
	 */
	/* @pure */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * @return The player whose turn it is.
	 */
	/* @pure */
	public Player getCurrentPlayer() {
		return this.getPlayers()[current];
	}

	/**
	 * Sets the current player to the next player.
	 */
	/*
	 *	@ensures \old(getNextPlayer()).equals(getCurrentPlayer());
	 */
	public void nextPlayer() {
		current = (current + 1) % NUMBER_PLAYERS;
	}

	/**
	 * @return The player whose turn it is up next.
	 */
	/* @pure */
	public Player getNextPlayer() {
		return this.getPlayers()[(current + 1) % NUMBER_PLAYERS];
	}

	/**
	 * @return The current board of the game.
	 */
	/* @pure */
	public Board getBoard() {
		return board;
	}

	/**
	 * @return The leaderboard.
	 */
	/* @pure */
	public LeaderBoard getLeaderboard() {
		return leaderBoard;
	}

	/**
	 * Sets the start time of the game.
	 */
	public void setStartTime() {
		startTime = System.nanoTime();
	}

	/**
	 * Sets the end time of the game.
	 */
	public void setEndTime() {
		endTime = System.nanoTime();
	}

	/**
	 * @return The duration of a single game in milliseconds
	 */
	public long getDuration() {
		return (endTime - startTime) / 1000000;
	}
	
	/**
	 * @return The winner of the game, if there is a winner.
	 */
	/*
	 * @requires players.size() == 2;
	 */
	public Player getWinner() {
		Player winner = null;
		if (board.hasWinner()) {
			winner = board.isWinner(players[0].getMark()) ? players[0]
							: players[1];
		}
		return winner;
	}
	
	/**
	 * Directly sets the current player to a given index.
	 * @param current The index of the new current player.
	 */
	/*@
	 * requires current < NUMBER_PLAYERS && current >= 0;
	 * ensures getCurrentPlayer() == getPlayers(current);
	 */
	public void setCurrentPlayer(int current) {
		this.current = current;
	}


}
