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
				Cell cell = map.getCell(new Coordinate(y, x));
				cellsUI[y][x].setBackground(cellColour(cell));
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
		ctrlPanel.setLayout(new GridLayout(10, 1, 0, 10));
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

		// Launch mainUI to the right of the screen
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
					 * Paint robot and visited cells.
					 */
					@Override
					public void paintComponent(Graphics g) {
						super.paintComponent(g);

						/* Don't paint on axis labelling */
						if (actualY >= 0 && actualY < Map.maxY) {
							if (actualX >= 0 && actualX < Map.maxX) {
								Cell currCell = map.getCell(new Coordinate(actualY, actualX));
								paintVisited(this.getWidth(), currCell, g);
								paintRobot(this.getWidth(), actualY, actualX, robot.getFootprint(), g);
								
								/* Show isPermanent flag */
								if (currCell.isPermanentCellType())
									this.setText("P");
								else
									this.setText("");
							}
						}
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
				Main.runSimExplorePerStep();
			}
		});

		/* Explore all Button */
		JButton exploreAll = new JButton("Explore all");
		exploreAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.runSimExploration();
			}
		});

		/* Show the Fastest Path */
		JButton fastestPath = new JButton("Calculate A*Star Fastest Path");
		fastestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.runShowFastestPath();
			}
		});
		
		/* Standby for Fastest Path */
		JButton standbyFastestPath = new JButton("Standby for Fastest Path");
		standbyFastestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		
		/* Start Fastest Path */
		JButton startRealFastestPath = new JButton("Start Fastest Path");
		startRealFastestPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
				Main.runRealStartExploration();
			}
		});

		/* Exploration Section */
		ctrlPanel.add(new JLabel("=== Exploration ===", JLabel.CENTER));

		if (Main.isRealRun) {
			ctrlPanel.add(startRealExplore);
		} else {
			ctrlPanel.add(explorePerStep);
			ctrlPanel.add(exploreAll);
		}

		/* Fastest Path Section */
		ctrlPanel.add(new JLabel("=== Fastest Path ===", JLabel.CENTER));
		ctrlPanel.add(fastestPath);

		/* About Map */
		ctrlPanel.add(new JLabel("=== About Map ===", JLabel.CENTER));
		ctrlPanel.add(printDescriptors);
	}

	/**
	 * Cell colour rule based on <tt>cellType</tt>.
	 * 
	 * @param cell
	 * @return
	 */
	private Color cellColour(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.FINAL_PATH:	// Prioritise showing FINAL_PATH
			return Color.BLUE;
		case Cell.START:
			return Color.YELLOW;
		case Cell.GOAL:
			return Color.GREEN;
		case Cell.WALL:
			return Color.BLACK;
		case Cell.PATH:
			return Color.WHITE;
		default:				// Unknown cells
			return Color.GRAY;
		}
	}

	/**
	 * Paint <tt>Robot</tt> with provided Graphics <tt>g</tt> if <tt>actualY</tt> and <tt>actualX</tt>
	 * matches <tt>robotFootprint</tt>.
	 * 
	 * @param actualY
	 * @param actualX
	 * @param robotFootprint
	 * @param g
	 */
	private void paintRobot(int width, int actualY, int actualX, Coordinate[] robotFootprint, Graphics g) {
		width /= 2;		// For a responsive UI

		// Paint FRONT_LEFT of Robot
		if (actualY == robotFootprint[Robot.FRONT_LEFT].getY() && actualX == robotFootprint[Robot.FRONT_LEFT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
		}

		// Paint FRONT_CENTER of Robot
		if (actualY == robotFootprint[Robot.FRONT_CENTER].getY()
				&& actualX == robotFootprint[Robot.FRONT_CENTER].getX()) {
			g.setColor(Color.DARK_GRAY);
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
		}

		// Paint FRONT_RIGHT of Robot
		if (actualY == robotFootprint[Robot.FRONT_RIGHT].getY()
				&& actualX == robotFootprint[Robot.FRONT_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
		}

		// Paint MIDDLE_LEFT of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_LEFT].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_LEFT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}

		// Paint MIDDLE_CENTER of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_CENTER].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_CENTER].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}

		// Paint MIDDLE_RIGHT of Robot
		if (actualY == robotFootprint[Robot.MIDDLE_RIGHT].getY()
				&& actualX == robotFootprint[Robot.MIDDLE_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}

		// Paint BACK_LEFT of Robot
		if (actualY == robotFootprint[Robot.BACK_LEFT].getY() && actualX == robotFootprint[Robot.BACK_LEFT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
			g.drawOval(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));	// Indicates sensor
		}

		// Paint BACK_CENTER of Robot
		if (actualY == robotFootprint[Robot.BACK_CENTER].getY()
				&& actualX == robotFootprint[Robot.BACK_CENTER].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}

		// Paint BACK_RIGHT of Robot
		if (actualY == robotFootprint[Robot.BACK_RIGHT].getY() && actualX == robotFootprint[Robot.BACK_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(width / 2, width / 2, width, width);
		}
	}

	/**
	 * Paint with provided Graphics <tt>g</tt> to represent visited cells.
	 * 
	 * @param width
	 * @param cell
	 * @param g
	 */
	private void paintVisited(int width, Cell cell, Graphics g) {
		width /= 2;		// For a responsive UI

		if (cell.isVisited() == true) {
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(width / 4, width / 4, (int) (width * 1.5), (int) (width * 1.5));
		}
	}
}
