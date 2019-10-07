package main;

import communications.TCPComm;

public class StandbyRealExploration implements Runnable {

	@Override
	public void run() {
		System.out.println(":: " + getClass().getName() + " Thread Started ::");

		Main.comms.send(TCPComm.SERIAL, "R90|L90");	// Turn South to calibrate first, then turn East
		Main.gui.refreshGUI(Main.robot, Main.exploredMap);

		System.out.println("Waiting for Bluetooth to send STARTE...");

		/* STRICTLY Wait for Android to send START EXPLORATION command */
		while (Main.comms.readFrom(TCPComm.BLUETOOTH) == "STARTE")
			;

		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}
}
