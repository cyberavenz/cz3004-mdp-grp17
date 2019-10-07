package main;

import communications.TCPComm;

public class StandbyRealExploration implements Runnable {

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + ": Started new RealMain thread.");

		Main.comms.send(TCPComm.BLUETOOTH, RealExploration.genMDFAndroid(Main.exploredMap, Main.robot));
		Main.comms.send(TCPComm.SERIAL, "R90|L90");	// Calibrate first

		/* Wait for Android to send START EXPLORATION command */
		while (Main.comms.readFrom(TCPComm.BLUETOOTH) == "STARTE")
			;

		/* Start Real Exploration */
		Main.runRealStartExploration();

		System.out.println(Thread.currentThread().getName() + ": RealMain thread ended.");
	}
}
