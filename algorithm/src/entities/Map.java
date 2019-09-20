package entities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
	 * Get P1 descriptors from a given <tt>Map</tt> object.
	 * 
	 * @param map
	 */
	public static String getP1Descriptors(Map map) {
		String P1 = new String();

		P1 += "11";		// Padding sequence

		for (int y = 0; y < Map.maxY; y++) {
			for (int x = 0; x < Map.maxX; x++) {
				int cellType = map.getCell(new Coordinate(y, x)).getCellType();

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
			String testString = P1.substring(i, i + 4);
			int intOf4Bits = Integer.parseInt(testString, 2);	// Binary String to Decimal Number
			hexString += Integer.toString(intOf4Bits, 16).toUpperCase();	// Decimal Number to Hex String
		}

		return hexString;
	}

	/**
	 * Get P2 descriptors from a given <tt>Map</tt> object.
	 * 
	 * @param map
	 */
	public static String getP2Descriptors(Map map) {
		String P2 = new String();

		for (int y = 0; y < Map.maxY; y++) {
			for (int x = 0; x < Map.maxX; x++) {
				int cellType = map.getCell(new Coordinate(y, x)).getCellType();

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
			String testString = P2.substring(i, i + 4);
			int intOf4Bits = Integer.parseInt(testString, 2);	// Binary String to Decimal Number
			hexString += Integer.toString(intOf4Bits, 16).toUpperCase();	// Decimal Number to Hex String
		}

		return hexString;
	}
}
