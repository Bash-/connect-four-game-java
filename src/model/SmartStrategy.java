package model;

import java.util.HashSet;
import java.util.Set;

/**
 * A smart strategy for the ComputerPlayer and hint function.
 * @author Martijn Gemmink
 *
 */
public class SmartStrategy implements Strategy {

	public static final int HEIGTH = Board.HEIGTH;
	public static final int WIDTH = Board.WIDTH;
	public static final int MIDDLE = WIDTH / 2;
	public static final int BOTTOMMIDDLE = WIDTH * HEIGTH - (1 / 2 * WIDTH);

	@Override
	public String getName() {
		return "Smart";
	}

	@Override
	public void requestMove(Game g, ComputerPlayer p) {
		Boolean didMove = false;
		Board b = g.getBoard();
		Mark m = p.getMark();

		// Check for all indexes if when the mark is set, if the player wins
		for (int i = 0; i < Board.WIDTH * Board.HEIGTH; i++) {
			Board boardCopy = b.deepCopy();
			if (boardCopy.getField(i) == Mark.EMPTY && !didMove) {
				boardCopy.setField(i, m);
				if (boardCopy.isWinner(m)) {
					System.out.println("[" + getName() + "] Ik kan vier op een rij krijgen");
					didMove = true;
					p.doMove(g, i);
				}
			}
		}

		// Check for all indexes if when the other mark is set, if the other
		// player wins
		for (int i = 0; i < Board.WIDTH * Board.HEIGTH; i++) {
			Board boardCopy = b.deepCopy();
			if (boardCopy.getField(i) == Mark.EMPTY && !didMove) {
				boardCopy.setField(i, m.other());
				if (boardCopy.isWinner(m.other())) {
					System.out.println("[" + getName()
									+ "] De tegenstander kan vier op een rij krijgen");
					didMove = true;
					p.doMove(g, i);
				}
			}
		}

		// Return middle if this is empty
		if ((b.getHighestField(MIDDLE) == 38) && !didMove) {
			System.out.println("[" + getName() + "] Ik pak de middelste kolom");
			didMove = true;
			p.doMove(g, MIDDLE);
		}
		
		if (!didMove && b.getField(38) == m.other() && b.getHighestField(4) == 39) {
			System.out.println("[" + getName() + "] Ik pak de index rechts naast het midden");
			didMove = true;
			p.doMove(g, 4);
		}
		
		
		// Predict if win in two steps
		if (!didMove) {
			Set<Integer> possiblemoves = new HashSet<Integer>();
			for (int i = 0; i < Board.HEIGTH * Board.WIDTH; i++) {
				if (!(b.getHighestField(Board.indexToCol(i)) == -1) && b.getField(b.getHighestField(Board.indexToCol(i))) == Mark.EMPTY && !didMove) {
					int first = i;
					for (int i2 = 0; i2 < Board.WIDTH * Board.HEIGTH; i2++) {
						Board boardCopy = b.deepCopy();
						if (boardCopy.getField(i2) == Mark.EMPTY && !didMove) {
							boardCopy.setField(first, m);
							if(!(boardCopy.getHighestField(Board.indexToCol(i2)) == -1)){
								boardCopy.setField(i2, m);
							}
							
							if (boardCopy.isWinner(m)) {
								possiblemoves.add(i);
								possiblemoves.add(i2);
							}
						}
					}
				}
			}
			if (possiblemoves.size() > 0 && !didMove) {
				didMove = true;
				System.out.println("[" + getName() + "] Ik kan vier op een rij "
								+ "krijgen in twee stappen");
				p.doMove(g, (int) possiblemoves.toArray()[(int) (Math.random() * (possiblemoves
								.size()))]);
			}
		}
		
		// Do a random move but check if the opponent can do a winning move.
		if (!didMove) {
			Set<Integer> possiblemoves = new HashSet<Integer>();
			for (int i = 0; i < Board.HEIGTH * Board.WIDTH; i++) {
				if (b.getField(i) == Mark.EMPTY && !didMove) {
					Board boardcopy3 = b.deepCopy();
					for (int j = 0; j < Board.HEIGTH * Board.WIDTH; j++) {
						if (boardcopy3.isEmptyField(j)) {
							boardcopy3.setField(j, m);
							if (!boardcopy3.isWinner(m.other())) {
								possiblemoves.add(i);
							}
						}
					}
				}
			}

			if (possiblemoves.size() == 0 && !didMove) {
				Set<Integer> lastmoves = new HashSet<Integer>();
				for (int i = 0; i < Board.HEIGTH * Board.WIDTH; i++) {
					lastmoves.add(i);
				}
				didMove = true;
				p.doMove(g, (int) lastmoves.toArray()[(int) (Math.random() * (lastmoves.size()))]);
			}
			if (possiblemoves.size() > 0 && !didMove) {
				didMove = true;
				p.doMove(g, (int) possiblemoves.toArray()[(int) (Math.random() * (possiblemoves
								.size()))]);
			}
		}
	}

	public int requestHint(Game g, Player p) {
		Boolean didMove = false;
		Board b = g.getBoard();
		Mark m = p.getMark();
		int bestMove = -1;

		// Check for all indexes if when the mark is set, if the player wins
		for (int i = 0; i < Board.WIDTH * Board.HEIGTH; i++) {
			Board boardCopy = b.deepCopy();
			if (boardCopy.getField(i) == Mark.EMPTY && !didMove) {
				boardCopy.setField(i, m);
				if (boardCopy.isWinner(m)) {
					didMove = true;
					bestMove = i;
				}
			}
		}

		// Check for all indexes if when the other mark is set, if the other
		// player wins
		for (int i = 0; i < Board.WIDTH * Board.HEIGTH; i++) {
			Board boardCopy = b.deepCopy();
			if (boardCopy.getField(i) == Mark.EMPTY && !didMove) {
				boardCopy.setField(i, m.other());
				if (boardCopy.isWinner(m.other())) {
					didMove = true;
					bestMove = i;
				}
			}
		}

		// Return middle if this is empty
		if ((b.getHighestField(MIDDLE) == 38) && !didMove) {
			didMove = true;
			bestMove = MIDDLE;
		}

		// Check if you are one step away from winning:
		Set<Integer> bestpossiblemoves = new HashSet<Integer>();
		for (int i = 0; i < WIDTH * HEIGTH; i++) {
			Board boardCopy1 = b.deepCopy();
			if (boardCopy1.getField(i) == Mark.EMPTY && !didMove) {
				boardCopy1.setField(i, m);
				Board boardCopy2 = boardCopy1.deepCopy();
				for (int j = 0; j < WIDTH * HEIGTH; j++) {
					if (boardCopy2.getField(j) == Mark.EMPTY && !didMove) {
						boardCopy2.setField(j, m);
						if (boardCopy2.isWinner(m) && !didMove) {
							bestpossiblemoves.add(i);
						}
					}
				}
			}
		}
		if (bestpossiblemoves.size() > 0 && !didMove) {
			didMove = true;
			bestMove = (int) bestpossiblemoves.toArray()[(int) (Math.random() * (bestpossiblemoves
							.size()))];
		}

		if (!didMove) {
			// If none of the above applies, do a random move
			Set<Integer> possiblemoves = new HashSet<Integer>();
			for (int i = 0; i < Board.HEIGTH * Board.WIDTH; i++) {
				if (b.getField(i) == Mark.EMPTY && !didMove) {
					possiblemoves.add(i);
				}
			}
			bestMove = (int) possiblemoves.toArray()[(int) (Math.random() * (possiblemoves.size()))];
		}

		return bestMove;
	}

}
