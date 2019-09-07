package entities;

public class Sensor {

	public enum Type {
		SHORT_RANGE, LONG_RANGE
	}

	private Type type;					// Type of sensor (E.g. Short or long range)
	private int relativeDir;			// The relative direction from robot's perspective
	private float lowLimit, uppLimit;	// Lower and upper limit of sensor

	/**
	 * Constructor for <tt>Sensor</tt>.
	 * 
	 * @param type        Type of sensor (E.g. SHORT_RANGE or LONG_RANGE).
	 * @param relativeDir The relative direction of the sensor with reference from
	 *                    <tt>Robot</tt>.
	 * @param lowLimit    Lower limit of sensor.
	 * @param uppLimit    Upper limit of sensor.
	 */
	public Sensor(Type type, int relativeDir, float lowLimit, float uppLimit) {
		this.type = type;
		this.relativeDir = relativeDir;
		this.lowLimit = lowLimit;
		this.uppLimit = uppLimit;
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

		// Cycle through NORTH, EAST, SOUTH, WEST
		if (relativeDir > robotDir) {
			return ((robotDir + relativeDir) % 4);
		} else {
			float returnDir = (robotDir - relativeDir) % 4;

			// Make it positive as Java will return negative modulus
			if (returnDir < 0)
				returnDir += 4;

			return (int) returnDir;
		}
	}

	public float getLowLimit() {
		return lowLimit;
	}

	public float getUppLimit() {
		return uppLimit;
	}

}
