package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import algorithms.Exploration;
import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import entities.Sensor;
import gui.GUI;

public class Main {

	public static boolean isRealRun = false;				// RealRun or Simulation mode?
	public static Map simulatedMap = new Map("test1.txt");	// Set simulatedMap for use (if simulation)
	public static Map unknownMap = new Map("unknown.txt");	// Default unknown state
	public static Robot robot = new Robot();				// Default start position

	public static Exploration exploration = new Exploration(unknownMap); // Exploration algorithm
	public static GUI gui = new GUI(robot, unknownMap);		// New GUI instance

	public static ScheduledExecutorService explorationExecutor;

	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Show GUI
		gui.setVisible(true);
		
		System.out.println(Map.getP1Descriptors(simulatedMap));
//		System.out.println(Map.getP2Descriptors(simulatedMap));
	}

	/**
	 * Static function called by <tt>GUI</tt> when "Exploration (per step)" button is pressed.
	 * 
	 */
	public static void btnExplorePerStep() {
		updateUnknownMap();

		/* Run exploration for one step */
		exploration.executeOneStep(robot, unknownMap);

		updateUnknownMap();
		gui.refreshGUI(robot, unknownMap);
	}

	public static void btnExploreAll() {
		// Reset all objects to clean state first
		robot = new Robot();
		unknownMap = new Map("unknown.txt");
		exploration = new Exploration(unknownMap);

		// Only 1 instance should be running. Cancel previous executor if it exists.
		if (explorationExecutor != null)
			explorationExecutor.shutdown();

		// Assign a new thread pool
		explorationExecutor = Executors.newScheduledThreadPool(1);

		// Create a Runnable task
		Runnable explorable = new Runnable() {
			@Override
			public void run() {
				updateUnknownMap();
				// Run exploration for one step
				boolean done = exploration.executeOneStep(robot, unknownMap);
				updateUnknownMap();
				gui.refreshGUI(robot, unknownMap);

				if (done)
					explorationExecutor.shutdown();
			}
		};

		explorationExecutor.scheduleAtFixedRate(explorable, 0, 100, TimeUnit.MILLISECONDS);
	}

	/**
	 * Update <tt>unknownMap</tt> based on what the <tt>Sensors</tt> from <tt>Robot</tt> sees.
	 */
	private static void updateUnknownMap() {
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
						Cell unknownCell = unknownMap.getCell(coordinates[j]);
						Cell simulatedCell = simulatedMap.getCell(coordinates[j]);
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
