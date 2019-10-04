package entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import entities.Cell;

public class Map {
	public static final int maxY = 20;
	public static final int maxX = 15;

	private Cell cells[][];

	/**
	 * Map constructor.
	 * 
	 * Default Grid: y=20 by x=15.
	 */
	public Map() {
		// Initialise cells
		cells = new Cell[maxY][maxX];

		// Initialise each cell
		for (int y = maxY - 1; y >= 0; y--) {
			for (int x = 0; x < maxX; x++) {
				cells[y][x] = new Cell(y, x);
			}
		}
	}

	/**
	 * Map constructor with import.
	 * 
	 * Default Grid: y=20 by x=15.
	 * 
	 * @param fileName
	 */
	public Map(String fileName) {
		this();
		this.importMap(fileName);
	}

	/**
	 * Import Map from txt file.
	 * 
	 * @param fileName (E.g. "empty.txt")
	 */
	public void importMap(String fileName) {
		try {

			String filePath = new File("").getAbsolutePath();
			Scanner s = new Scanner(new BufferedReader(new FileReader(filePath.concat("/presets/" + fileName))));

			while (s.hasNext()) {
				for (int y = maxY - 1; y >= 0; y--) {
					for (int x = 0; x < maxX; x++) {
						char type = s.next().charAt(0);
						cells[y][x].setCellType(type);
					}
				}
			}

			s.close();
		} catch (IOException e) {
			System.err.format("Import Map IOException: %s%n", e);
		}
	}

	/**
	 * Get specific cell from Map.
	 * 
	 * @param coordinate
	 * @return Requested <tt>Cell</tt>.
	 */
	public Cell getCell(Coordinate coordinate) {
		return cells[coordinate.getY()][coordinate.getX()];
	}

	/**
	 * Start coordinate is always set to (1, 1).
	 * 
	 * @return <tt>Coordinate</tt> of the start point.
	 */
	public Coordinate getStartCoord() {
		return new Coordinate(1, 1);
	}

	/**
	 * Get P1 descriptors.
	 */
	public String getP1Descriptors() {
		String P1 = new String();

		P1 += "11";		// Padding sequence

		for (int y = 0; y < Map.maxY; y++) {
			for (int x = 0; x < Map.maxX; x++) {
				int cellType = this.getCell(new Coordinate(y, x)).getCellType();

				if (cellType == Cell.UNKNOWN)
					P1 += "0";
				else
					P1 += "1";
			}
		}

		P1 += "11";		// Padding sequence

		// Convert to Hexadecimal
		String hexString = new String();
		for (int i = 0; i < 304; i += 4) {
			String binOf4Bits = P1.substring(i, i + 4);
			int intOf4Bits = Integer.parseInt(binOf4Bits, 2);	// Binary String to Decimal Number
			hexString += Integer.toString(intOf4Bits, 16).toUpperCase();	// Decimal Number to Hex String
		}

		return hexString;
	}

	/**
	 * Get P2 descriptors.
	 */
	public String getP2Descriptors() {
		String P2 = new String();

		for (int y = 0; y < Map.maxY; y++) {
			for (int x = 0; x < Map.maxX; x++) {
				int cellType = this.getCell(new Coordinate(y, x)).getCellType();

				if (cellType != Cell.UNKNOWN) {
					if (cellType == Cell.WALL)
						P2 += "1";
					else
						P2 += "0";
				}
			}
		}

		// Normalise P2 Binary
		int remainder = P2.length() % 4;
		String lastBit = new String();
		String padding = new String();

		switch (remainder) {
		case 1:
			lastBit = P2.substring(P2.length() - 1);
			padding = "000";
			P2 = P2.substring(0, P2.length() - 1).concat(padding).concat(lastBit);
			break;
		case 2:
			lastBit = P2.substring(P2.length() - 2);
			padding = "00";
			P2 = P2.substring(0, P2.length() - 2).concat(padding).concat(lastBit);
			break;
		case 3:
			lastBit = P2.substring(P2.length() - 3);
			padding = "0";
			P2 = P2.substring(0, P2.length() - 3).concat(padding).concat(lastBit);
			break;
		default: // Do nothing
		}

		// Convert to Hexadecimal
		String hexString = new String();
		for (int i = 0; i < P2.length(); i += 4) {
			String binOf4Bits = P2.substring(i, i + 4);
			int intOf4Bits = Integer.parseInt(binOf4Bits, 2);	// Binary String to Decimal Number
			hexString += Integer.toString(intOf4Bits, 16).toUpperCase();	// Decimal Number to Hex String
		}

		return hexString;
	}

	/**
	 * Simulated reveal of cells around the <tt>Robot</tt> based on the provided <tt>testMap</tt>.
	 * 
	 * @param robot
	 * @param testMap Set to <tt>null</tt> for real run
	 */
	public void simulatedReveal(Robot robot, Map testMap) {
		Sensor[] sensors = robot.getAllSensors();
		Coordinate[] coordinates;

		/* Simulation Mode */
		for (int i = 0; i < sensors.length; i++) {
			coordinates = sensors[i].getFacingCoordinates(robot);

			// Only when sensor sees some coordinates
			if (coordinates != null) {
				for (int j = 0; j < coordinates.length; j++) {
					Cell unknownCell = this.getCell(coordinates[j]);
					Cell simulatedCell = testMap.getCell(coordinates[j]);
					unknownCell.setCellType(simulatedCell.getCellType());

					// Sensor should not be able to see past walls
					if (simulatedCell.getCellType() == Cell.WALL)
						break;
				}
			}
		}
	}

	/**
	 * Actual reveal of cells around the <tt>Robot</tt> based on the provided sensor values from Arduino.
	 * 
	 * @param robot
	 * @param incomingReadings
	 */
	public void actualReveal(Robot robot, String incomingReadings) {
		/* Real Run Mode */
		Sensor[] sensors = robot.getAllSensors();
		int[] arduinoSensorValues = Sensor.cleanAllReadings(sensors, incomingReadings);
		Coordinate[] coordinates;
		boolean ignore_L_BL_W = false;

		if (arduinoSensorValues.length != sensors.length) {
			System.err.println("Incorrect sensor format received from Arduino!");
			return;
		}

		/*
		 * Read sensor values only if robot has some facing coordinates.
		 * 
		 * Sensors 0 to 4: Short Range | Sensor 5: Long Range
		 * 
		 * IMPORTANT: Only if Sensor 4 sees no wall at all, then read from Sensor 5.
		 */
		for (int i = 0; i < sensors.length; i++) {
			coordinates = sensors[i].getFacingCoordinates(robot);

			// For all sensors, run only when there are facing coordinates
			if (coordinates != null) {
				// When sensor sees a wall
				if (arduinoSensorValues[i] < coordinates.length) {
					/* If ignore_L_BL_W is true, break */
					if (i == Robot.L_BL_W && ignore_L_BL_W == true)
						break;

					for (int j = 0; j < coordinates.length; j++) {
						if (j == arduinoSensorValues[i]) {
							this.getCell(coordinates[j]).setCellType(Cell.WALL);

							// Trust only Sensor 0 and 2
							if (i == 0 || i == 2) {
								this.getCell(coordinates[j]).setPermanentCellType(true);
							}
							break;	// Unable to see past any wall, BREAK!
						} else {
							this.getCell(coordinates[j]).setCellType(Cell.PATH);
						}
					}

					/* If S_BL_W detects a wall, ignore L_BL_W sensor */
					if (i == Robot.S_BL_W)
						ignore_L_BL_W = true;
				}

				// When sensor sees no wall at all (when max value is received)
				else if (arduinoSensorValues[i] == coordinates.length) {
					for (int j = 0; j < coordinates.length; j++) {
						this.getCell(coordinates[j]).setCellType(Cell.PATH);
					}

					/*
					 * If S_BL_W does not detect a wall, set flag to true so it will read from L_BL_W sensor in the next
					 * loop.
					 */
					if (i == Robot.S_BL_W)
						ignore_L_BL_W = false;
				}
			}
		}
	}

	public void finalPathReveal(ArrayList<Node> finalPath) {
		/* Clear any remnants of previously computed final path */
		for (int y = 0; y < maxY; y++) {
			for (int x = 0; x < maxX; x++) {
				if (cells[y][x].getCellType() == Cell.FINAL_PATH)
					cells[y][x].setCellType(Cell.PATH);
			}
		}

		/* Mark cellType as FINAL_PATH */
		for (int i = 0; i < finalPath.size(); i++) {
			Node n = finalPath.get(i);
			Cell cell = this.cells[n.getCell().getY()][n.getCell().getX()];
			cell.setCellType(Cell.FINAL_PATH);
		}
	}
}
