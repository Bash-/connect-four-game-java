package server.view;

import server.control.ServerStarter;

/**
 * A class which controls the GUI frames of the server
 * @author Bas Hendrikse
 *
 */
public class ServerView {

	private ServerStarter serverStarter;

	public ServerView(ServerStarter ss) {
		SetupFrame setupFrame = new SetupFrame(this);
		this.serverStarter = ss;
		setupFrame.setVisible(true);
	}

	public ServerStarter getServerStarter() {
		return serverStarter;
	}
}
