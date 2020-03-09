package model;

/**
 * A class for a computerplayer with artificial intelligence.
 * @author Martijn Gemmink & Bas Hendrikse
 *
 */
public class ComputerPlayer extends Player {

	Strategy strat;
	Mark mark;
	
	public ComputerPlayer(String name, Mark mark) {
		super(name, mark);
		this.mark = mark;
		strat = new NaiveStrategy();
	}
	
	public ComputerPlayer(String name, Mark mark, Strategy strategy) {
		super(name, mark);
		this.mark = mark;
		strat = strategy;
	}

	@Override
	public void requestMove(Game game) {
		strat.requestMove(game, this);
	}

}
