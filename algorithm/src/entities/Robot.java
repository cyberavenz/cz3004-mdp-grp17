package entities;

public class Robot {

	public static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;

	private Coordinate currPos;
	private int currDir;

	public Robot() {
		/* Initialise Robot at Start Position facing right */
		currPos = new Coordinate(0, 0); 	// Bottom left of robot (Remember: Robot occupies 2 x 2 cells)
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

	public int getCurrDir() {
		return currDir;
	}

}
