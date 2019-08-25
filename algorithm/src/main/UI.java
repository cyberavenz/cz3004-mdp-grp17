package main;

import javax.swing.*;
import java.awt.*;

import map.Cell;
import map.Map;

public class UI {

	private static JButton[][] cellsUI = new JButton[Map.maxY][Map.maxX];

	/* TEMP: Just display UI */
	public static void main(String[] args) {

		Map map = new Map();
		 map.importMap("unknown.txt");
		showUI(map);
	}

	/* Generate Main UI */
	private static void showUI(Map map) {
		JFrame mainUI = new JFrame();		// Creating main UI (JFrame)
		JPanel container = new JPanel();	// Creating container for panels
		JPanel mapPanel = new JPanel(); 	// Creating map panel
		JPanel ctrlPanel = new JPanel();	// Creating control panel

		// Panel Options
		container.setLayout(new BoxLayout(container, BoxLayout.X_AXIS));
		mapPanel.setLayout(new GridLayout(Map.maxY, Map.maxX));
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));	// Top, Left, Bottom, Right
		ctrlPanel.setLayout(new GridLayout(10, 1));
		ctrlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

		// Populate Map Panel
		for (int y = Map.maxY - 1; y >= 0; y--) {
			for (int x = 0; x < Map.maxX; x++) {
				cellsUI[y][x] = new JButton();
				cellsUI[y][x].setEnabled(false);
				cellsUI[y][x].setPreferredSize(new Dimension(10,10));
				cellsUI[y][x].setBackground(cellColour(map.getCell(y, x)));

				mapPanel.add(cellsUI[y][x]);
			}
		}

		// Populate Control Panel
		JButton btnImportMap = new JButton("Import Map");
		ctrlPanel.add(btnImportMap);
		JButton btnExportMap = new JButton("Export Map");
		ctrlPanel.add(btnExportMap);

		// Main UI Options
		container.add(mapPanel);
		container.add(ctrlPanel);
		mainUI.setSize(1000, 700);	// Width, Height
		mainUI.add(container);
		mainUI.setVisible(true);	// Making the mainUI visible
		mainUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit MainUI on close

		// Centre jFrame in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainUI.setLocation(dim.width / 2 - mainUI.getSize().width / 2, dim.height / 2 - mainUI.getSize().height / 2);
	}

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
