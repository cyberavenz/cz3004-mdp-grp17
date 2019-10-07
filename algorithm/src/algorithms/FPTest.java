package algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.Collections;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;

/**
 * @deprecated use FastestPath.java instead.
 */
@Deprecated
public class FPTest {

	private HashMap<Coordinate, Node> nodes = new HashMap<Coordinate, Node>();
	private HashMap<Node, ArrayList<Node>> edges = new HashMap<Node, ArrayList<Node>>();

//	private Map currMap;
	private Coordinate startCoord, goalCoord;

	public FPTest(Map currMap, Coordinate startCoord, Coordinate goalCoord) {
//		this.currMap = currMap;
		this.startCoord = startCoord;
		this.goalCoord = goalCoord;

		/* Step 1: Create graph of nodes with heuristic */
		for (int y = 1; y <= Map.maxY - 2; y++) {
			for (int x = 1; x <= Map.maxX - 2; x++) {
				Coordinate currPos = new Coordinate(y, x);

				Robot robot = new Robot(currPos, Robot.NORTH, false);
				Coordinate[] robotFootprint = robot.getFootprint();

				boolean hasWall = false;

				for (int i = 0; i < robotFootprint.length; i++) {
					Cell cell = currMap.getCell(robotFootprint[i]);
					if (cell.getCellType() == Cell.WALL || cell.getCellType() == Cell.UNKNOWN) {
						hasWall = true;
						break;
					}
				}

				if (hasWall == false) {
					int heuristic;
					int y2 = goalCoord.getY();
					int x2 = goalCoord.getX();

					heuristic = (int) Math.round(Math.sqrt(Math.pow((y2 - y), 2) + Math.pow((x2 - x), 2)));

					Node thisNode = new Node(heuristic, currMap.getCell(currPos));
					nodes.put(currPos, thisNode);
				}
			}
		}
		System.out.println("FastestPath: Graph has a total of " + nodes.size() + " nodes.");

		int totalEdges = 0;
		/* Step 2: Link nodes with edges */
		for (HashMap.Entry<Coordinate, Node> entry : nodes.entrySet()) {
			ArrayList<Node> neighbours = getNeighbours(entry.getValue());
			edges.put(entry.getValue(), neighbours);

			totalEdges += neighbours.size();
		}
		System.out.println("FastestPath: Graph has a total of " + totalEdges + " edges.");
	}

	public void runAStar() {
		Set<Node> explored = new HashSet<Node>();
		PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>(100, new Comparator<Node>() {
			public int compare(Node i, Node j) {
				if (i.totalDistance > j.totalDistance) {
					return 1;
				} else if (i.totalDistance < j.totalDistance) {
					return -1;
				} else {
					if (i.getDistanceToStart() > j.getDistanceToStart()) {
						return 1;
					} else if (i.getDistanceToStart() < j.getDistanceToStart()) {
						return -1;
					} else {
						return 0;
					}
				}
			}
		});	// Frontier that is sorted

		Node startNode = nodes.get(startCoord); // Where do we get this node??
		Node goalNode = nodes.get(goalCoord);
		Boolean found = false;

		startNode.setDistanceToStart(0);
		priorityQueue.add(startNode);
		while (priorityQueue.size() != 0 && !found) {
			Node current = priorityQueue.poll();
			explored.add(current);
			if (current.isEqual(goalNode)) {
				found = true;
				ArrayList<Node> finalPath = returnPath(goalNode);

				for (int i = 0; i < finalPath.size(); i++) {
					System.out.println(finalPath.get(i).getCell().getY() + ", " + finalPath.get(i).getCell().getX());
				}
			}
			ArrayList<Node> neighbours = getNeighbours(current);
			for (int i = 0; i < neighbours.size(); i++) {
				Node n = neighbours.get(i);
				int tempDistanceToStart = n.getDistanceToStart() + 1;
				int tempTotalDistance = n.getHeuristic() + tempDistanceToStart;
				if (explored.contains(n) && tempTotalDistance >= n.getTotalDistance()) {
					continue;
				} else if (!priorityQueue.contains(n) || tempTotalDistance < n.getTotalDistance()) {
					n.setParent(current);
					n.setDistanceToStart(tempDistanceToStart);
					n.setTotalDistance(tempTotalDistance);
					if (priorityQueue.contains(n)) {
						priorityQueue.remove(n);
					}
					priorityQueue.add(n);
				}
			}
		}
	}

	public ArrayList<Node> returnPath(Node target) {
		ArrayList<Node> path = new ArrayList<Node>();
		for (Node node = target; node != null; node = node.parent) {
			path.add(node);
		}
		Collections.reverse(path);
		return path;
	}

	private ArrayList<Node> getNeighbours(Node currNode) {
		ArrayList<Node> toReturn = new ArrayList<Node>();

		int y = currNode.getCell().getY();
		int x = currNode.getCell().getX();

		Coordinate[] coordsToGet = new Coordinate[4];
		coordsToGet[0] = new Coordinate(y + 1, x);	// Top
		coordsToGet[1] = new Coordinate(y - 1, x);	// Bottom
		coordsToGet[2] = new Coordinate(y, x + 1);	// Right
		coordsToGet[3] = new Coordinate(y, x - 1);	// Left

		for (int i = 0; i < coordsToGet.length; i++) {
			Node nodeToGet = nodes.get(coordsToGet[i]);

			if (nodeToGet != null) {
				toReturn.add(nodeToGet);
			}
		}

		return toReturn;
	}

	public class Node {
		private int heuristic; // h
		private int distanceToStart; // g
		private int totalDistance; // f
		private Node parent;
		private Cell cell;

		public Node(int heuristic, Cell cell) {
			this.heuristic = heuristic;
			this.cell = cell;
			this.setDistanceToStart(0);
			this.parent = null;
		}

		public int getHeuristic() {
			return heuristic;
		}

		public Cell getCell() {
			return cell;
		}

		public int getDistanceToStart() {
			return distanceToStart;
		}

		public int getTotalDistance() {
			return totalDistance;
		}

		public void setParent(Node parent) {
			this.parent = parent;
		}

		public void setDistanceToStart(int distance) {
			this.distanceToStart = distance;
		}

		public void setTotalDistance(int distance) {
			this.totalDistance = distance;
		}

		public boolean isEqual(Node n) {
			int nY = n.getCell().getY();
			int nX = n.getCell().getX();

			int thisY = this.getCell().getY();
			int thisX = this.getCell().getX();

			if (nY == thisY && nX == thisX)
				return true;
			else
				return false;
		}
	}
}
