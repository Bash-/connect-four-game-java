package server.chat;

import java.util.Observable;

/**
 * Saves and adds chat sent by Clients via the Server.
 * @author Bas Hendrikse
 *
 */

public class Chat extends Observable {

	private String chatLog = "";

	public void addMessage(String sender, String message) {
		String currentText = getChatLog();
		chatLog = "[" + sender + "] " + message + "\n" + currentText;
		setChanged();
		notifyObservers("chatMessage");
	}

	public String getChatLog() {
		return chatLog;
	}

}
