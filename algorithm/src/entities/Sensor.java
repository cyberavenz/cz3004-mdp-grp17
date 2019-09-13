package entities;

import java.util.ArrayList;

public class Sensor {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;

	public enum Type {
		SHORT_RANGE, LONG_RANGE
	}

	private Type type;			// Type of sensor (E.g. Short or long range)
	private int relativePos;	// The relative position of the sensor on the robot
	private int relativeDir;	// The relative direction of the sensor from robot's perspective
	private int depth;			// The maximum number of cells that the sensor can see ahead

	/**
	 * * Constructor for <tt>Sensor</tt>.
	 * 
	 * @param type        Type of sensor (E.g. <tt>SHORT_RANGE</tt> or <tt>LONG_RANGE</tt>).
	 * @param relativePos The relative position of the sensor on the <tt>Robot</tt>.
	 * @param relativeDir The relative direction of the sensor with reference from <tt>Robot</tt>.
	 * @param depth       The maximum number of cells that the <tt>Sensor</tt> can see ahead.
	 */
	public Sensor(Type type, int relativePos, int relativeDir, int depth) {
		if (relativeDir <= NORTH && relativeDir >= WEST) {
			// Invalid relativeDir, default to NORTH
			System.out
					.println("ERROR: Sensor() initialised with an invalid relativeDir parameter.");
			relativeDir = NORTH;
		}

		this.type = type;
		this.relativeDir = relativeDir;
		this.relativePos = relativePos;
		this.depth = depth;
	}

	public Type getType() {
		return type;
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
	 * Get an array of <tt>Coordinates</tt> that the sensor can currently see. This function will
	 * only return valid <tt>Coordinates</tt> within the map boundary.
	 * 
	 * @param robot Requires <tt>Robot</tt> to obtain actual direction of the <tt>Sensor</tt>.
	 * @return An array of <tt>Coordinates</tt> based on the depth of <tt>Sensor</tt>. A
	 *         <tt>null</tt> return means sensor is facing the boundary of the map.
	 */
	public Coordinate[] getFacingCoordinates(Robot robot) {
		// Sensor depth may be outside map boundary - Use ArrayList to dynamically add Coordinates.
		ArrayList<Coordinate> facingCoords = new ArrayList<Coordinate>();
		Coordinate[] robotFootprint = robot.getFootprint();

		int actualSensorDir = getActualDir(robot);
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

	public int getDepth() {
		return this.depth;
	}

}
