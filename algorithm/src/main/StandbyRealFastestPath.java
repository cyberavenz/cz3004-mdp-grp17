package main;

import java.util.ArrayList;

import algorithms.FastestPath;
import communications.TCPComm;
import entities.Cell;
import entities.Coordinate;
import entities.Node;
import entities.Robot.Rotate;

public class StandbyRealFastestPath implements Runnable {

	private FastestPath fp;

	/**
	 * Standby <tt>Robot</tt> for the Fastest Path run. Assumes <tt>Robot</tt> is at start position (1, 1) facing
	 * South.<br>
	 * <ol>
	 * <li>Run calibration</li>
	 * <li>Rotate to direction of fastest path</li>
	 * </ol>
	 */
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + ": Started new StandbyFastestPath thread.");

		this.fp = new FastestPath(Main.exploredMap, new Coordinate(1, 1), new Coordinate(18, 13));
		ArrayList<Node> finalPath = fp.runAStar();

		Main.exploredMap.finalPathReveal(finalPath);
		Main.gui.refreshGUI(Main.robot, Main.exploredMap);

		Cell secondCell = finalPath.get(1).getCell();		// Second step

		try {
			// Fastest Path goes North
			if (secondCell.getY() == 2 && secondCell.getX() == 1) {
				Main.comms.send(TCPComm.SERIAL, "C");		// Calibrate
				Thread.sleep(2000);
				Main.robot.rotate(Rotate.RIGHT);
				Thread.sleep(2000);
				Main.robot.rotate(Rotate.RIGHT);
				Thread.sleep(2000);
			}

			// Fastest Path goes East
			else {
				Main.comms.send(TCPComm.BLUETOOTH, "C");		// Calibrate
				Thread.sleep(2000);
				Main.robot.rotate(Rotate.LEFT);
				Thread.sleep(2000);
			}

			/* Wait for Android to send START EXPLORATION command */
			while (Main.comms.readFrom(TCPComm.BLUETOOTH) == "STARTF") {
				if (!Thread.currentThread().isInterrupted())
					break;
			}

			Main.robot.sendFastestPath(fp.navigateSteps());		// Sends to Arduino and Android

			System.out.println(Thread.currentThread().getName() + ": StandbyFastestPath thread ended.");
		} catch (Exception e) {
		}
	}
}
