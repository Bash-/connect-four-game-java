package model;

import java.util.*;

/**
 * A "dumb" strategy for the ComputerPlayer, does moves randomly.
 * @author Martijn Gemmink & Bas Hendrikse
 *
 */
public class NaiveStrategy implements Strategy {

	@Override
	public String getName() {
		return "Naive";
	}

	@Override
	public void requestMove(Game game, ComputerPlayer p) {

		Board b = game.getBoard();

		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < (Board.WIDTH * Board.HEIGTH); i++) {
			if (b.getField(i) == Mark.EMPTY) {
				set.add(i);
			}
		}

		p.doMove(game, (int) set.toArray()[(int) (Math.random() * (set.size()))]);
	}
}
