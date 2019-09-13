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
	 * Map Constructor.
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
	 * Export P1 and P2 to txt file from a Map object.
	 * 
	 * @param map
	 */
	public void exportMap(Map map) {
		// TODO Export P1 and P2 to txt file
	}

	/**
	 * Get specific cell from Map.
	 * 
	 * @param y
	 * @param x
	 * @return Requested <tt>Cell</tt>.
	 */
	public Cell getCell(int y, int x) {
		return cells[y][x];
	}

	/**
	 * Start coordinate is always set to (1, 1).
	 * 
	 * @return <tt>Coordinate</tt> of the start point.
	 */
	public Coordinate getStartCoord() {
		return new Coordinate(1, 1);
	}
}
