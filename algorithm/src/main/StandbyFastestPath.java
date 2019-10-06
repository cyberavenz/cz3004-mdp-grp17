package main;

import java.util.ArrayList;

import communications.TCPComm;
import entities.Cell;
import entities.Node;
import entities.Robot;
import entities.Robot.Rotate;

public class StandbyFastestPath implements Runnable {

	private Robot robot;
	private TCPComm comms;
	private ArrayList<Node> finalPath;

	public StandbyFastestPath(Robot robot, TCPComm comms, ArrayList<Node> finalPath) {
		this.robot = robot;
		this.comms = comms;
		this.finalPath = finalPath;
	}

	/**
	 * Standby <tt>Robot</tt> for the Fastest Path run. Assumes <tt>Robot</tt> is at start position (1,
	 * 1) facing South.<br>
	 * <ol>
	 * <li>Run calibration</li>
	 * <li>Rotate to direction of fastest path</li>
	 * </ol>
	 */
	@Override
	public void run() {
		Cell secondCell = finalPath.get(1).getCell();		// Second step

		try {
			// Fastest Path goes North
			if (secondCell.getY() == 2 && secondCell.getX() == 1) {
				comms.send(TCPComm.ANDROID, "C");					// Calibrate
				Thread.sleep(2000);
				robot.rotate(Rotate.RIGHT);
				Thread.sleep(2000);
				robot.rotate(Rotate.RIGHT);
				Thread.sleep(2000);
			}

			// Fastest Path goes East
			else {
				comms.send(TCPComm.ANDROID, "C");					// Calibrate
				Thread.sleep(2000);
				robot.rotate(Rotate.LEFT);
				Thread.sleep(2000);
			}

		} catch (Exception e) {
		}
	}
}
