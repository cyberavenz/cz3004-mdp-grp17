package entities;

public class Coordinate {
	private int y, x;

	public Coordinate(int y, int x) {
		this.y = y;
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public static boolean isEqual(Coordinate c1, Coordinate c2) {
		if (c1.y == c2.y) {
			if (c1.x == c2.x) {
				return true;
			}
		}
		return false;
	}

}
