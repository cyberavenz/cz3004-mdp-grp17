package main;

import communications.TCPComm;

public class RealExploration implements Runnable {

	private boolean done = false;

	@Override
	public void run() {
		System.out.println(":: " + getClass().getName() + " Thread Started ::");

		Main.comms.send(TCPComm.SERIAL, "SXX");							// Request sensor reading
		String fromArduino = Main.comms.readFrom(TCPComm.SERIAL); 		// Wait for reading
		Main.exploredMap.actualReveal(Main.robot, fromArduino);			// Read sensors and populate map
		Main.gui.refreshGUI(Main.robot, Main.exploredMap);				// Show it on GUI
		System.out.println("============= END STEP =============\n");

		try {
			while (!done) {
				/* Run exploration for one step */
				done = Main.explorer.executeOneStep(Main.robot, Main.exploredMap);	// Send next movement
				Main.gui.refreshGUI(Main.robot, Main.exploredMap);					// Show it on GUI

				Thread.sleep(700);													// Don't rush Arduino

				System.out.println(
						"Robot is at: " + Main.robot.getCurrPos().getY() + " " + Main.robot.getCurrPos().getX());
				Main.comms.send(TCPComm.SERIAL, "SXX");								// Request sensor reading
				fromArduino = Main.comms.readFrom(TCPComm.SERIAL); 					// Wait for reading
				
				Main.exploredMap.actualReveal(Main.robot, fromArduino);				// Reveal sensor readings on map

				Main.gui.refreshGUI(Main.robot, Main.exploredMap);					// Show it on GUI

				Thread.sleep(100);													// So your eyes can see the change

				System.out.println("============= END STEP =============\n");
			}
		} catch (Exception e) {
			// IGNORE ALL INTERRUPTS TO THIS THREAD.
			// EXPLORATION MUST RUN TO THE END.
		}

		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}
}
