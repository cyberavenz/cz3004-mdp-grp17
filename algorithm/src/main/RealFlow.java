package main;

import communications.TCPComm;

public class RealFlow implements Runnable {

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + ": Started new RealMain thread.");

		Main.comms.send(TCPComm.ANDROID, RealExploration.genMDFAndroid(Main.exploredMap, Main.robot));
		Main.comms.send(TCPComm.ARDUINO, "R90|L90");	// Calibrate first

		while (!Thread.currentThread().isInterrupted()) {

			/* Wait for Android to send START EXPLORATION command */
			while (Main.comms.readFrom(TCPComm.ANDROID) == "STARTE")
				;
			Main.btnRealStartExploration();
		}

		System.out.println(Thread.currentThread().getName() + ": RealMain thread ended.");
	}
}
