package algorithms;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import entities.Sensor;
import entities.Robot.Rotate;
import main.Main;

public class Exploration {

	private static char state = '1';	// Keep track of state with a static variable

	/**
	 * State machine for exploration algorithm.
	 * 
	 * Each call executes a command to the robot.
	 * 
	 * @param robot
	 * @param map
	 */
	public static void execute(Robot robot, Map map) {

		System.out.println("State: " + state);

		/* Set as Visited */
		map.getCell(robot.getCurrPos()).setCellType(Cell.PATH);
		System.out.println("setCellType on: " + map.getCell(robot.getCurrPos()).getCellType());

		/*
		 * State Machine
		 * 
		 * Refer to right-hugging flowchart under documentation folder.
		 */
		switch (state) {

		// Back to start position?
		case '1':
			if (robot.getCurrPos() == map.getStartCoord())
				state = 'A';
			else
				state = '2';
			break;

		// Is there a wall on the right?
		case '2':
			Coordinate[] rightCoordinates = genRightCoordinates(robot);

			/* Default State */
			state = '4';	// No

			/* Overwrite State */
			// null coordinates means robot is at map boundary
			if (rightCoordinates == null) {
				state = '3';		// Yes
				break;
			}

			for (int i = 0; i < rightCoordinates.length; i++) {
				if (Main.simulatedMap.getCell(rightCoordinates[i]).getCellType() == Cell.WALL) {
					state = '3';	// Yes
				}
			}
			break;

		// Is there a wall in the front?
		case '3':
			Coordinate[] frontCoordinates = genFrontCoordinates(robot);

			/* Default State */
			state = 'C';	// No

			/* Overwrite State */
			// null coordinates means robot is at map boundary
			if (frontCoordinates == null) {
				state = 'B';		// Yes
				break;
			}

			for (int i = 0; i < frontCoordinates.length; i++) {
				if (Main.simulatedMap.getCell(frontCoordinates[i]).getCellType() == Cell.WALL)
					state = 'B';	// Yes
			}
			break;

		// Has the cell on the right been visited?
		case '4':
			Coordinate[] pastRightCoordinates = genRightCoordinates(robot);

			// Index 1 is middle cell
			if (map.getCell(pastRightCoordinates[1]).getCellType() == Cell.PATH)
				state = 'C';	// Yes

			else
				state = 'D';

			break;

		// End of exploration!
		case 'A':
			System.out.println("End of Exploration");
			// TODO Print results
			break;

		// Rotate left
		case 'B':
			robot.rotate(Rotate.LEFT);
			state = '1';
			break;

		// Move forward
		case 'C':
			robot.moveForward(1);
			state = '1';
			break;

		// Rotate right
		case 'D':
			robot.rotate(Rotate.RIGHT);
			state = '1';
			break;

		// Do nothing
		default:
		}

	}

	/**
	 * Generate the coordinates on the right side of the <tt>Robot</tt>. Useful to cross-check with
	 * existing <tt>Map</tt> memory.
	 * 
	 * @param robot       <tt>Robot</tt>
	 * @param coordinates Coordinates that the sensor sees for reference.
	 * @return
	 */
	private static Coordinate[] genRightCoordinates(Robot robot) {
		Coordinate[] toReturn = new Coordinate[3];	// 3 x 3 robot has 3 coordinates per side

		Sensor rightSensor = robot.getSensor(Robot.S_FR_E);
		Coordinate[] coordinates = rightSensor.getFacingCoordinates(robot);

		// null coordinates means robot is at map boundary
		if (coordinates == null)
			return null;

		toReturn[0] = coordinates[0];	// First cell that the S_FR_E sensor sees

		switch (robot.getCurrDir()) {
		case Robot.NORTH:
			toReturn[1] = new Coordinate(coordinates[0].getY() - 1, coordinates[0].getX());	// Offset down
			toReturn[2] = new Coordinate(coordinates[0].getY() - 2, coordinates[0].getX());	// Offset down
			break;

		case Robot.SOUTH:
			toReturn[1] = new Coordinate(coordinates[0].getY() + 1, coordinates[0].getX());
			toReturn[2] = new Coordinate(coordinates[0].getY() + 2, coordinates[0].getX());
			break;

		case Robot.EAST:
			toReturn[1] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() - 1);
			toReturn[2] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() - 2);
			break;

		case Robot.WEST:
			toReturn[1] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() + 1);
			toReturn[2] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() + 2);
			break;

		default: // Do nothing
		}

		return toReturn;
	}

	/**
	 * Generate the coordinates on the front side of the <tt>Robot</tt>. Useful to cross-check with
	 * existing <tt>Map</tt> memory.
	 * 
	 * @param robot       <tt>Robot</tt>
	 * @param coordinates Coordinates that the sensor sees for reference.
	 * @return
	 */
	private static Coordinate[] genFrontCoordinates(Robot robot) {
		Coordinate[] toReturn = new Coordinate[3];	// 3 x 3 robot has 3 coordinates per side

		Sensor frontLeftSensor = robot.getSensor(Robot.S_FL_N);
		Coordinate[] coordinates = frontLeftSensor.getFacingCoordinates(robot);

		// null coordinates means robot is at map boundary
		if (coordinates == null)
			return null;

		toReturn[0] = coordinates[0];	// First cell that the S_FL_N sensor sees

		switch (robot.getCurrDir()) {
		case Robot.NORTH:
			toReturn[1] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() + 1);	// Offset right
			toReturn[2] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() + 2);	// Offset right
			break;

		case Robot.SOUTH:
			toReturn[1] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() - 1);
			toReturn[2] = new Coordinate(coordinates[0].getY(), coordinates[0].getX() - 2);
			break;

		case Robot.EAST:
			toReturn[1] = new Coordinate(coordinates[0].getY() - 1, coordinates[0].getX());
			toReturn[2] = new Coordinate(coordinates[0].getY() - 2, coordinates[0].getX());
			break;

		case Robot.WEST:
			toReturn[1] = new Coordinate(coordinates[0].getY() + 1, coordinates[0].getX());
			toReturn[2] = new Coordinate(coordinates[0].getY() + 2, coordinates[0].getX());
			break;

		default: // Do nothing
		}

		return toReturn;
	}
}
