package gui;

import javax.swing.*;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import main.Main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GUI extends JFrame {

	private static final long serialVersionUID = -3014581757826180255L;

	private Container mainContainer;
	private JPanel mapPanel, ctrlPanel;

	private JLabel[][] cellsUI;
	private Robot robot;
	private Map map;

	/**
	 * Constructor for <tt>GUI</tt> as a JFrame.
	 * 
	 * @param robot
	 * @param map
	 */
	public GUI(Robot robot, Map map) {
		super("MDP Group 17 (AY19/20 Sem 1) - Algorithm");

		// Initialise class variables
		this.robot = robot;
		this.map = map;

		// Initialise overall layout
		initLayout();
	}

	/**
	 * Ensures <tt>GUI</tt> is showing the latest instance of <tt>Robot</tt> and <tt>Map</tt>.
	 * 
	 * @param robot
	 * @param map
	 */
	public void refreshGUI(Robot robot, Map map) {
		this.robot = robot;
		this.map = map;

		for (int y = Map.maxY - 1; y >= 0; y--) {
			for (int x = 0; x < Map.maxX; x++) {
				// Note: cellsUI[][] uses actualY and actualX and does not include axis labelling
				cellsUI[y][x].setBackground(cellColour(map.getCell(new Coordinate(y, x))));
			}
		}
		mapPanel.repaint();	// Repaint mapPanel to show updated Robot position
	}

	/**
	 * (Optional) Set mode of GUI visually.
	 * 
	 * @param connected
	 */
	public void setModeColour(boolean connected) {
		if (connected)
			mainContainer.setBackground(new Color(115, 221, 141));
		else
			mainContainer.setBackground(new Color(198, 105, 105));
	}

	/**
	 * Initialise the Container, JPanels and cellsUI.
	 */
	private void initLayout() {
		mapPanel = new MapPanel();
		mapPanel.setOpaque(false);
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		populateMapPanel();		// Populate Map Panel with cells from _map

		ctrlPanel = new JPanel();
		ctrlPanel.setOpaque(false);
		ctrlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ctrlPanel.setLayout(new GridLayout(10, 1, 10, 10));
		populateCtrlPanel();	// Populate Control Panel with buttons

		mainContainer = this.getContentPane();
		mainContainer.setLayout(new BorderLayout(0, 0));
		mainContainer.add(mapPanel, BorderLayout.WEST);
		mainContainer.add(ctrlPanel);
		this.setSize(900, 800); // Width, Height
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit mainUI on close
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// Close socket before exiting (In Real Run Mode)
				if (Main.isRealRun && Main.comms != null)
					Main.comms.close();
			}
		});

		// Center mainUI in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 1 - getSize().width / 1, dim.height / 2 - getSize().height / 2 - 20);
	}

	/**
	 * Custom class for <tt>MapPanel</tt> as a JPanel.
	 * 
	 * Responsible for producing a responsive grid map.
	 *
	 */
	private class MapPanel extends JPanel {

		private static final long serialVersionUID = 3896801036058623157L;

		public MapPanel() {
			// Additional +1 row & +1 col for axis labelling
			super(new GridLayout(Map.maxY + 1, Map.maxX + 1, 2, 2));
		}

		/**
		 * Override the getPreferredSize() of JPanel to always ensure that MapAndRobotJPanel has a dynamic
		 * preferred size so that it maintains the original aspect ratio. In this case, it will return a
		 * JPanel that is always sized with an aspect ratio of 3:4 (15 columns by 20 rows).
		 * 
		 * Referenced from:
		 * https://stackoverflow.com/questions/21142686/making-a-robust-resizable-swing-chess-gui
		 */
		@Override
		public final Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			Component c = getParent();

			if (c == null)
				return d;

			else {
				// Final Dimension should be 16:21 (W:H) aspect ratio for 16 cells by 21 cells
				// (including axis labelling)
				// Use of double for more accuracy -> then round to nearest integer
				double numOfCellsWidth = Map.maxX + 1;	// 16 = 15 + 1
				double numOfCellsHeight = Map.maxY + 1;	// 21 = 20 + 1

				int prefHeight = (int) c.getHeight();
				int prefWidth = (int) Math.round(prefHeight / numOfCellsHeight * numOfCellsWidth);

				return (new Dimension(prefWidth, prefHeight));
			}
		}
	}

	/**
	 * Populate cells and robot location in the <tt>mapPanel</tt>.
	 * 
	 * Responsible for colouring cells by <tt>cellType</tt> and painting of cells occupied by robot.
	 */
	private void populateMapPanel() {
		cellsUI = new JLabel[Map.maxY][Map.maxX];

		// Populate Map Panel
		for (int y = Map.maxY; y >= 0; y--) {		// Additional loop for axis labelling
			for (int x = 0; x <= Map.maxX; x++) {
				int actualY = y - 1;
				int actualX = x - 1;

				JLabel newCell = new JLabel("", JLabel.CENTER) {
					private static final long serialVersionUID = -4788108468649278480L;

					/**
					 * Paint cell if robot is occupying it.
					 */
					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						paintRobot(actualY, actualX, robot.getFootprint(), g);
					}
				};
				newCell.setPreferredSize(new Dimension(40, 40)); // Ensure cell is a square
				newCell.setOpaque(true);

				// If it's the first row / col, perform axis labelling
				if (y == 0 && x == 0) {
					newCell.setText("y/x");
					mapPanel.add(newCell);
				} else if (x == 0) {
					newCell.setText(Integer.toString(actualY));
					mapPanel.add(newCell);
				} else if (y == 0) {
					newCell.setText(Integer.toString(actualX));
					mapPanel.add(newCell);
				}

				// Actual Map Population
				else {
					newCell.setOpaque(true);
					newCell.setBackground(cellColour(map.getCell(new Coordinate(y - 1, x - 1))));

					cellsUI[actualY][actualX] = newCell;
					mapPanel.add(cellsUI[actualY][actualX]);
				}

			}
		}

	}

	/**
	 * Populate Labels and Buttons in the <tt>ctrlPanel</tt>.
	 */
	private void populateCtrlPanel() {
		/* Display Mode */
		String mode = Main.isRealRun ? "Real Run" : "Simulation";
		ctrlPanel.add(new JLabel("MODE: " + mode, JLabel.CENTER));

		/* Exploration (per step) button */
		JButton explorePerStep = new JButton("Exploration (per step)");
		explorePerStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.btnExplorePerStep();
			}
		});

		/* Explore all Button */
		JButton exploreAll = new JButton("Explore all");
		exploreAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.btnExploreAll();
			}
		});

		/* P1 and P2 Descriptors */
		JButton printDescriptors = new JButton("Print P1 and P2");
		printDescriptors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("P1: " + map.getP1Descriptors());
				System.out.println("P2: " + map.getP2Descriptors());
			}
		});

		/* Real Run: Start Explore */
		JButton startRealExplore = new JButton("START EXPLORATION");
		startRealExplore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.btnStartRealExplore();
			}
		});
		
		/* Real Run: Force Stop Explore */
		JButton fStopRealExplore = new JButton("!! FORCE STOP EXPLORATION !!");
		fStopRealExplore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.btnFStopRealExplore();
			}
		});

		if (Main.isRealRun) {
			ctrlPanel.add(startRealExplore);
			ctrlPanel.add(fStopRealExplore);
		} else {
			ctrlPanel.add(explorePerStep);
			ctrlPanel.add(exploreAll);
		}
		ctrlPanel.add(printDescriptors);
	}

	/**
	 * Cell colour rule based on <tt>cellType</tt>.
	 * 
	 * @param cell
	 * @return
	 */
	private Color cellColour(Cell cell) {
		if (cell.isVisited())
			return Color.LIGHT_GRAY;

		switch (cell.getCellType()) {
		case Cell.UNKNOWN:
			return Color.GRAY;
		case Cell.START:
			return Color.YELLOW;
		case Cell.GOAL:
			return Color.GREEN;
		case Cell.PATH:
			return Color.WHITE;
		case Cell.FINAL_PATH:
			return Color.BLUE;
		default:
			return Color.BLACK;
		}
	}

	/**
	 * Paint <tt>Robot</tt> onto <tt>Graphics g</tt> if <tt>actualY</tt> and <tt>actualX</tt> matches
	 * <tt>robotFootprint</tt>.
	 * 
	 * @param actualY
	 * @param actualX
	 * @param robotFootprint
	 * @param g
	 */
	private void paintRobot(int actualY, int actualX, Coordinate[] robotFootprint, Graphics g) {
		// Paint FRONT_LEFT of Robot
		if (actualY == robotFootprint[Robot.FRONT_LEFT].getY() && actualX == robotFootprint[Robot.FRONT_LEFT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
			g.drawOval(9, 9, 16, 16);
		}

		// Paint FRONT_CENTER of Robot
		if (actualY == robotFootprint[Robot.FRONT_CENTER].getY()
				&& actualX == robotFootprint[Robot.FRONT_CENTER].getX()) {
			g.setColor(Color.DARK_GRAY);
			g.fillOval(12, 12, 10, 10);
		}

		// Paint FRONT_RIGHT of Robot
		if (actualY == robotFootprint[Robot.FRONT_RIGHT].getY()
				&& actualX == robotFootprint[Robot.FRONT_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
			g.drawOval(9, 9, 16, 16);
		}

		// Paint MIDDLE_LEFT of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_LEFT].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_LEFT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
		}

		// Paint MIDDLE_CENTER of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_CENTER].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_CENTER].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
		}

		// Paint MIDDLE_RIGHT of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_RIGHT].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
		}

		// Paint BACK_LEFT of Robot
		if (actualY == robotFootprint[Robot.BACK_LEFT].getY() && actualX == robotFootprint[Robot.BACK_LEFT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
			g.drawOval(9, 9, 16, 16);
		}

		// Paint BACK_CENTER of Robot
		if (actualY == robotFootprint[Robot.BACK_CENTER].getY()
				&& actualX == robotFootprint[Robot.BACK_CENTER].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
		}

		// Paint BACK_RIGHT of Robot
		if (actualY == robotFootprint[Robot.BACK_RIGHT].getY() && actualX == robotFootprint[Robot.BACK_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
		}
	}
}
