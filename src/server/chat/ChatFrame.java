package server.chat;

import java.awt.BorderLayout;

import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import server.Client;

/**
 * Visualises the chat in a frame with an input field.
 * @author Bas Hendrikse
 *
 */
public class ChatFrame extends JFrame {

	private TextArea chatField;
	private Chat chat;

	public ChatFrame(final Chat chat, final Client client) {
		super("Chat with others");
		this.chat = chat;

		setSize(400, 400);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);

		chatField = new TextArea();
		chatField.setEditable(false);
		add(chatField, BorderLayout.CENTER);

		JPanel inputPanel = new JPanel();

		inputPanel.add(new JLabel("Message: "));

		final TextField inputField = new TextField(40);

		inputField.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					client.sendChatMessage(inputField.getText());
					inputField.setText("");
				}
			}
		});

		inputPanel.add(inputField);

		add(inputPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
	}

	public void updateMessage() {
		chatField.setText(chat.getChatLog());
		chatField.repaint();
	}

}
