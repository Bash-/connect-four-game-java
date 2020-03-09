package model;

/**
 * An interface with two standard methods for a strategy.
 * @author Martijn Gemmink & Bas Hendrikse
 *
 */
public interface Strategy {

	public String getName();
	public void requestMove(Game g, ComputerPlayer p);
}
