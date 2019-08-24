package map;

import map.Cell;

public class Map {
	public final int maxX = 15;
	public final int maxY = 20;

	private Cell cells[][];

	/* Constructor */
	public Map() {
		/* Initialise total cells */
		cells = new Cell[maxX][maxY];

		/* Initialise each cell */
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				cells[x][y] = new Cell(x, y);
			}
		}
	}

	/* Load map from file */
	public void loadMap() {
		// TODO Loading of map from txt file
	}

	/* Get Entry Cell Location */
	public Cell getEntry() {

		return cells[0][0];
	}

	/* Get Goal Cell */
	public Cell getGoal() {

		return cells[0][0];
	}
}
