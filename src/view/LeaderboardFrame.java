package view;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

/**
 * A frame containing tabs with different representations of the leaderboard.
 * @author Bas Hendrikse
 *
 */
public class LeaderboardFrame extends Frame {
	
	private FourGUIView view;
	private ArrayList<ArrayList<Object>> leaderBoard;
	private ArrayList<ArrayList<Object>> topTenBoard;
	private Object[][] data;
	private Object[][] topTenData;
	private String[] allScoresNames;
	private String[] topTenNames;
	private JTable allScoresTable;
	private JTable topTenTable;
	private JScrollPane allScrollPane;
	private JScrollPane topTenScrollPane;
	private JTabbedPane tabPane;
	
	
	/**
	 * Creates a frame displaying data from the Leaderboard in a table .
	 * @param v the GUI controller.
	 */
	public LeaderboardFrame(FourGUIView v) {
		super("Four in a row");
		this.view = v;	
		
		//set the size of the frame
		setSize(700, 300);
		setResizable(true);
		
		
		
		//set position of the frame
		Dimension windowSize = getSize();		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y + 202;    
        setLocation(dx, dy);
		
        //the column names for the first table
		String[] columns = {"Player 1", "Player 2", "Winner", "Time", "Duration (ms)"};
		allScoresNames = columns;
        
		 //the column names for the table
		String[] topTenColumns = {"Position", "Player", "Player 2",
		    "Winner", "Time", "Duration (ms)"};
		topTenNames = topTenColumns;
		
		
		update();
		//can exit on pressing the (standard) red x in the top right corner
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				view.getLeaderboardFrame().setVisible(false);
			}
		});
		
	}
	
	/**
	 * Updates the LeaderboardFrame with the current leaderboard.
	 */
	public void update() {
		
		
		if (allScrollPane != null) {
			//remove the current content
			remove(allScrollPane);
		}
		if (topTenScrollPane != null) {
			remove(topTenScrollPane);
		}
		if (tabPane != null) {
			remove(tabPane);
		}
		
		tabPane = new JTabbedPane();
		//request the current leaderboard
		leaderBoard = view.getGame().getLeaderboard().getLeaderBoard();
		
		data = new Object[leaderBoard.size()][5];
		
		int i = 0;
		for (ArrayList<Object> value : leaderBoard) {
			if (value.size() == 5) {
				data[i][0] = value.get(0);
				data[i][1] = value.get(1);
				data[i][2] = value.get(2);
				data[i][3] = value.get(3);
				data[i][4] = value.get(4);
				i++;
			}
		}
		//add the content again to the frame
		allScoresTable = new JTable(data, allScoresNames);
		allScoresTable.setEnabled(false);
		allScoresTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        allScoresTable.setFillsViewportHeight(true);
        allScoresTable.setAutoCreateRowSorter(true);
		allScrollPane = new JScrollPane(allScoresTable);
		
		tabPane.addTab("All games", allScrollPane);
		
		topTenBoard = view.getGame().getLeaderboard().getTopTen();
		topTenData = new Object[topTenBoard.size()][6];
		
		i = 0;
		for (ArrayList<Object> value : topTenBoard) {
			if (value.size() == 5) {
				topTenData[i][0] = i + 1  + ".";
				topTenData[i][1] = value.get(0);
				topTenData[i][2] = value.get(1);
				topTenData[i][3] = value.get(2);
				topTenData[i][4] = value.get(3);
				topTenData[i][5] = value.get(4);
				i++;
			}
		}		
		

		topTenTable = new JTable(topTenData, topTenNames);
		
		topTenTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		
		topTenTable.setEnabled(false);
		topTenTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
		topTenTable.setFillsViewportHeight(true);
		topTenTable.setAutoCreateRowSorter(true);
		topTenScrollPane = new JScrollPane(topTenTable);
		
		tabPane.addTab("Ten fastest games", topTenScrollPane);
		add(tabPane);
		
		
		//refresh the frame
		invalidate();
		validate();
		repaint();
		
	}
	
	
	

}
