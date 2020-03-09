package model;

import java.awt.Color;

/**
 * Beschrijf de mogelijke kleuren in het spel.
 * 
 * @author Martijn Gemmink
 * @author Bas Hendrikse
 */
public enum Mark {

	EMPTY(Color.LIGHT_GRAY), RED(Color.RED), YELLOW(Color.YELLOW);

	Color color;

	Mark(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	/*
	 * @ ensures this == Mark.RED ==> \result == Mark.YELLOW; ensures this ==
	 * Mark.YELLOW ==> \result == Mark.RED;
	 */
	/**
	 * Geeft de andere kleur terug.
	 * 
	 * @return de andere kleur, of anders EMPTY.
	 */
	public Mark other() {
		if (this == RED) {
			return YELLOW;
		} else if (this == YELLOW) {
			return RED;
		} else {
			return EMPTY;
		}
	}
}
