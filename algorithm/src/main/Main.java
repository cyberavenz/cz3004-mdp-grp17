package main;

/* Import UI components */
import javax.swing.*;
import java.awt.*;

import map.Map;

public class Main {

	/* Main Program */
	public static void main(String[] args) {
		generateFrame();
	}

	/* Generate Frame */
	private static void generateFrame() {
		JFrame jFrame = new JFrame();	// creating instance of JFrame
		jFrame.setSize(1000, 600);		// 1000 width and 600 height

		// Centre jFrame in the middle of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		jFrame.setLocation(dim.width / 2 - jFrame.getSize().width / 2, dim.height / 2 - jFrame.getSize().height / 2);

		// Load Map Button
		// JButton btnLoadMap = new JButton("Load Map"); // creating instance of JButton
		// btnLoadMap.setBounds(130, 100, 100, 40); // x axis, y axis, width, height
		// jFrame.add(btnLoadMap); // adding button in JFrame

		// jFrame options
		jFrame.setLayout(new GridLayout(Map.maxX, Map.maxY)); // Grid Layout
		jFrame.setVisible(true);	// making the frame visible
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit Main on close
	}

}
