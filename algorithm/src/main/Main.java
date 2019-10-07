package main;

import algorithms.Exploration;

import algorithms.FastestPath;
import communications.TCPComm;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import gui.GUI;

public class Main {

	public static boolean isRealRun = false;				// RealRun or Simulation mode?
	public static Map testMap;								// testMap (only used in simulation mode)
	public static Map exploredMap = new Map("unknown.txt");	// Set exploredMap (starts from an unknown state)
	public static Robot robot = new Robot(isRealRun);		// Default starting position of robot

	public static TCPComm comms;
	public static GUI gui;
	public static Exploration exploration;

	public static Thread simExploration;
	public static Thread standbyRealExploration;
	public static Thread realExploration;
	public static Thread standbyRealFastestPath;
//	private static Thread realFastestPath;

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
				Thread.sleep(2000);		// Raspberry Pi needs time to get ready
			} catch (Exception e) {
			}

			// TODO Start RealFlow thread
			standbyRealExploration = new Thread(new StandbyRealExploration());
			standbyRealExploration.start();
		}

		/* SIMULATION MODE */
		else {
			// Load testMap
			testMap = new Map("prevSemWeek9.txt");	// Set simulatedMap for use (if simulation)
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
		boolean done = exploration.executeOneStep(robot, exploredMap);

		exploredMap.simulatedReveal(robot, testMap);
		gui.refreshGUI(robot, exploredMap);

		if (done) {
			// Reset all objects to a clean state
			robot = new Robot(isRealRun);
			exploredMap = new Map("unknown.txt");
			exploration = new Exploration(exploredMap);
		}
	}

	/**
	 * <b>SIMULATION ONLY</b><br>
	 * Start new <tt>SimExploration</tt> thread when "Explore all" button is pressed.
	 */
	public static void runSimExploration() {
		if (simExploration == null || !simExploration.isAlive()) {
			simExploration = new Thread(new SimExploration());
			simExploration.start();
		} else {
			simExploration.interrupt();
		}
	}

	/**
	 * <b>REAL RUN ONLY</b><br>
	 * Start new <tt>RealExploration</tt> thread.
	 */
	public static void runRealStartExploration() {
		if (realExploration == null || !realExploration.isAlive()) {
			realExploration = new Thread(new RealExploration());
			realExploration.start();
		}
	}

	/**
	 * Reveal Fastest Path on GUI (Will not send it to Robot)
	 */
	public static void runShowFastestPath() {
		FastestPath fp = new FastestPath(exploredMap, new Coordinate(1, 1), new Coordinate(18, 13));
		exploredMap.finalPathReveal(fp.runAStar());
		gui.refreshGUI(robot, exploredMap);
	}

	/**
	 * <b>REAL RUN ONLY</b><br>
	 * Start Fastest Path to Arduino and Android.
	 */
	public static void runRealStartFastestPath() {
		// TODO
	}
}
