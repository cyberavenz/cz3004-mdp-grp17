package gui;

import javax.swing.*;
import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

	private static final long serialVersionUID = -3014581757826180255L;

	private Container mainContainer;
	private JPanel mapPanel, ctrlPanel;

	private JLabel[][] cellsUI;
	private Robot _robot;
	private Map _map;

	/* TEMP: Just display UI */
	public static void main(String[] args) {
		Map map = new Map();
		Robot robot = new Robot();
		map.importMap("empty.txt");

		GUI mainGUI = new GUI(robot, map);
		mainGUI.setVisible(true);
	}

	/**
	 * Constructor for <tt>GUI</tt> as a JFrame.
	 * 
	 * @param robot
	 * @param map
	 */
	public GUI(Robot robot, Map map) {
		super("MDP Group 17 (AY19/20 Sem 1) - Algorithm");

		// Initialise class variables
		_robot = robot;
		_map = map;

		// Initialise overall layout
		initLayout();
	}

	/**
	 * Initialise the Container, JPanels and cellsUI.
	 */
	private void initLayout() {
		mapPanel = new MapPanel();
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		populateMapPanel();		// Populate Map Panel with cells from _map

		ctrlPanel = new JPanel();
		ctrlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ctrlPanel.setLayout(new GridLayout(10, 1, 10, 10));
		populateCtrlPanel();	// Populate Control Panel with buttons

		mainContainer = this.getContentPane();
		mainContainer.setLayout(new BorderLayout(0, 0));
		mainContainer.add(mapPanel, BorderLayout.WEST);
		mainContainer.add(ctrlPanel);
		this.setSize(1000, 800); // Width, Height
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit mainUI on close

		// Center mainUI in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - getSize().width / 2,
				dim.height / 2 - getSize().height / 2);
	}

	/**
	 * Populate cells and robot location in the <tt>mapPanel</tt>.
	 * 
	 * Responsible for colouring cells by <tt>cellType</tt> and painting of cells
	 * occupied by robot.
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
						paintRobot(actualY, actualX, _robot.getFootprint(), g);
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
					newCell.setBackground(cellColour(_map.getCell(y - 1, x - 1)));

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
		ctrlPanel.add(new JLabel("MODE: Simulation", JLabel.CENTER));
		ctrlPanel.add(new JButton("Import Map"));
		ctrlPanel.add(new JButton("Exploration"));
		ctrlPanel.add(new JButton("Fastest-path"));
		ctrlPanel.add(new JButton("Test Communications"));

		JButton moveForward = new JButton("Move Forward");
		moveForward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_robot.moveForward(1);
				mapPanel.repaint();
			}
		});
		JButton rotateRight = new JButton("Rotate Right");
		rotateRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_robot.rotate(Robot.Rotate.RIGHT);
				mapPanel.repaint();
			}
		});
		JButton rotateLeft = new JButton("Rotate Left");
		rotateLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_robot.rotate(Robot.Rotate.LEFT);
				mapPanel.repaint();
			}
		});
		ctrlPanel.add(moveForward);
		ctrlPanel.add(rotateRight);
		ctrlPanel.add(rotateLeft);
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
			super(new GridLayout(Map.maxY + 1, Map.maxX + 1, 2, 2)); // Additional +1 row & +1 col
																	 // for axis labelling
		}

		/**
		 * Override the getPreferredSize() of JPanel to always ensure that
		 * MapAndRobotJPanel has a dynamic preferred size so that it maintains the
		 * original aspect ratio. In this case, it will return a JPanel that is always
		 * sized with an aspect ratio of 3:4 (15 columns by 20 rows).
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
	 * Cell colour rule based on <tt>cellType</tt>.
	 * 
	 * @param cell
	 * @return
	 */
	private Color cellColour(Cell cell) {
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
	 * Paint <tt>Robot</tt> onto <tt>Graphics g</tt> if <tt>actualY</tt> and
	 * <tt>actualX</tt> matches <tt>robotFootprint</tt>.
	 * 
	 * @param actualY
	 * @param actualX
	 * @param robotFootprint
	 * @param g
	 */
	private void paintRobot(int actualY, int actualX, Coordinate[] robotFootprint, Graphics g) {
		// Paint FRONT_LEFT of Robot
		if (actualY == robotFootprint[Robot.FRONT_LEFT].getY()
				&& actualX == robotFootprint[Robot.FRONT_LEFT].getX()) {
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
		if (actualY == robotFootprint[Robot.BACK_LEFT].getY()
				&& actualX == robotFootprint[Robot.BACK_LEFT].getX()) {
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
		if (actualY == robotFootprint[Robot.BACK_RIGHT].getY()
				&& actualX == robotFootprint[Robot.BACK_RIGHT].getX()) {
			g.setColor(new Color(127, 204, 196));
			g.fillOval(12, 12, 10, 10);
		}
	}
}