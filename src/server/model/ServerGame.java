package server.model;

import view.FourGUIView;
import model.Game;
import model.Player;

/**
 * A server game for the server of the Connnect Four game.
 * @author Bas Hendrikse
 *
 */
public class ServerGame extends Game {

	public ServerGame(Player p1, Player p2) {
		super(p1, p2);
		
		// Remove comments if you want a server GUI.
//		FourGUIView fgv = new FourGUIView(this);
//		getBoard().addObserver(fgv);
//		fgv.start();
	}
	
}
