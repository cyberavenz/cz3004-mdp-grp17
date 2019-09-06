package entities;

public class Robot {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	//	@formatter:off
	public static final int
		FRONT_LEFT = 0, FRONT_CENTER = 1, FRONT_RIGHT = 2,
		MIDDLE_LEFT = 3, MIDDLE_CENTER = 4, MIDDLE_RIGHT = 5,
		BACK_LEFT = 6, BACK_CENTER = 7, BACK_RIGHT = 8;
	//	@formatter:on

	public enum Rotate {
		RIGHT, LEFT
	}

	private Coordinate currPos;	// MIDDLE_CENTER of Robot
	private int currDir;		// North, South, East, West

	/**
	 * <tt>Robot</tt> Constructor (Each <tt>Robot</tt> occupies 3 x 3 cells)
	 * 
	 * Default Position: y=1 | x=1, Default Direction: EAST.
	 */
	public Robot() {
		currPos = new Coordinate(1, 1);
		currDir = EAST;
	}

	/**
	 * <tt>Robot</tt> Constructor with starting position and direction.
	 * 
	 * @param coordinate
	 * @param direction
	 */
	public Robot(Coordinate coordinate, int direction) {
		// Check for invalid coordinates
		if (coordinate.getY() == Map.maxY - 1 || coordinate.getY() == 0 || coordinate.getX() == 0
				|| coordinate.getY() == Map.maxX - 1) {
			System.out.println(
					"ERROR: Robot() cannot be initialised at the edge of map as robot footprint is 3 x 3 cells.");
			return;
		} else if (coordinate.getY() > Map.maxY - 1 || coordinate.getX() > Map.maxX - 1) {
			System.out.println("ERROR: Robot() cannot be initialised outside of map.");
			return;
		} else {
			currPos = coordinate;
			currDir = direction;
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
	 * @return <tt>Coordinate[]</tt>: All cells that are occupied by the position of
	 *         <tt>Robot</tt>.
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
	 * @param toRotate
	 */
	public void rotate(Rotate toRotate) {
		switch (toRotate) {
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

}
