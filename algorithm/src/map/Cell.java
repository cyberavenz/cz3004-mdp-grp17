package map;

public class Cell {
	private final int x, y;		// Position
	private boolean isObstacle;	// Obstacle
	private int imageId;		// Image Recognition

	/* Constructor */
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean isObstacle() {
		return isObstacle;
	}

	public void setObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
