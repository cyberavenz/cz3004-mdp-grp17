package entities;

public class Robot {

	public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	public static final int FRONT_LEFT = 0, FRONT_RIGHT = 1, BACK_LEFT = 2, BACK_RIGHT = 3;

	private Coordinate currPos; // Bottom left position of Robot
	private int currDir;		// North, South, East, West

	public Robot() {
		/* Initialise Robot at Start Position facing EAST */
		// Robot occupies 3 x 3 cells
		currPos = new Coordinate(1, 1);
		currDir = EAST;
	}

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
