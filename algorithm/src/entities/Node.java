package entities;

public class Node implements Comparable<Node> {
	private Cell cell;
	private boolean isVisited;
	private int heuristic;
	private int cost;

	public Node(int heuristic, Cell cell) {
		this.cell = cell;
		this.isVisited = false;
		this.heuristic = heuristic;
		this.cost = Integer.MAX_VALUE;
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

	public int getHeuristic() {
		return heuristic;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
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
		return Integer.compare(this.cost, n.cost);
	}
}
