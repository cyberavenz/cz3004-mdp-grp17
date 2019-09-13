package entities;

public class Robot {

	public enum Rotate {
		RIGHT, LEFT
	}

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	//	@formatter:off
	public static final int
		FRONT_LEFT = 0, FRONT_CENTER = 1, FRONT_RIGHT = 2,
		MIDDLE_LEFT = 3, MIDDLE_CENTER = 4, MIDDLE_RIGHT = 5,
		BACK_LEFT = 6, BACK_CENTER = 7, BACK_RIGHT = 8;
	//	@formatter:on

	private Coordinate currPos;	// MIDDLE_CENTER of Robot
	private int currDir;		// North, South, East, West

	private Sensor[] sensors;

	/**
	 * <tt>Robot</tt> Constructor (Each <tt>Robot</tt> occupies 3 x 3 cells)
	 * 
	 * Default Position: y=1 | x=1, Default Direction: EAST.
	 */
	public Robot() {
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
	public Robot(Coordinate startPos, int startDir) {
		// Default starting position
		currPos = new Coordinate(1, 1);
		currDir = EAST;
		initSensors();

		// Check for any invalid coordinates first
		if (startPos.getY() == Map.maxY - 1 || startPos.getY() == 0 || startPos.getX() == 0
				|| startPos.getY() == Map.maxX - 1) {
			System.out.println(
					"ERROR: Robot() cannot be initialised at the edge of map as robot footprint is 3 x 3 cells.");
		} else if (startPos.getY() > Map.maxY - 1 || startPos.getY() < 0
				|| startPos.getX() > Map.maxX - 1 || startPos.getX() < 0) {
			System.out.println("ERROR: Robot() cannot be initialised outside of map.");
		}

		// Valid condition, overwrite default starting position
		else {
			currPos = startPos;
			currDir = startDir;
		}
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
			robotFootprint[FRONT_LEFT] = new Coordinate(currPos.getY() - 1, currPos.getX() - 1);
			robotFootprint[FRONT_CENTER] = new Coordinate(currPos.getY() - 1, currPos.getX());
			robotFootprint[FRONT_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() + 1);

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

			robotFootprint[BACK_LEFT] = new Coordinate(currPos.getY() + 1, currPos.getX() + 1);
			robotFootprint[BACK_CENTER] = new Coordinate(currPos.getY(), currPos.getX() + 1);
			robotFootprint[BACK_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() + 1);
			return robotFootprint;
		default:
			return null;
		}
	}

	/**
	 * Move <tt>Robot</tt> forward in direction of <tt>currDir</tt>.
	 * 
	 */
	public void moveForward(int steps) {
		int newPos;
		String warning = "WARNING: moveRobot() is going out of map boundary.";

		switch (currDir) {
		case NORTH:
			newPos = currPos.getY() + steps;

			// Prevents Robot from going out of map boundary
			if (newPos < Map.maxY - 1)
				currPos.setY(newPos);
			else
				System.out.println(warning);
			break;
		case SOUTH:
			newPos = currPos.getY() - steps;

			if (newPos > 0)
				currPos.setY(newPos);
			else
				System.out.println(warning);
			break;
		case EAST:
			newPos = currPos.getX() + steps;

			if (newPos < Map.maxX - 1)
				currPos.setX(newPos);
			else
				System.out.println(warning);
			break;
		case WEST:
			newPos = currPos.getX() - steps;

			if (newPos > 0)
				currPos.setX(newPos);
			else
				System.out.println(warning);
			break;
		default: // Do nothing
		}
	}

	/**
	 * Rotate <tt>Robot</tt>
	 * 
	 * @param direction
	 */
	public void rotate(Rotate direction) {
		switch (direction) {
		case RIGHT:	// Rotate clockwise
			currDir = (currDir + 1) % 4;
			break;
		case LEFT:	// Rotate counter-clockwise
			float newDir = (currDir - 1) % 4;

			// Make it positive as Java will return negative modulus
			if (newDir < 0)
				newDir += 4;

			currDir = (int) newDir;
			break;
		default: // Do nothing
		}
	}

	/**
	 * Initialise all sensors that will be mounted on <tt>Robot</tt>.
	 */
	private void initSensors() {
		sensors = new Sensor[6];	// 6 sensors in total

		// Front left, facing NORTH
		sensors[0] = new Sensor(Sensor.Type.SHORT_RANGE, FRONT_LEFT, NORTH, 3);
		// Front center, facing NORTH
		sensors[1] = new Sensor(Sensor.Type.SHORT_RANGE, FRONT_CENTER, NORTH, 3);
		// Front right, facing NORTH
		sensors[2] = new Sensor(Sensor.Type.SHORT_RANGE, FRONT_RIGHT, NORTH, 3);
		// Front right, facing EAST
		sensors[3] = new Sensor(Sensor.Type.SHORT_RANGE, FRONT_RIGHT, EAST, 3);
		// Back left, facing WEST
		sensors[4] = new Sensor(Sensor.Type.SHORT_RANGE, BACK_LEFT, WEST, 3);
		// Back left, facing WEST
		sensors[5] = new Sensor(Sensor.Type.LONG_RANGE, BACK_LEFT, WEST, 5);
	}

	public Sensor getSensor(int number) {
		return sensors[number];
	}

}
