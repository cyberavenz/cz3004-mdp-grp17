package algorithms;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;
import entities.Sensor;
import entities.Robot.Rotate;
import main.Main;

public class Exploration {

	private char state;	// No need start from state 1 when exploration first starts

	/**
	 * Constructor for exploration. Prepares <tt>Map</tt> for a new exploration by restarting state
	 * machine and resetting all <tt>isVisited</tt> flags.
	 * 
	 * @param map To reset all <tt>isVisited</tt> flags.
	 */
	public Exploration(Map map) {
		// Reset isVisited for all cells
		// TODO

		// Start from state 2 as exploration begins at start position
		state = '2';
	}

	/**
	 * State machine for exploration algorithm. Each call provides and execution command to the provided
	 * <tt>Robot</tt> with reference to the <tt>Map</tt>.
	 * 
	 * @param robot With reference to the given <tt>Robot</tt>.
	 * @param map   With reference to the given <tt>Map</tt>.
	 * @return <i>true</i> when exploration has ended.
	 */
	public boolean executeOneStep(Robot robot, Map map) {
		boolean done = false;		
		
		/* Set currPos as visited */
		map.getCell(robot.getCurrPos()).setVisited();

		/*
		 * State Machine
		 * 
		 * Refer to right-hugging flowchart under documentation folder.
		 */
		while (!done) {	// Repeat until movement command
			switch (state) {

			case '1':	// Back to start position?
				System.out.print(Character.toString(state) + ' ');
				if (Coordinate.isEqual(robot.getCurrPos(), map.getStartCoord()))
					state = 'A';
				else
					state = '2';
				break;

			case '2':	// Is there a wall on the right?
				System.out.print(Character.toString(state) + ' ');
				Coordinate[] rightCoordinates = genRightCoordinates(robot);

				// Assume default state
				state = '4';		// No

				// null coordinates means robot is at map boundary
				if (rightCoordinates == null)
					state = '3';	// Yes

				// Check all coordinates if there is a wall
				else {
					for (int i = 0; i < rightCoordinates.length; i++) {
						if (Main.testMap.getCell(rightCoordinates[i]).getCellType() == Cell.WALL) {
							state = '3';	// Yes
						}
					}
				}
				break;

			case '3':	// Is there a wall in the front?
				System.out.print(Character.toString(state) + ' ');
				Coordinate[] frontCoordinates = genFrontCoordinates(robot);

				// Assume default state
				state = 'C';	// No

				// null coordinates means robot is at map boundary
				if (frontCoordinates == null)
					state = 'B';		// Yes

				// Check all coordinates if there is a wall
				else {
					for (int i = 0; i < frontCoordinates.length; i++) {
						if (Main.testMap.getCell(frontCoordinates[i]).getCellType() == Cell.WALL)
							state = 'B';	// Yes
					}
				}
				break;

			case '4':	// Has the cell on the right been visited?
				System.out.print(Character.toString(state) + ' ');
				Coordinate[] robotFootprint = robot.getFootprint();

				// Check if MIDDLE_RIGHT cell of Robot has been visited before
				if (map.getCell(robotFootprint[Robot.MIDDLE_RIGHT]).isVisited())
					state = '5';	// Yes

				else
					state = 'D';	// No
				break;
				
			case '5':	// Is there a wall in the front?
				System.out.print(Character.toString(state) + ' ');
				frontCoordinates = genFrontCoordinates(robot);

				// Assume default state
				state = '6';	// No

				// null coordinates means robot is at map boundary
				if (frontCoordinates == null)
					state = 'D';		// Yes

				// Check all coordinates if there is a wall
				else {
					for (int i = 0; i < frontCoordinates.length; i++) {
						if (Main.testMap.getCell(frontCoordinates[i]).getCellType() == Cell.WALL)
							state = 'D';	// Yes
					}
				}
				break;
			
			case '6':	// Has the cell in the front been visited?
				System.out.print(Character.toString(state) + ' ');
				robotFootprint = robot.getFootprint();

				// Check if MIDDLE_RIGHT cell of Robot has been visited before
				if (map.getCell(robotFootprint[Robot.FRONT_CENTER]).isVisited())
					state = 'D';	// Yes

				else
					state = 'C';	// No
				break;

			case 'A':	// End of exploration!
				System.out.println("\nEnd of Exploration.");
				// TODO Print results
				// TODO Reset all visited flags?
				return true;	// Signify end of exploration

			case 'B':	// Rotate left
				System.out.println("\nRotate left.");
				robot.rotate(Rotate.LEFT);
				state = '1';
				done = true;
				break;

			case 'C':	// Move forward
				System.out.println("\nMove forward.");
				robot.moveForward(1);
				state = '1';
				done = true;
				break;

			case 'D':	// Rotate right and move forward
				System.out.println("\nRotate right and move forward.");
				robot.rotate(Rotate.RIGHT);
				robot.moveForward(1);
				state = '1';
				done = true;
				break;

			default:	// Do nothing
			}
		}

		return false;	// Not completed
	}

	/**
	 * Generate the coordinates on the right side of the <tt>Robot</tt>. Useful to cross-check with
	 * existing <tt>Map</tt> memory.
	 * 
	 * @param robot       <tt>Robot</tt>
	 * @param coordinates Coordinates that the sensor sees for reference.
	 * @return
	 */
	private Coordinate[] genRightCoordinates(Robot robot) {
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
	private Coordinate[] genFrontCoordinates(Robot robot) {
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
