package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import algorithms.Exploration;
import communications.TCPComm;
import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import entities.Sensor;
import gui.GUI;

public class Main {

	public static boolean isRealRun = false;				// RealRun or Simulation mode?
	public static Map testMap;								// testMap (only used in simulation mode)
	public static Map exploredMap = new Map("unknown.txt");	// Set exploredMap (starts from an unknown state)
	public static Robot robot = new Robot();				// Default starting position of robot

	public static TCPComm comms;
	public static GUI gui;
	public static Exploration exploration;

	public static ScheduledExecutorService explorationExecutor;

	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/* 1. Initialise GUI */
		gui = new GUI(robot, exploredMap);
		gui.setVisible(true);

		/* 2. Initialise algorithms */
		exploration = new Exploration(exploredMap);		// Exploration algorithm

		/* 3. Check if real run? */
		// REAL RUN MODE
		if (isRealRun) {
			gui.setModeColour(false);
			comms = new TCPComm();
			gui.setModeColour(comms.isConnected());
		}

		// SIMULATION MODE
		else {
			// Load testMap
			testMap = new Map("test3.txt");	// Set simulatedMap for use (if simulation)
			// Display testMap first if simulation mode
			gui.refreshGUI(robot, testMap);
		}
	}

	/**
	 * Static function called by <tt>GUI</tt> when "Exploration (per step)" button is pressed.
	 * 
	 */
	public static void btnExplorePerStep() {
		updateExploredMap();

		/* Run exploration for one step */
		exploration.executeOneStep(robot, exploredMap);

		updateExploredMap();
		gui.refreshGUI(robot, exploredMap);
	}

	/**
	 * Static function called by <tt>GUI</tt> when "Explore all" button is pressed.
	 */
	public static void btnExploreAll() {
		// Reset all objects to clean state first
		robot = new Robot();
		exploredMap = new Map("unknown.txt");
		exploration = new Exploration(exploredMap);

		// Only 1 instance should be running. Cancel previous executor if it exists.
		if (explorationExecutor != null)
			explorationExecutor.shutdown();

		// Assign a new thread pool
		explorationExecutor = Executors.newScheduledThreadPool(1);

		// Create a Runnable task
		Runnable explorable = new Runnable() {
			@Override
			public void run() {
				updateExploredMap();
				// Run exploration for one step
				boolean done = exploration.executeOneStep(robot, exploredMap);
				updateExploredMap();
				gui.refreshGUI(robot, exploredMap);

				if (done)
					explorationExecutor.shutdown();
			}
		};

		explorationExecutor.scheduleAtFixedRate(explorable, 0, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * Static function called by <tt>GUI</tt> when "Print P1 and P2" button is pressed.
	 */
	public static void btnPrintDescriptors() {
		System.out.println("P1: " + Map.getP1Descriptors(exploredMap));
		System.out.println("P2: " + Map.getP2Descriptors(exploredMap));
	}

	/**
	 * Update <tt>unknownMap</tt> based on what the <tt>Sensors</tt> from <tt>Robot</tt> sees.
	 */
	private static void updateExploredMap() {
		if (isRealRun) {
			// TODO update unknownMap based on what sensor sees
		}

		/* Simulated update */
		else {
			Sensor[] sensors = robot.getAllSensors();
			Coordinate[] coordinates;

			for (int i = 0; i < sensors.length; i++) {
				coordinates = sensors[i].getFacingCoordinates(robot);

				// Only when sensor sees some coordinates
				if (coordinates != null) {
					for (int j = 0; j < coordinates.length; j++) {
						Cell unknownCell = exploredMap.getCell(coordinates[j]);
						Cell simulatedCell = testMap.getCell(coordinates[j]);
						unknownCell.setCellType(simulatedCell.getCellType());

						// Sensor should not be able to see past walls
						if (simulatedCell.getCellType() == Cell.WALL)
							break;
					}
				}
			}
		}
	}

}
