package main;

import communications.TCPComm;

public class StandbyWaypoint implements Runnable {

	@Override
	public void run() {
		System.out.println(":: " + getClass().getName() + " Thread Started ::");
		
		System.out.println("Waiting for Bluetooth to send Waypoint...");

		/* STRICTLY Wait for Android to send START EXPLORATION command */
		while (Main.comms.readFrom(TCPComm.BLUETOOTH) == "STARTE")
			;

		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}

}
