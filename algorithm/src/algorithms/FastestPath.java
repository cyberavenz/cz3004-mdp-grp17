package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Node;
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

	public ArrayList<Coordinate> runAStar() {
		PriorityQueue<Node> orderedFrontier = new PriorityQueue<Node>();	// Frontier that is sorted
		HashMap<Node, Node> childParent = new HashMap<Node, Node>();		// Child, Parent (Current, Previous)

		Node startNode = this.nodes.get(startCoord);	// Get Start Node
		startNode.setCost(0);							// Set Start Node total cost to 0
		orderedFrontier.add(startNode);					// Add it to the frontier

		Node currNode;
		while (!orderedFrontier.isEmpty()) {
			currNode = orderedFrontier.poll();

			if (!currNode.isVisited()) {
				currNode.setVisited(true);

				/* Check if it is Goal Node, BREAK! */
				if (currNode.equals(this.nodes.get(goalCoord))) {
					ArrayList<Coordinate> toReturn = new ArrayList<Coordinate>();
					// TODO Reconstruct final path based on childParent relation
					return toReturn;
				}

				/* Check neighbours */
				ArrayList<Node> neighbours = getNeighbours(currNode);
				for (int i = 0; i < neighbours.size(); i++) {
					Node currNeighbour = neighbours.get(i);	// Current Neighbour Node for easy reference

					if (!currNeighbour.isVisited()) {		// Only traverse currNeighbour if unvisited

						// Total Cost = currNode.distance + weight of 1 (each edge) + currNeighbour.heuristic
						int totalCost = currNode.getCost() + 1 + currNeighbour.getHeuristic();

						if (totalCost < currNeighbour.getCost()) {
							currNeighbour.setCost(totalCost);		// Update currNeighbour's distance
							// TODO totalCost /= totalDistance
							
							childParent.put(currNeighbour, currNode);	// Keep track of currNeighbour's parent

							orderedFrontier.add(currNeighbour);		// Add currNeighbour to frontier
						}
					}
				}
			}
		}

		System.out.println("FastestPath: A*Star is unable to find a path to goal node.");
		return new ArrayList<Coordinate>();	// No paths found, return empty list
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
}
