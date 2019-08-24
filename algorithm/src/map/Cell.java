package map;

public class Cell {
	private final int x, y;		// Position
	private int cellType;		// 0: Empty | 1: Wall | 2: Start | 3: Goal | 4: Part of correct Path!
	private int imageId;		// Image ID 0: NIL | 1: Up Arrow ... 15: Alphabet E

	/* Constructor */
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;

		this.cellType = 0;		// Default
		this.imageId = 0;		// Default
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getCellType() {
		return cellType;
	}

	public void setCellType(int cellType) {
		this.cellType = cellType;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

}
