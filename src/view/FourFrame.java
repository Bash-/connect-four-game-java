package view;

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import model.Board;
import model.ComputerPlayer;
import model.Game;
import model.Mark;
import model.Player;
import model.SmartStrategy;
import server.model.ClientGame;

/**
 * A frame containing all buttons on which a new move can be set for the Connect Four game. 
 * Displays the current game state and has buttons to open chat, open the leaderboard and play again.
 * @author Bas Hendrikse
 *
 */
public class FourFrame extends JFrame {
	private JButton[] buttons;
	private Board board;
	private Game game;
	private Button resetBtn;
	private Label statusLbl;

	private JPanel controls;

	protected FourGUIView view;

	public FourFrame(final FourGUIView view) {
		super("Connect Four");

		this.view = view;
		this.board = this.view.getBoard();
		this.game = this.view.getGame();

		setLayout(new BorderLayout());
		setSize(500, 500);
		
		Dimension windowSize = getSize();		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2 - 100;    
        setLocation(dx, dy);
        
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(Board.HEIGTH, Board.WIDTH, 2, 2));
		buttonPanel.setBackground(Color.DARK_GRAY);
		controls = new JPanel();
		
		controls.setLayout(new GridLayout(2, 1));
		buttons = new JButton[Board.HEIGTH * Board.WIDTH];
		
		for (int i = 0; i < buttons.length; i++) {
			final int index = i;
			buttons[i] = new RoundButton();
			buttons[i].setBackground(Color.LIGHT_GRAY);
			buttons[i].setBorderPainted(false);
			buttons[i].setSize(10, 10);
			buttons[i].setEnabled(true);
			buttonPanel.add(buttons[i]);

			buttons[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (board.isField(board.getHighestField(Board.indexToCol(index)))) {
						game.getCurrentPlayer().doMove(game, index);
					} else {
						view.showError("There is no available empty field in this column");
					}
				}
			});
		}
		
		JPanel firstRow = new JPanel();

		Button menuBtn = new Button("Check LeaderBoard");

		menuBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				view.getLeaderboardFrame().setVisible(true);
			}

		});
		firstRow.add(menuBtn);
		
		if (game instanceof ClientGame) {
			Button chatBtn = new Button("Chat");
			chatBtn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					view.getChatFrame().setVisible(true);
				}
			});
			firstRow.add(chatBtn);
		}
		
		resetBtn = new Button("Play again?");
		resetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (game instanceof ClientGame) {
					view.getClient().sendRematch();
				} else {
					game.reset();
					statusLbl.setText(game.getCurrentPlayer().getName() + "'s turn");
					game.requestMove();
				}
			}

		});
		resetBtn.setEnabled(false);
		firstRow.add(resetBtn);

		JPanel secondRow = new JPanel();
		secondRow.add(new Label("Game status: "));
		
		statusLbl = new Label(game.getCurrentPlayer().getName() + "'s turn");
		secondRow.add(statusLbl);
		
		if (game instanceof ClientGame) {
			firstRow.setLayout(new GridLayout(1, 3));
		} else {
			firstRow.setLayout(new GridLayout(1, 2));
		}
		controls.add(firstRow);
		secondRow.setLayout(new GridLayout(1, 2));
		controls.add(secondRow);
		
		add(buttonPanel, BorderLayout.CENTER);
		add(controls, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public void setGameOverStatus() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(false);
			buttons[i].setBackground(board.getField(i).getColor());
		}
		resetBtn.setEnabled(true);

		if (board.hasWinner()) {
			Player winner = game.getWinner();
			statusLbl.setText("Speler " + winner.getName() + " ("
			      + winner.getMark().toString() + ") has won!");
		}	else if (game instanceof ClientGame 
		    && ((ClientGame) game).getConnectionLostWinner() != null) {
			statusLbl.setText("Speler " +
			    ((ClientGame) game).getConnectionLostWinner().getName() + " ("
					  + ((ClientGame) game).getConnectionLostWinner().getMark().toString() + ") has won!");
		} else {
			statusLbl.setText("Draw. There is no winner!");
		}	
	}
	
	public int getHints() {
		SmartStrategy strategy = new SmartStrategy();
		int hint = strategy.requestHint(this.game, game.getCurrentPlayer());
		int result = Board.indexToCol(hint);
		return result;
		
	}
	
	public void resetLabel(){
		statusLbl.setText(game.getCurrentPlayer().getName() + "'s turn");
	}

	public void update(Mark[] fields) {
		statusLbl.setText(game.getNextPlayer().getName() + "'s turn");
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setEnabled(false);
			buttons[i].setForeground(board.getField(i).getColor());
			buttons[i].setBackground(board.getField(i).getColor());
			buttons[i].setEnabled(true);
		}
		if (!board.isFull()) {
			buttons[getHints()].setBackground(Color.GREEN);
		}
		resetBtn.setEnabled(false);

	}

}
