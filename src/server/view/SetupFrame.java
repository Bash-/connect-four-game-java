package server.view;

import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A frame which a port can be given to start the server on, also displays this computer's IP.
 * @author Bas Hendrikse
 *
 */
public class SetupFrame extends Frame {

	private int port;

	public SetupFrame(final ServerView view) {
		super("Server setup");

		add(new Label("Port: "));

		final TextField tf = new TextField(10);
		tf.setEditable(true);
		tf.setText("1337");
		add(tf);

		setLayout(new FlowLayout());
		setSize(300, 100);
		setLocationRelativeTo(null);

		Button playBtn = new Button("Start");
		add(playBtn);

		playBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					port = Integer.parseInt(tf.getText());
					view.getServerStarter().startServer(port);
				} catch (NumberFormatException e2) {
					System.err.println("The port can only contain numbers");
				}
			}
		});

		try {
			add(new Label("" + InetAddress.getLocalHost()));
		} catch (HeadlessException | UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
}
