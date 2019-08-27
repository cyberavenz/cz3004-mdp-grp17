package entities;

public class Robot {

	public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	public static final int FRONT_LEFT = 0, FRONT_RIGHT = 1, BACK_LEFT = 2, BACK_RIGHT = 3;

	private Coordinate currPos;	// Bottom left position of Robot
	private int currDir;		// North, South, East, West

	public Robot() {
		/* Initialise Robot at Start Position facing EAST */
		// Robot occupies 2 x 2 cells
		currPos = new Coordinate(1, 0);
		currDir = EAST;
	}
	
	public Robot(Coordinate coordinate, int direction) {
		currPos = coordinate;
		currDir = direction;
	}

	public Coordinate getCurrPos() {
		return currPos;
	}

	public Coordinate[] getCurrPosAll() {
		Coordinate[] currPosAll = new Coordinate[4];
		currPosAll[BACK_LEFT] = currPos;

		switch (currDir) {
		case NORTH:
			currPosAll[FRONT_LEFT] = new Coordinate(currPos.getY() + 1, currPos.getX());
			currPosAll[FRONT_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX() + 1);
			currPosAll[BACK_RIGHT] = new Coordinate(currPos.getY(), currPos.getX() + 1);
			return currPosAll;
		case SOUTH:
			currPosAll[FRONT_LEFT] = new Coordinate(currPos.getY() - 1, currPos.getX());
			currPosAll[FRONT_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() - 1);
			currPosAll[BACK_RIGHT] = new Coordinate(currPos.getY(), currPos.getX() - 1);
			return currPosAll;
		case EAST:
			currPosAll[FRONT_LEFT] = new Coordinate(currPos.getY(), currPos.getX() + 1);
			currPosAll[FRONT_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX() + 1);
			currPosAll[BACK_RIGHT] = new Coordinate(currPos.getY() - 1, currPos.getX());
			return currPosAll;
		case WEST:
			currPosAll[FRONT_LEFT] = new Coordinate(currPos.getY(), currPos.getX() - 1);
			currPosAll[FRONT_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX() - 1);
			currPosAll[BACK_RIGHT] = new Coordinate(currPos.getY() + 1, currPos.getX());
			return currPosAll;
		default:
			return null;
		}
	}

	public int getCurrDir() {
		return currDir;
	}

	// TODO Re-do
	public void moveForward() {
		switch (currDir) {
		case NORTH:
			currPos.setY(currPos.getY() + 1);
			break;
		case SOUTH:
			currPos.setY(currPos.getY() - 1);
			break;
		case EAST:
			currPos.setX(currPos.getX() + 1);
			break;
		case WEST:
			currPos.setX(currPos.getX() - 1);
			break;
		default: // Do nothing
		}
	}

}
