package main;

import algorithms.Exploration;

import algorithms.FastestPath;
import communications.TCPComm;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import gui.GUI;

public class Main {

	/** Set Mode here **/
	public static boolean isRealRun = false;

	/* Shared Variables */
	public static Robot robot;
	public static Map exploredMap;
	public static GUI gui;
	public static Exploration explorer;
	public static FastestPath fp;

	/* Simulation Only Variables */
	public static Map testMap;
	public static Thread tSimExplore;

	/* Real Run Only Variables */
	public static TCPComm comms;
	public static Thread tStandbyRealExplore;
	public static Thread tRealExplore;
	public static Thread tStandbyRealFastestPath;

	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		/* 1. Initialise Variables */
		robot = new Robot(isRealRun);			// Default starting position of robot (1,1 facing East)
		exploredMap = new Map("unknown.txt");	// Set exploredMap (starts from an unknown state)

		/* 2. Initialise GUI */
		gui = new GUI(robot, exploredMap);

		/* 3. Initialise algorithms */
		explorer = new Exploration(exploredMap);	// Exploration algorithm

		/* 4. Check if current mode */
		/* ======================== REAL RUN MODE ======================== */
		if (isRealRun) {
			gui.setModeColour(false);
			comms = new TCPComm();		// Initialise TCP Communication (Will wait...)
			gui.setModeColour(comms.isConnected());

			try {
				Thread.sleep(2000);		// Raspberry Pi needs time to get ready
			} catch (Exception e) {
			}

			/** 1. Wait for Android to send STARTE **/
			tStandbyRealExplore = new Thread(new StandbyRealExploration());
			tStandbyRealExplore.start();

			try {
				tStandbyRealExplore.join();	// Wait until above thread ends...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/** 2. RUNNING REAL EXPLORATION **/
			tRealExplore = new Thread(new RealExploration());
			tRealExplore.start();

			try {
				tRealExplore.join();	// Wait until above thread ends...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/** 3. RUNNING STANDBY FASTEST PATH and wait for Android to send STARTF... **/
			tStandbyRealFastestPath = new Thread(new StandbyRealFastestPath());
			tStandbyRealFastestPath.start();

			try {
				tStandbyRealFastestPath.join();	// Wait until above thread ends...
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/** 4. SEND STRING TO SERIAL AND BLUETOOTH **/
			comms.sendFastestPath(fp.navigateSteps());
		}

		/* ======================== SIMULATION MODE ======================== */
		else {
			// Load testMap
			testMap = new Map("prevSemWeek9.txt");	// Set simulatedMap for use
			gui.refreshGUI(robot, testMap); 		// Display testMap first if simulation mode
		}
	}

	/**
	 * <b>SIMULATION ONLY</b><br>
	 * Static function called by <tt>GUI</tt> when "Exploration (per step)" button is pressed.
	 * 
	 */
	public static void runSimExplorePerStep() {
		exploredMap.simulatedReveal(robot, testMap);
		gui.refreshGUI(robot, exploredMap);

		/* Run exploration for one step */
		boolean done = explorer.executeOneStep(robot, exploredMap);

		exploredMap.simulatedReveal(robot, testMap);
		gui.refreshGUI(robot, exploredMap);

		if (done) {
			// Reset all objects to a clean state
			robot = new Robot(isRealRun);
			exploredMap = new Map("unknown.txt");
			explorer = new Exploration(exploredMap);
			gui.refreshGUI(robot, exploredMap);
		}
	}

	/**
	 * <b>SIMULATION ONLY</b><br>
	 * Start new <tt>SimulateExploration</tt> thread when "Explore all" button is pressed.
	 */
	public static void runSimExploreAll() {
		if (tSimExplore == null || !tSimExplore.isAlive()) {
			tSimExplore = new Thread(new SimulateExploration());
			tSimExplore.start();
		} else {
			tSimExplore.interrupt();
		}
	}

	/**
	 * <b>SIMULATION ONLY</b><br>
	 * Reveal Fastest Path on GUI. Run Exploration first.
	 */
	public static void runShowFastestPath() {
		fp = new FastestPath(exploredMap, new Coordinate(1, 1), new Coordinate(18, 13));
		exploredMap.finalPathReveal(fp.runAStar());
		gui.refreshGUI(robot, exploredMap);
	}

}
