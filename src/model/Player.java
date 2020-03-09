package model;

/**
 * An abstract Player class for the Connect Four Game.
 * @author Bas Hendrikse
 *
 */
public abstract class Player {

    private String name;
    private Mark mark;

    public Player(String theName, Mark theMark) {
        this.name = theName;
        this.mark = theMark;
    }
    
    public String getName() {
        return name;
    }

    public Mark getMark() {
        return mark;
    }
    
    public abstract void requestMove(Game game);
    
    public void doMove(Game game, int index) {
        game.doMove(index);
    }

}
