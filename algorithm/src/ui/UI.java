package ui;

import javax.swing.*;
import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import java.awt.*;

public class UI {

	private static JLabel[][] cellsUI = new JLabel[Map.maxY][Map.maxX];

	/* TEMP: Just display UI */
	public static void main(String[] args) {

		Map map = new Map();
		Robot robot = new Robot();
		map.importMap("empty.txt");
		initUI(map, robot);
	}

	/* Generate Main UI */
	private static void initUI(Map map, Robot robot) {
		JFrame mainUI = new JFrame("MDP Group 17 - Algorithm");	// Creating main UI (JFrame)
		JPanel container = new JPanel();	// Creating container for panels
		JPanel mapPanel = new JPanel();		// Creating map panel
		JPanel ctrlPanel = new JPanel();	// Creating control panel

		// Panel Options
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		mapPanel.setLayout(new GridLayout(Map.maxY + 1, Map.maxX + 1, 2, 2));	// Additional row & col for axis label
		mapPanel.setMaximumSize(new Dimension(600, 800));
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));	// Top, Left, Bottom, Right
		ctrlPanel.setLayout(new GridLayout(10, 1, 0, 10));
		ctrlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

		// Populate Map Panel
		for (int y = Map.maxY; y >= 0; y--) {		// Additional loop for axis label
			for (int x = 0; x <= Map.maxX; x++) {
				int actualY = y - 1;
				int actualX = x - 1;

				// Add axis label
				if (x == 0 && y == 0)
					mapPanel.add(new JLabel("   y/x"));
				else if (x == 0)
					mapPanel.add(new JLabel("    " + actualY));
				else if (y == 0)
					mapPanel.add(new JLabel("    " + actualX));

				// Actual Map Population
				else {
					JLabel newCell = new JLabel();

					newCell.setOpaque(true);
					newCell.setPreferredSize(new Dimension(40, 40));
					newCell.setBackground(cellColour(map.getCell(y - 1, x - 1)));

					cellsUI[actualY][actualX] = newCell;

					mapPanel.add(cellsUI[actualY][actualX]);
				}
			}
		}

		// Display Robot
		updateRobot(robot);

		// Populate Control Panel
		JButton btnImportMap = new JButton("Import Map");
		ctrlPanel.add(btnImportMap);
		JButton btnExportMap = new JButton("Button B");
		ctrlPanel.add(btnExportMap);

		// Main UI Options
		container.add(mapPanel);
		container.add(ctrlPanel);
		mainUI.add(container);
		mainUI.setSize(900, 800); // Width, Height
		mainUI.setVisible(true); // Making the mainUI visible
		mainUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit mainUI on close

		// Center mainUI in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainUI.setLocation(dim.width / 2 - mainUI.getSize().width / 2, dim.height / 2 - mainUI.getSize().height / 2);
	}

	/* Update Cells in Map */
	public static void updateMap(Map map) {
		for (int y = Map.maxY - 1; y >= 0; y--) {
			for (int x = 0; x < Map.maxX; x++) {
				cellsUI[y][x].setBackground(cellColour(map.getCell(y, x)));
			}
		}
	}

	/* Update Robot Location */
	public static void updateRobot(Robot robot) {
		Coordinate[] currPosAll = robot.getCurrPosAll();
//		int currDir = robot.getCurrDir();

		// Clear all existing cells
		for (int y = Map.maxX; y > 0; y--) {
			for (int x = 1; x < Map.maxX; x++) {
				cellsUI[y][x].setText("");
			}
		}

		// Display robot location
		cellsUI[currPosAll[Robot.FRONT_LEFT].getY()][currPosAll[Robot.FRONT_LEFT].getX()].setText("      •");
		cellsUI[currPosAll[Robot.FRONT_RIGHT].getY()][currPosAll[Robot.FRONT_RIGHT].getX()].setText("      •");
		cellsUI[currPosAll[Robot.BACK_LEFT].getY()][currPosAll[Robot.BACK_LEFT].getX()].setText("      -");
		cellsUI[currPosAll[Robot.BACK_RIGHT].getY()][currPosAll[Robot.BACK_RIGHT].getX()].setText("      -");
	}

	/* Cell Colour Rule */
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
