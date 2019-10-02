package main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import algorithms.Exploration;
import communications.TCPComm;
import entities.Map;
import entities.Robot;
import gui.GUI;

public class Main {

	public static boolean isRealRun = true;				// RealRun or Simulation mode?
	public static Map testMap;								// testMap (only used in simulation mode)
	public static Map exploredMap = new Map("unknown.txt");	// Set exploredMap (starts from an unknown state)
	public static Robot robot = new Robot();				// Default starting position of robot

	public static TCPComm comms;
	public static GUI gui;
	public static Exploration exploration;

	private static ScheduledExecutorService explorationExecutor;
	private static Thread realExplorationThread;

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

			try {
				Thread.sleep(3000);
			} catch (Exception e) {
			}

			comms.send(TCPComm.ARDUINO, "R90|L90");
		}

		// SIMULATION MODE
		else {
			// Load testMap
			testMap = new Map("test3.txt");	// Set simulatedMap for use (if simulation)
			gui.refreshGUI(robot, testMap); // Display testMap first if simulation mode
			
			
		}
	}

	/**
	 * Static function called by <tt>GUI</tt> when "Exploration (per step)" button is pressed.
	 * 
	 */
	public static void btnExplorePerStep() {
		exploredMap.simulatedReveal(robot, testMap);
		gui.refreshGUI(robot, exploredMap);

		/* Run exploration for one step */
		exploration.executeOneStep(robot, exploredMap);

		exploredMap.simulatedReveal(robot, testMap);
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
				exploredMap.simulatedReveal(robot, testMap);
				// Run exploration for one step
				boolean done = exploration.executeOneStep(robot, exploredMap);
				exploredMap.simulatedReveal(robot, testMap);
				gui.refreshGUI(robot, exploredMap);

				if (done)
					explorationExecutor.shutdown();
			}
		};

		explorationExecutor.scheduleAtFixedRate(explorable, 0, 100, TimeUnit.MILLISECONDS);
	}

	public static void btnStartRealExplore() {
		/* Don't start again if thread is alive */
//		if (realExplorationThread != null || realExplorationThread.isAlive())
//			return;
		
//		comms.send(TCPComm.ARDUINO, "CXX");		// Request sensor reading
		comms.send(TCPComm.ARDUINO, "SXX");		// Request sensor reading
		exploredMap.actualReveal(robot, comms.readFrom(TCPComm.ARDUINO));		// Read sensors and populate map
		gui.refreshGUI(robot, exploredMap);		// Show it on GUI

		Runnable realExplorable = new Runnable() {
			private boolean done = false, exit = false;

			@Override
			public void run() {
				exit = false;
				System.out.println("Inside realExplorable thread: " + Thread.currentThread().getName());

				do {
					/* To exit cleanly before each exploration step */
					if (exit)
						return;

					try {
						/* Run exploration for one step */
						done = exploration.executeOneStep(robot, exploredMap);	// Send next movement

						Thread.sleep(1000);

						comms.send(TCPComm.ARDUINO, "SXX");		// Request sensor reading
						exploredMap.actualReveal(robot, comms.readFrom(TCPComm.ARDUINO));		// Read sensors and
																									// populate map
						gui.refreshGUI(robot, exploredMap);		// Show it on GUI
					} catch (Exception e) {
					}
				} while (!done);
			}
		};

		realExplorationThread = new Thread(realExplorable);

		System.out.println("Starting realExploration thread...");
		realExplorationThread.start();
	}

	public static void btnFStopRealExplore() {
	}
}
