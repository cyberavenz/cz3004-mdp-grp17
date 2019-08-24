package main;

/* Import UI components */
import javax.swing.*;
import java.awt.*;

import map.Map;

public class UI {

	/* TEMP: Just display UI */
	public static void main(String[] args) {

		Map map = new Map();
		showUI(map);
	}

	/* Generate Main UI */
	private static void showUI(Map map) {
		JFrame mainUI = new JFrame();		// Creating main UI (JFrame)
		JPanel mapPanel = new JPanel(); 	// Creating map panel
		JPanel ctrlPanel = new JPanel();	// Creating control panel

		mainUI.setSize(1000, 640);			// width, height
		mapPanel.setBounds(0, 0, 800, 600);	// x axis, y axis, width, height
		mapPanel.setLayout(new GridLayout(map.maxX, map.maxY));
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		ctrlPanel.setBounds(800, 0, 200, 600);
		mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Centre jFrame in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainUI.setLocation(dim.width / 2 - mainUI.getSize().width / 2, dim.height / 2 - mainUI.getSize().height / 2);

		// Load Map Button
		// JButton btnLoadMap = new JButton("Load Map"); // creating instance of JButton
		// btnLoadMap.setBounds(130, 100, 100, 40); // x axis, y axis, width, height
		// jFrame.add(btnLoadMap); // adding button in JFrame

		// Load cells from map
		for (int x = 0; x < map.maxX; x++) {
			for (int y = 0; y < map.maxY; y++) {
				JButton btn = new JButton();
				btn.setEnabled(false);
				mapPanel.add(btn);
			}
		}

		// jFrame options
		mainUI.add(mapPanel);
		mainUI.add(ctrlPanel);
		mainUI.setVisible(true);	// making the frame visible
		mainUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit Main on close
	}

}
