package gui;

import javax.swing.*;
import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import java.awt.*;

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
	 * Constructor for GUI as a JFrame.
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
		mapPanel = new MapAndRobotJPanel();
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		populateMapPanel();		// Populate Map Panel with cells from _map

		ctrlPanel = new JPanel();
		ctrlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ctrlPanel.setLayout(new GridLayout(10, 1, 10, 10));
		populateCtrlPanel();	// Populate Control Panel with buttons

		mainContainer = this.getContentPane();
		mainContainer.setLayout(new BorderLayout(5, 5));
		mainContainer.add(mapPanel, BorderLayout.WEST);
		mainContainer.add(ctrlPanel);
		this.setSize(1000, 840); // Width, Height
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit mainUI on close

		// Center mainUI in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);
	}

	/**
	 * Populate Cells in the Map Panel.
	 */
	private void populateMapPanel() {
		cellsUI = new JLabel[Map.maxY][Map.maxX];

		// Populate Map Panel
		for (int y = Map.maxY; y >= 0; y--) {		// Additional loop for axis labelling
			for (int x = 0; x <= Map.maxX; x++) {
				int actualY = y - 1;
				int actualX = x - 1;

				JLabel newCell = new JLabel("", JLabel.CENTER);
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
	 * Populate Labels and Buttons in the Control Panel.
	 */
	private void populateCtrlPanel() {
		ctrlPanel.add(new JLabel("MODE: Simulation", JLabel.CENTER));
		ctrlPanel.add(new JButton("Import Map"));
		ctrlPanel.add(new JButton("Exploration"));
		ctrlPanel.add(new JButton("Fastest-path"));
		ctrlPanel.add(new JButton("Test Communications"));
	}

	/**
	 * Custom class for MapAndRobotJPanel as a JPanel.
	 * 
	 * Responsible for producing a responsive grid map.
	 *
	 */
	private class MapAndRobotJPanel extends JPanel {

		private static final long serialVersionUID = 3896801036058623157L;

		public MapAndRobotJPanel() {
			super(new GridLayout(Map.maxY + 1, Map.maxX + 1, 2, 2)); // Additional +1 row & +1 col for axis labelling
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
				System.out.println("c: " + c.getWidth() + " by " + c.getHeight());

				// Final Dimension should be 16:21 (W:H) aspect ratio for 16 cells by 21 cells
				// (including axis labelling)
				int numOfCellsWidth = Map.maxX + 1;		// 16 = 15 + 1
				int numOfCellsHeight = Map.maxY + 1;	// 21 = 20 + 1

				int prefHeight = (int) c.getHeight();
				int prefWidth = (prefHeight / 21) * 16;

				System.out.println("Width: " + prefWidth + " Height: " + prefHeight);

				return (new Dimension(prefWidth, prefHeight));
			}

//			if (c == null) {
//				prefSize = new Dimension((int) d.getWidth(), (int) d.getHeight());
//			} else if (c != null && c.getWidth() > d.getWidth() && c.getHeight() > d.getHeight()) {
//				prefSize = c.getSize();
//			} else {
//				prefSize = d;
//			}
//			int w = (int) prefSize.getWidth();
//			int h = (int) prefSize.getHeight();
//
//			// Get the smaller of the two sizes
//			int s = (w > h ? h : w);
//
//			return new Dimension(s, (int) (s * 1.33));	// Final Dimension should be 3:4.
		}
	}

	/**
	 * UI: Cell colour rule based on cell type.
	 * 
	 * @param cell
	 * @return
	 */
	private static Color cellColour(Cell cell) {
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
}
