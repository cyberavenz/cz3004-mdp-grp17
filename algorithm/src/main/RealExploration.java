package main;

import communications.TCPComm;
import entities.Map;
import entities.Robot;

public class RealExploration implements Runnable {

	private boolean done = false;

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + ": Started new RealExploration thread.");

		Main.comms.send(TCPComm.ARDUINO, "SXX");						// Request sensor reading
		String fromArduino = Main.comms.readFrom(TCPComm.ARDUINO); 		// Wait for reading
		Main.exploredMap.actualReveal(Main.robot, fromArduino);			// Read sensors and populate map
		Main.gui.refreshGUI(Main.robot, Main.exploredMap);				// Show it on GUI
		System.out.println("============= END STEP =============\n");

		try {
			while (!done) {
				/* Run exploration for one step */
				done = Main.exploration.executeOneStep(Main.robot, Main.exploredMap);	// Send next movement

				Main.comms.send(TCPComm.ANDROID, genMDFAndroid(Main.exploredMap, Main.robot));
				Main.gui.refreshGUI(Main.robot, Main.exploredMap);						// Show it on GUI

				Thread.sleep(700);														// Don't rush Arduino

				System.out.println(
						"Robot is at: " + Main.robot.getCurrPos().getY() + " " + Main.robot.getCurrPos().getX());
				Main.comms.send(TCPComm.ARDUINO, "SXX");								// Request sensor reading
				fromArduino = Main.comms.readFrom(TCPComm.ARDUINO); 					// Wait for reading
				Main.exploredMap.actualReveal(Main.robot, fromArduino);					// Populate map

				Main.comms.send(TCPComm.ANDROID, genMDFAndroid(Main.exploredMap, Main.robot));
				Main.gui.refreshGUI(Main.robot, Main.exploredMap);						// Show it on GUI

				Thread.sleep(100);														// So your eyes can see the
																							// change

				System.out.println("============= END STEP =============\n");
			}
		} catch (Exception e) {
			// IGNORE ALL INTERRUPTS TO THIS THREAD.
			// EXPLORATION MUST RUN TO THE END.
		}

		System.out.println(Thread.currentThread().getName() + ": RealExploration thread ended.");
	}

	public static String genMDFAndroid(Map map, Robot robot) {
		String toReturn = new String();

		toReturn = "MDF" + "|" + map.getP1Descriptors() + "|" + map.getP2Descriptors() + "|" + robot.getCurrDir() + "|"
				+ (19 - robot.getCurrPos().getY()) + "|" + robot.getCurrPos().getX() + "|" + "0";

		return toReturn;
	}
}
