package map;

public class Cell {
	public static final char UNKNOWN = 'A', WALL = 'B', START = 'C', GOAL = 'D', PATH = 'E', FINAL_PATH = 'F';
	public static final char UP_ARROW = 1, DOWN_ARROW = 2, RIGHT_ARROW = 3, LEFT_ARROW = 4, STOP = 5, ONE = 6, TWO = 7, THREE = 8, FOUR = 9,
			FIVE = 10, ALPHABET_A = 11, ALPHABET_B = 12, ALPHABET_C = 13, ALPHABET_D = 14, ALPHABET_E = 15;

	private final int y, x;		// Cell position
	private char cellType;		// Cell type listed above

	/* Constructor */
	public Cell(int y, int x) {
		this.y = y;
		this.x = x;

		this.cellType = UNKNOWN;	// Default to UNKNOWN
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public char getCellType() {
		return cellType;
	}

	public void setCellType(char cellType) {
		this.cellType = cellType;
	}

}
