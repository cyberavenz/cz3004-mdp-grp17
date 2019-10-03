package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Robot;

public class FastestPath {

	private HashMap<Coordinate, Node> nodes = new HashMap<Coordinate, Node>();
	private HashMap<Node, ArrayList<Node>> edges = new HashMap<Node, ArrayList<Node>>();

	private Map currMap;
	private Coordinate startCoord, goalCoord;

	public FastestPath(Map currMap, Coordinate startCoord, Coordinate goalCoord) {
		this.currMap = currMap;
		this.startCoord = startCoord;
		this.goalCoord = goalCoord;

		/* Step 1: Create graph of nodes with heuristic */
		for (int y = 1; y <= Map.maxY - 2; y++) {
			for (int x = 1; x <= Map.maxX - 2; x++) {
				Coordinate currPos = new Coordinate(y, x);

				Robot robot = new Robot(currPos, Robot.NORTH);
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
		PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>();	// Frontier that is sorted
		HashMap<Node, Node> childOf = new HashMap<Node, Node>();		// Child, Parent
		Node startNode = nodes.get(startCoord);
		startNode.setDistance(0);
		priorityQueue.add(startNode);

		Node currNode = null;

		while (!priorityQueue.isEmpty()) {
			currNode = priorityQueue.poll();

			if (!currNode.isVisited) {
				currNode.setVisited();

				if (currNode.isEqual(nodes.get(goalCoord))) {
					// TODO Reconstruct final path
				}

				ArrayList<Node> neighbours = getNeighbours(currNode);
				for (int i = 0; i < neighbours.size(); i++) {
					if (!neighbours.get(i).isVisited) {
						int totalDistance = 1 + neighbours.get(i).getHeuristic();

						if (totalDistance < neighbours.get(i).getDistance()) {
							neighbours.get(i).setDistance(totalDistance);
							childOf.put(neighbours.get(i), currNode);
							
							priorityQueue.add(neighbours.get(i));
						}
					}
				}
			}
		}
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
		private int heuristic;
		private Cell cell;
		private boolean isVisited;
		private int distance;

		public Node(int heuristic, Cell cell) {
			this.heuristic = heuristic;
			this.cell = cell;
			this.isVisited = false;
			this.setDistance(Integer.MAX_VALUE);
		}

		public int getHeuristic() {
			return heuristic;
		}

		public Cell getCell() {
			return cell;
		}

		public boolean isVisited() {
			return isVisited;
		}

		public void setVisited() {
			this.isVisited = true;
		}

		public int getDistance() {
			return distance;
		}

		public void setDistance(int distance) {
			this.distance = distance;
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
