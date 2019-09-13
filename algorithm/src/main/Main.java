package main;

import algorithms.Exploration;
import entities.Map;
import entities.Robot;
import gui.GUI;

public class Main {

	public static boolean isRealRun = false;				// RealRun or Simulation mode?
	public static Map simulatedMap = new Map("test1.txt");	// Set simulatedMap for use (if simulation)
	public static Map unknownMap = new Map("unknown.txt");	// Default unknown state
	public static Robot robot = new Robot();				// Default start position

	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Show GUI
		GUI main = new GUI(robot, unknownMap);
	}

	/**
	 * Static function called by <tt>GUI</tt> when "Exploration (per step)" button is pressed.
	 * 
	 */
	public static void explorePerStep() {
		Exploration.execute(robot, unknownMap);
	}

}
