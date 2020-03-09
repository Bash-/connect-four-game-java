package server.control;

import server.Server;
import server.view.ServerView;

/**
 * Shows the GUI of the server and possible to initialise one.
 * @author Bas Hendrikse
 *
 */
public class ServerStarter {

	private Server server;

	public ServerStarter() {
		new ServerView(this);
	}

	public void startServer(int port) {
		Server server = new Server(port);

		server.start();
	}

	public static void main(String[] args) {
		new ServerStarter();
	}

}
