package view;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import control.ConnectFour;

/**
 * A frame containing input fields which is used to create Players.
 * And the frame contains input fields to connect to a server.
 * @author Bas Hendrikse
 *
 */
public class StartFrame extends Frame {
	
	public StartFrame(final ConnectFour cf) {
		super("Connect Four");
		
		setSize(500, 300);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
		JPanel imagePanel = new JPanel();
		ImageIcon icon = new ImageIcon("src/img/ConnectFour0.5.jpg");	
		JLabel logo = new JLabel(icon);
		imagePanel.add(logo);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridLayout(1, 2));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(0, 2));
		
		leftPanel.add(new Label("Username player 1: "));
		final TextField usernameField1 = new TextField(20);
		usernameField1.setEditable(true);
		usernameField1.setText("PlayerX");
		leftPanel.add(usernameField1);
		
		leftPanel.add(new Label("Type player 1: "));
		final Choice playerTypes1 = new Choice();
		playerTypes1.add("Human");
		playerTypes1.add("Computer-Naive");
		playerTypes1.add("Computer-Smart");
		leftPanel.add(playerTypes1);
		
		leftPanel.add(new Label("Username player 2: "));
		final TextField usernameField2 = new TextField(20);
		usernameField2.setEditable(true);
		usernameField2.setText("PlayerY");
		leftPanel.add(usernameField2);
		
		leftPanel.add(new Label("Type player 2: "));
		final Choice playerTypes2 = new Choice();
		playerTypes2.add("Human");
		playerTypes2.add("Computer-Naive");
		playerTypes2.add("Computer-Smart");
		leftPanel.add(playerTypes2);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(0, 2));
		
		rightPanel.add(new Label("Server ip: "));
		final TextField ipField = new TextField(20);
		ipField.setEditable(true);
		ipField.setText("localhost");
		rightPanel.add(ipField);

		rightPanel.add(new Label("Server port: "));
		final TextField portField = new TextField(20);
		portField.setEditable(true);
		portField.setText("1337");
		rightPanel.add(portField);
		
		rightPanel.add(new Label("Move delay (ms): "));
		final TextField delayField = new TextField(20);
		delayField.setEditable(true);
		delayField.setText("0");
		rightPanel.add(delayField);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		
		Button playOfflineBtn = new Button("Play Offline");
		buttonPanel.add(playOfflineBtn);
		
		playOfflineBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cf.startOfflineGame(usernameField1.getText(), playerTypes1.getSelectedItem(), 
				    usernameField2.getText(), playerTypes2.getSelectedItem());
			}

		});
		
		Button playOnlineBtn = new Button("Play Online");
		buttonPanel.add(playOnlineBtn);
		
		playOnlineBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					cf.startOnlineGame(usernameField1.getText(),
					    InetAddress.getByName(ipField.getText()), 
					    Integer.parseInt(portField.getText()), playerTypes1.getSelectedItem(), 
					    Long.parseLong(delayField.getText()));
				} catch (NumberFormatException | UnknownHostException e1) {
					e1.printStackTrace();
				}
			}

		});
		
		inputPanel.add(leftPanel);
		inputPanel.add(rightPanel);
		
		add(imagePanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

}
