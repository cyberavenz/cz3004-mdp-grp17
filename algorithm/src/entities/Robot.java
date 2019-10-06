package entities;

import java.util.LinkedList;

import communications.TCPComm;
import main.Main;

public class Robot {

	//	@formatter:off
	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	public static final int
		FRONT_LEFT = 0, FRONT_CENTER = 1, FRONT_RIGHT = 2,
		MIDDLE_LEFT = 3, MIDDLE_CENTER = 4, MIDDLE_RIGHT = 5,
		BACK_LEFT = 6, BACK_CENTER = 7, BACK_RIGHT = 8;
	public enum Rotate { RIGHT, LEFT };
	/* 6 sensors in total
	 * TYPE_PLACEMENT_DIRECTION */
	public static final int
		S_FL_N = 0, S_FC_N = 1, S_FR_N = 2, S_FR_E = 3,
		S_BL_W = 4, L_BL_W = 5;
	//	@formatter:on

	private boolean isRealRun;	// Real Run or Simulation (Whether to send movement command)
	private Coordinate currPos;	// MIDDLE_CENTER of Robot
	private int currDir;		// North, South, East, West
	private Sensor[] sensors;	// Initialised by initSensors()

	/**
	 * <tt>Robot</tt> Constructor (Each <tt>Robot</tt> occupies 3 x 3 cells)
	 * 
	 * Default Position: y=1 | x=1, Default Direction: EAST.
	 */
	public Robot(boolean isRealRun) {
		this.isRealRun = isRealRun;
		currPos = new Coordinate(1, 1);
		currDir = EAST;
		initSensors();
	}

	/**
	 * <tt>Robot</tt> Constructor with starting position and direction.
	 * 
	 * @param startPos
	 * @param startDir
	 */
	public Robot(Coordinate startPos, int startDir, boolean isRealRun) {
		this.isRealRun = isRealRun;

		int startY = startPos.getY();
		int startX = startPos.getX();
		boolean error = false;

		if (startY == 0 || startY == Map.maxY - 1) {
			error = true;
		} else if (startX == 0 || startX == Map.maxX - 1) {
			error = true;
		}

		if (error == true) {
			System.out.println("ERROR: Robot(y:" + startPos.getY() + " x:" + startPos.getX()
					+ ") cannot be initialised outside of map. Default position is assumed.");

			// Default starting position
			currPos = new Coordinate(1, 1);
			currDir = EAST;
		} else {
			this.currPos = startPos;
			this.currDir = startDir;
		}

		// Always initialise sensors
		initSensors();
	}

	/**
	 * @return Current position of <tt>Robot</tt>.
	 */
	public Coordinate getCurrPos() {
		return currPos;
	}

	/**
	 * @return Current direction of <tt>Robot</tt>.
	 */
	public int getCurrDir() {
		return currDir;
	}

	/**
	 * @return <tt>Coordinate[]</tt>: All cells that are occupied by the position of <tt>Robot</tt>.
	 */
	public Coordinate[] getFootprint() {
		Coordinate[] robotFootprint = new Coordinate[9];
		robotFootprint[MIDDLE_CENTER] = currPos;

		switch (currDir) {
		case NORTH:
			robotFootprint[FRONT_LEFT] = new Coordinate(currPos.getY() + 1, currPos.getX() - 1);
			robotFootprint[FRONT_CENTER] = new Coordinate(currPos.getY() + 1, currPos.getX());
			robotFootprint[FRONT_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX() + 1);

			robotFootprint[MIDDLE_LEFT] = new Coordinate(currPos.getY(), currPos.getX() - 1);
			robotFootprint[MIDDLE_RIGHT] = new Coordinate(currPos.getY(), currPos.getX() + 1);

			robotFootprint[BACK_LEFT] = new Coordinate(currPos.getY() - 1, currPos.getX() - 1);
			robotFootprint[BACK_CENTER] = new Coordinate(currPos.getY() - 1, currPos.getX());
			robotFootprint[BACK_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() + 1);
			return robotFootprint;
		case SOUTH:
			robotFootprint[FRONT_LEFT] = new Coordinate(currPos.getY() - 1, currPos.getX() + 1);
			robotFootprint[FRONT_CENTER] = new Coordinate(currPos.getY() - 1, currPos.getX());
			robotFootprint[FRONT_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() - 1);

			robotFootprint[MIDDLE_LEFT] = new Coordinate(currPos.getY(), currPos.getX() + 1);
			robotFootprint[MIDDLE_RIGHT] = new Coordinate(currPos.getY(), currPos.getX() - 1);

			robotFootprint[BACK_LEFT] = new Coordinate(currPos.getY() + 1, currPos.getX() + 1);
			robotFootprint[BACK_CENTER] = new Coordinate(currPos.getY() + 1, currPos.getX());
			robotFootprint[BACK_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX() - 1);
			return robotFootprint;
		case EAST:
			robotFootprint[FRONT_LEFT] = new Coordinate(currPos.getY() + 1, currPos.getX() + 1);
			robotFootprint[FRONT_CENTER] = new Coordinate(currPos.getY(), currPos.getX() + 1);
			robotFootprint[FRONT_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() + 1);

			robotFootprint[MIDDLE_LEFT] = new Coordinate(currPos.getY() + 1, currPos.getX());
			robotFootprint[MIDDLE_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX());

			robotFootprint[BACK_LEFT] = new Coordinate(currPos.getY() + 1, currPos.getX() - 1);
			robotFootprint[BACK_CENTER] = new Coordinate(currPos.getY(), currPos.getX() - 1);
			robotFootprint[BACK_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() - 1);
			return robotFootprint;
		case WEST:
			robotFootprint[FRONT_LEFT] = new Coordinate(currPos.getY() - 1, currPos.getX() - 1);
			robotFootprint[FRONT_CENTER] = new Coordinate(currPos.getY(), currPos.getX() - 1);
			robotFootprint[FRONT_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX() - 1);

			robotFootprint[MIDDLE_LEFT] = new Coordinate(currPos.getY() - 1, currPos.getX());
			robotFootprint[MIDDLE_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX());

			robotFootprint[BACK_LEFT] = new Coordinate(currPos.getY() - 1, currPos.getX() + 1);
			robotFootprint[BACK_CENTER] = new Coordinate(currPos.getY(), currPos.getX() + 1);
			robotFootprint[BACK_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX() + 1);
			return robotFootprint;
		default:
			return null;
		}
	}

	/**
	 * Move <tt>Robot</tt> forward by a specified number of steps.
	 * 
	 * @param steps
	 */
	public void moveForward(int steps) {
		int newPos;
		boolean send = false;
		String warning = "WARNING: moveRobot() is going out of map boundary.";

		switch (currDir) {
		case NORTH:
			newPos = currPos.getY() + steps;

			// Prevents Robot from going out of map boundary
			if (newPos < Map.maxY - 1) {
				currPos.setY(newPos);
				send = true;
			} else
				System.out.println(warning);
			break;
		case SOUTH:
			newPos = currPos.getY() - steps;

			if (newPos > 0) {
				currPos.setY(newPos);
				send = true;
			} else
				System.out.println(warning);
			break;
		case EAST:
			newPos = currPos.getX() + steps;

			if (newPos < Map.maxX - 1) {
				currPos.setX(newPos);
				send = true;
			} else
				System.out.println(warning);
			break;
		case WEST:
			newPos = currPos.getX() - steps;

			if (newPos > 0) {
				currPos.setX(newPos);
				send = true;
			} else
				System.out.println(warning);
			break;
		default: // Do nothing
		}

		// If Real Run, send command!
		if (this.isRealRun && send) {
			String strSteps = String.format("%02d", steps);
			Main.comms.send(TCPComm.ARDUINO, "F" + strSteps);
		}
	}

	/**
	 * Rotate <tt>Robot</tt> in the specified direction.
	 * 
	 * @param direction
	 */
	public void rotate(Rotate direction) {
		switch (direction) {
		case RIGHT:	// Rotate clockwise
			currDir = (currDir + 1) % 4;

			// If Real Run, send command!
			if (this.isRealRun)
				Main.comms.send(TCPComm.ARDUINO, "R90");
			break;
		case LEFT:	// Rotate counter-clockwise
			float newDir = (currDir - 1) % 4;

			// Make it positive as Java will return negative modulus
			if (newDir < 0)
				newDir += 4;

			currDir = (int) newDir;

			// If Real Run, send command!
			if (this.isRealRun)
				Main.comms.send(TCPComm.ARDUINO, "L90");
			break;
		default: // Do nothing
		}
	}

	public void sendFastestPath(LinkedList<String> navigateSteps) {
		if (navigateSteps.isEmpty()) {
			System.err.println("Fastest Path does not exist. Call runAStar() again.");
			return;
		}

		String pointer = navigateSteps.poll();
		String toSend = "";

		while (pointer != null) {

			int numOfForwards = 0;
			while (pointer == "F01") {
				numOfForwards++;
				pointer = navigateSteps.poll();
			}
			if (numOfForwards > 0)
				toSend += "F" + String.format("%02d", numOfForwards) + "|";

			if (pointer != null)
				toSend += pointer + "|";

			pointer = navigateSteps.poll();

		}

		System.out.println(toSend);

		if (this.isRealRun)
			Main.comms.send(TCPComm.ARDUINO, toSend);
	}

	/**
	 * Get a specific <tt>Sensor</tt> on <tt>Robot</tt>.
	 * 
	 * @param number TYPE_PLACEMENT_DIRECTION: Use static final variables.
	 * @return
	 */
	public Sensor getSensor(int number) {
		return sensors[number];
	}

	/**
	 * Get all <tt>Sensors</tt> on <tt>Robot</tt>.
	 * 
	 * @return
	 */
	public Sensor[] getAllSensors() {
		return this.sensors;
	}

	/**
	 * Initialise all sensors that will be mounted on <tt>Robot</tt>.
	 */
	private void initSensors() {
		sensors = new Sensor[6];	// 6 sensors in total

		// Short_FrontLeft_NORTH : 0
		sensors[S_FL_N] = new Sensor(Sensor.SHORT_RANGE, FRONT_LEFT, NORTH);
		// Short_FrontCenter_NORTH : 1
		sensors[S_FC_N] = new Sensor(Sensor.SHORT_RANGE, FRONT_CENTER, NORTH);
		// Short_FrontRight_NORTH : 2
		sensors[S_FR_N] = new Sensor(Sensor.SHORT_RANGE, FRONT_RIGHT, NORTH);
		// Short_FrontRight_EAST : 3
		sensors[S_FR_E] = new Sensor(Sensor.SHORT_RANGE, FRONT_RIGHT, EAST);
		// Short_Back_Left_WEST : 4
		sensors[S_BL_W] = new Sensor(Sensor.SHORT_RANGE, BACK_LEFT, WEST);
		// Long_BackLeft_WEST : 5
		sensors[L_BL_W] = new Sensor(Sensor.LONG_RANGE, BACK_LEFT, WEST);
	}

}
