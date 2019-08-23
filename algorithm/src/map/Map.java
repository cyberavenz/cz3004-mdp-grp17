package map;

import map.Cell;

public class Map {
	public static final int maxX = 15;
	public static final int maxY = 20;

	/* Constructor */
	public Map() {
		/* Initialise total cells */
		Cell cells[][] = new Cell[maxX][maxY];

		/* Initialise each cell */
		for (int x = 0; x < maxX; x++) {
			for (int y = 0; y < maxY; y++) {
				cells[x][y] = new Cell(x, y);
			}
		}
	}
}
