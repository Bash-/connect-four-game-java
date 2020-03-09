package model;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Creates a new LeaderBoard for the Connect Four game. Can read from existing
 * leaderboard file or create a new one.
 * 
 * @author Bas Hendrikse
 */

public class LeaderBoard {
	
// -- Instance Variables

	private ArrayList<ArrayList<Object>> leaderBoard;
	private String textFile;
	private Game game;
	
// -- Constructor

	/**
	 * Creates a leaderboard.
	 * @param game
	 */
	public LeaderBoard(Game g) {
		this.game = g;
		leaderBoard = new ArrayList<ArrayList<Object>>();
		textFile = "Leaderboard.txt";

	}

	/**
	 * Creates a leaderboard.
	 * @param game
	 * @param text, the name of the file of the leaderboard.
	 */
	public LeaderBoard(Game g, String text) throws FileNotFoundException, EOFException {
		this.game = g;
		this.textFile = text;
		BufferedReader br = new BufferedReader(new FileReader(this.textFile));
		leaderBoard = read(br);
	}
	
	// -- Methods

	/*@ pure */
	/**
	 * Returns the leaderboard.
	 * @return Leaderboard
	 */
	public ArrayList<ArrayList<Object>> getLeaderBoard() {
		return leaderBoard;
	}

	/**
	 * Adds the game to the this leaderboard. 
	 */
	public void addGame() {
		boolean hasComputer = false;
		Player[] players = game.getPlayers();
		for (Player p : players) {
			if (p instanceof ComputerPlayer) {
				hasComputer = true;
			}
		}
		if (!hasComputer) {
			Player winner = game.getWinner();

			ArrayList<Object> gameList = new ArrayList<Object>();

			gameList.add(players[0].getName());
			gameList.add(players[1].getName());
			gameList.add(game.getBoard().hasWinner() ? winner.getName() : "--draw--");
			gameList.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
			gameList.add(game.getDuration());

			leaderBoard.add(gameList);

			try {
				write(new PrintWriter(textFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void write(PrintWriter pw) {
		pw.print(toString());
		pw.flush();
	}

	/**
	 * Creates a nice string representation of the leaderboard
	 * so we can write it to a file.
	 * @return String representation of the leaderboard
	 */
	public String toString() {
		String result = "";
		for (ArrayList<Object> v : leaderBoard) {
			result += v.get(0) + " ~ " + v.get(1) + " ~ " + v.get(2) + " ~ " + v.get(3) + " ~ "
							+ v.get(4) + "\n";
		}
		return result;
	}

	/**
	 * Returns the top ten players of this leaderboard.
	 */
	public ArrayList<ArrayList<Object>> getTopTen() {
		ArrayList<ArrayList<Object>> copy = this.leaderBoard;
		Map<ArrayList<Object>, Integer> topMap = new HashMap<ArrayList<Object>, Integer>();
		for (ArrayList<Object> entry : copy) {
			if (entry.get(4) instanceof String) {
				topMap.put(entry, Integer.parseInt((String) entry.get(4)));
			} else {
				long l = (long) entry.get(4);
				topMap.put(entry, (int) l);
			}

		}
		
		topMap = (TreeMap<ArrayList<Object>, Integer>) sortByValue(topMap);

		copy.clear();

		for (ArrayList<Object> k : topMap.keySet()) {
			copy.add(k);
		}
		ArrayList<ArrayList<Object>> topTen = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < 10; i++) {
			if (copy.size() >  i) {
				topTen.add(copy.get(i));
			}
		}

		return topTen;
	}

	/**
	 * Sorts the Map on the values.
	 * @return the sorted Map
	 */
	public static Map sortByValue(Map unsortedMap) {
		Map sortedMap = new TreeMap(new MapComparator(unsortedMap));
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}

	/**
	 * Reads the file an makes an array by splitting
	 * the Strings between ~
	 * @return ArrayList<ArrayList<Object>> (leaderboard)
	 */
	public static ArrayList<ArrayList<Object>> read(BufferedReader br) throws EOFException {
		ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();

		Scanner scan = new Scanner(br);
		while (scan.hasNextLine()) {
			ArrayList<Object> gameResult = new ArrayList<Object>();
			String nextLine = scan.nextLine();
			String[] line = nextLine.split(" ~ ");
			for (int i = 0; i < line.length; i++) {
				gameResult.add(line[i]);
			}
			result.add(gameResult);
		}
		scan.close();
		return result;
	}
}
