package entities;

public class Node implements Comparable<Node> {
	private Cell cell;
	private boolean isVisited;
	private int heuristic;			// h(n): Heuristic
	private int distanceToStart;	// g(n): Sum of weights
	private int totalCost;			// f(n): Weights + this.heuristic

	public Node(int heuristic, Cell cell) {
		this.cell = cell;
		this.isVisited = false;
		this.heuristic = heuristic;
		this.distanceToStart = 1;			// Default 1
		this.totalCost = Integer.MAX_VALUE;	// Default infinity
	}

	public Cell getCell() {
		return cell;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	/**
	 * <b>h(n)</b>: Heuristic
	 * 
	 * @return
	 */
	public int getHeuristic() {
		return heuristic;
	}

	/**
	 * <b>g(n)</b>: Sum of weights
	 * 
	 * @return
	 */
	public int getDistanceToStart() {
		return distanceToStart;
	}

	public void setDistanceToStart(int cost) {
		this.distanceToStart = cost;
	}

	/**
	 * <b>f(n)</b>: Weights + this.heuristic
	 * 
	 * @return
	 */
	public int getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}

	/**
	 * Object hash code contract for use in a HashMap.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cell == null) ? 0 : cell.hashCode());
		result = prime * result + heuristic;
		return result;
	}

	/**
	 * Object equals contract for use in a HashMap.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (cell == null) {
			if (other.cell != null)
				return false;
		} else if (!cell.equals(other.cell))
			return false;
		if (heuristic != other.heuristic)
			return false;
		return true;
	}

	/**
	 * Makes a <tt>Node</tt> totalWeight comparable with another <tt>Node</tt> for use in a PriorityQueue.
	 * 
	 * Compares this object with the specified object for order. Returns a negative integer, zero, or a positive
	 * integer as this object is less than, equal to, or greater than the specified object.
	 * 
	 * @param n
	 * @return
	 */
	@Override
	public int compareTo(Node n) {
		/* Try to compare totalCost first */
		if (this.totalCost != n.totalCost)
			return Integer.compare(this.totalCost, n.totalCost);
		/* If they are the same, compare the differences in weights */
		else
			return Integer.compare(this.distanceToStart, n.distanceToStart);
	}
}
