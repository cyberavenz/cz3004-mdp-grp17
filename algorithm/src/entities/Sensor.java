package entities;

import java.util.ArrayList;

public class Sensor {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
	/* 2 types of sensor range */
	public static final int SHORT_RANGE = 2, LONG_RANGE = 5;

	private int relativePos;	// The relative position of the sensor on the robot
	private int relativeDir;	// The relative direction of the sensor from robot's perspective
	private int depth;			// The maximum number of cells that the sensor can see ahead

	/**
	 * * Constructor for <tt>Sensor</tt>.
	 * 
	 * @param depth       The maximum number of cells that the <tt>Sensor</tt> can see ahead.
	 * @param relativePos The relative position of the sensor on the <tt>Robot</tt>.
	 * @param relativeDir The relative direction of the sensor with reference from <tt>Robot</tt>.
	 */
	public Sensor(int depth, int relativePos, int relativeDir) {
		if (relativeDir <= NORTH && relativeDir >= WEST) {
			// Invalid relativeDir, default to NORTH
			System.out.println("ERROR: Sensor() initialised with an invalid relativeDir parameter.");
			relativeDir = NORTH;
		}

		this.depth = depth;
		this.relativePos = relativePos;
		this.relativeDir = relativeDir;
	}

	/**
	 * Get actual direction of sensor with reference from <tt>Robot</tt>.
	 * 
	 * @param robot
	 * @return
	 */
	public int getActualDir(Robot robot) {
		int robotDir = robot.getCurrDir();

		return (robotDir + this.relativeDir) % 4;
	}

	/**
	 * Get an array of <tt>Coordinates</tt> that the sensor can currently see. This function will only
	 * return valid <tt>Coordinates</tt> within the map boundary.
	 * 
	 * @param robot Requires <tt>Robot</tt> to obtain actual direction of the <tt>Sensor</tt>.
	 * @return An array of <tt>Coordinates</tt> based on the depth of <tt>Sensor</tt>. A <tt>null</tt>
	 *         return means sensor is facing the boundary of the map.
	 */
	public Coordinate[] getFacingCoordinates(Robot robot) {
		// Sensor depth may be outside map boundary - Use ArrayList to dynamically add Coordinates.
		ArrayList<Coordinate> facingCoords = new ArrayList<Coordinate>();
		Coordinate[] robotFootprint = robot.getFootprint();

		int actualSensorDir = this.getActualDir(robot);
		int actualSensorPosY = robotFootprint[this.relativePos].getY();
		int actualSensorPosX = robotFootprint[this.relativePos].getX();

		switch (actualSensorDir) {
		case NORTH:
			for (int i = 0; i < this.depth; i++) {
				int newY = actualSensorPosY + i + 1;
				int newX = actualSensorPosX;

				if (newY < Map.maxY)
					facingCoords.add(new Coordinate(newY, newX));
			}
			break;
		case SOUTH:
			for (int i = 0; i < this.depth; i++) {
				int newY = actualSensorPosY - i - 1;
				int newX = actualSensorPosX;

				if (newY >= 0)
					facingCoords.add(new Coordinate(newY, newX));
			}
			break;
		case EAST:
			for (int i = 0; i < this.depth; i++) {
				int newY = actualSensorPosY;
				int newX = actualSensorPosX + i + 1;

				if (newX < Map.maxX)
					facingCoords.add(new Coordinate(newY, newX));
			}
			break;
		case WEST:
			for (int i = 0; i < this.depth; i++) {
				int newY = actualSensorPosY;
				int newX = actualSensorPosX - i - 1;

				if (newX >= 0)
					facingCoords.add(new Coordinate(newY, newX));
			}
			break;
		default:
			// Do nothing
		}

		/* Final return */
		if (facingCoords.isEmpty()) {
			// Sensor is facing map boundary
			return null;
		} else {
			// Prepare array of Coordinates to be returned
			int length = facingCoords.size();
			Coordinate[] toReturn = new Coordinate[length];

			for (int i = 0; i < length; i++)
				toReturn[i] = facingCoords.get(i);

			return toReturn;
		}
	}

	/**
	 * Get the maximum number of cells that the sensor can see ahead.
	 * 
	 * @return
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * Get simulated value of robot with reference to an actual <tt>Map</tt>.
	 * 
	 * @param robot     <tt>Robot</tt> for reference.
	 * @param actualMap An actual <tt>Map</tt> to know what values to simulate. Do not parse in
	 *                  <i>unknown.txt</i> map!
	 * @return 0: Wall in front | 1: Wall 1 cell away | 2: Wall 2 cells away | ... | 99: No wall
	 *         detected
	 */
//	public int simulatedLook(Robot robot, Map actualMap) {
//		Coordinate[] facingCoords = this.getFacingCoordinates(robot);
//
//		// Boundary of map
//		if (facingCoords == null)
//			return 0;
//
//		// Valid map coordinates, check against actualMap for simulated value
//		else {
//			int i = 0;
//			boolean wallDetected = false;
//
//			for (i = 0; i < facingCoords.length; i++) {
//				if (actualMap.getCell(facingCoords[i]).getCellType() == Cell.WALL) {
//					wallDetected = true;
//					break;
//				}
//			}
//			if (wallDetected)
//				return i;
//			else
//				return 99;	// Sensor sees no wall at all
//		}
//	}

	public int getRelativePos() {
		return this.relativePos;
	}

	public static int[] cleanAllReadings(Sensor[] sensors, String incomingReadings) {
		String[] splitString = incomingReadings.split("[|]");	// Requires the use of [] character class
		int[] toReturn = new int[splitString.length];

		System.out.println("Cleaned sensor values: ");

		for (int i = 0; i < splitString.length; i++) {
			toReturn[i] = Integer.parseInt(splitString[i]);

			// If received value is above range, assume max depth
			if (toReturn[i] > sensors[i].getDepth())
				toReturn[i] = sensors[i].getDepth();

			System.out.print(toReturn[i]);
			if (i < splitString.length - 1)
				System.out.print("|");
		}

		System.out.println();
		return toReturn;
	}
}
