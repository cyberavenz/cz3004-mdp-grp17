package map;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import map.Cell;

public class Map {
	public static final int maxX = 15;
	public static final int maxY = 20;

	private Cell cells[][];

	/* Constructor */
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

	/* Import map from file */
	public void importMap(String fileName) {
		// TODO Loading of map from txt file
		try {

			String filePath = new File("").getAbsolutePath();
			Scanner s = new Scanner(new BufferedReader(new FileReader(filePath.concat("/src/presets/" + fileName))));

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

	/* Get Entry Cell Location */
	public Cell getStartCell() {
		return cells[0][0];		// Bottom Left
	}

	/* Get Goal Cell */
	public Cell getGoalCell() {
		return cells[19][14];	// Top Right
	}

	public Cell getCell(int y, int x) {
		return cells[y][x];
	}
}
