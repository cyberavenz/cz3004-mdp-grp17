package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import entities.Cell;
import entities.Coordinate;
import entities.Map;
import entities.Node;
import entities.Robot;
import entities.Robot.Rotate;

public class FastestPath {

	private HashMap<Coordinate, Node> nodes = new HashMap<Coordinate, Node>();
	private HashMap<Node, ArrayList<Node>> edges = new HashMap<Node, ArrayList<Node>>();

	private Coordinate startCoord, goalCoord;

	private ArrayList<Node> finalPath = new ArrayList<Node>();

	public FastestPath(Map currMap, Coordinate startCoord, Coordinate goalCoord) {
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
		System.out.println("FastestPath: " + nodes.size() + " nodes");

		int totalEdges = 0;
		/* Step 2: Link nodes with edges */
		for (HashMap.Entry<Coordinate, Node> entry : nodes.entrySet()) {
			ArrayList<Node> neighbours = getNeighbours(entry.getValue());
			edges.put(entry.getValue(), neighbours);

			totalEdges += neighbours.size();
		}
		System.out.println("FastestPath: " + totalEdges + " edges");
	}

	public ArrayList<Node> runAStar() {
		if (!this.finalPath.isEmpty()) {
			return this.finalPath;
		}
		
		PriorityQueue<Node> orderedFrontier = new PriorityQueue<Node>();	// Frontier that is sorted
		HashMap<Node, Node> childParent = new HashMap<Node, Node>();		// Child, Parent (Current, Previous)

		Node startNode = this.nodes.get(startCoord);	// Get Start Node
		Node goalNode = this.nodes.get(goalCoord);		// Get Goal Node
		startNode.setDistanceToStart(0);				// Set Start Node total cost to 0
		orderedFrontier.add(startNode);					// Add it to the frontier

		Node currNode;
		while (!orderedFrontier.isEmpty()) {
			currNode = orderedFrontier.poll();

			if (!currNode.isVisited()) {
				currNode.setVisited(true);

				/* Check if it is Goal Node, BREAK! */
				if (currNode.equals(goalNode)) {
					this.finalPath = genFinalPath(goalNode, childParent);
					return this.finalPath;
				}

				/* Check neighbours */
				ArrayList<Node> neighbours = getNeighbours(currNode);
				for (int i = 0; i < neighbours.size(); i++) {
					Node neighbourNode = neighbours.get(i);

					/* Only traverse currNeighbour if it is unvisited */
					if (!neighbourNode.isVisited()) {
						// Determine weight based on actual robot movement
						int weight = determineWeight(childParent.get(currNode), neighbourNode);
						int newTotalCost = currNode.getDistanceToStart() + weight + neighbourNode.getHeuristic();

						/* Only traverse currNeighbour if new totalCost is lower */
						if (newTotalCost < neighbourNode.getTotalCost()) {
							childParent.put(neighbourNode, currNode);

							neighbourNode.setDistanceToStart(currNode.getDistanceToStart() + weight);
							neighbourNode.setTotalCost(newTotalCost);

							orderedFrontier.add(neighbourNode);
						}
					}
				}
			}
		}

		System.err.println("FastestPath: A*Star is unable to find a path to goal node.");
		this.finalPath = new ArrayList<Node>();
		return this.finalPath;	// No paths found, return empty list
	}

	/**
	 * Generate navigation per step based. Call <tt>StandbyFastestPath</tt> runnable first.
	 * 
	 * @param robot
	 * @return
	 */
	public LinkedList<String> navigateSteps() {

		LinkedList<String> fastestPathBuilder = new LinkedList<String>();

		if (finalPath.isEmpty()) {
			System.err.println("Fastest Path does not exist. Call runAStar() again.");
			return fastestPathBuilder;
		}

		Robot virtualRobot;

		/* Create a Virtual robot for reference */
		// Fastest Path goes North
		if (finalPath.get(1).getCell().getY() == 2 && finalPath.get(1).getCell().getX() == 1) {
			virtualRobot = new Robot(new Coordinate(1, 1), Robot.NORTH, false);
		}

		// Fastest Path goes East
		else {
			virtualRobot = new Robot(new Coordinate(1, 1), Robot.EAST, false);
		}

		/* Traverse from 2nd step (from index 1) */
		for (int i = 1; i < finalPath.size(); i++) {
			Coordinate[] footprint = virtualRobot.getFootprint();

			Cell nextCell = finalPath.get(i).getCell();
			Coordinate nextCoord = new Coordinate(nextCell.getY(), nextCell.getX());

			if (nextCoord.equals(footprint[Robot.FRONT_CENTER])) {
				fastestPathBuilder.add("F01");

				virtualRobot.moveForward(1);
			} else if (nextCoord.equals(footprint[Robot.MIDDLE_RIGHT])) {
				fastestPathBuilder.add("R90");
				fastestPathBuilder.add("F01");

				virtualRobot.rotate(Rotate.RIGHT);
				virtualRobot.moveForward(1);
			} else if (nextCoord.equals(footprint[Robot.MIDDLE_LEFT])) {
				fastestPathBuilder.add("L90");
				fastestPathBuilder.add("F01");

				virtualRobot.rotate(Rotate.LEFT);
				virtualRobot.moveForward(1);
			}
		}

		return fastestPathBuilder;
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

	/**
	 * Reconstruct final path based on childParent relation
	 * 
	 * @param n
	 * @param childParent
	 * @return
	 */
	private ArrayList<Node> genFinalPath(Node n, HashMap<Node, Node> childParent) {
		ArrayList<Node> toReturn = new ArrayList<Node>();

		while (n != null) {
			toReturn.add(n);
			n = childParent.get(n);
		}
		Collections.reverse(toReturn);

		if (toReturn.isEmpty())
			System.err.println("FastestPath: Unable to back track to start node.");

		return toReturn;
	}

	/**
	 * Determine weight based on actual robot movement. That is, penalise whenever rotation is required.
	 * 
	 * @param currNodeParent
	 * @param neighbourNode
	 * @return
	 */
	private int determineWeight(Node currNodeParent, Node neighbourNode) {
		// parent2 does not exist, default weight
		if (currNodeParent == null)
			return 1;

		Cell currCellParent = currNodeParent.getCell();
		Cell neighbourCell = neighbourNode.getCell();

		// currCellParent and neighbourCell are along same Y axis, no penalty
		if (currCellParent.getY() == neighbourCell.getY())
			return 1;

		// currCellParent and neighbourCell are along same Y axis, no penalty
		if (currCellParent.getX() == neighbourCell.getX())
			return 1;

		// currCellParent and neighbourCell are on different axis, induce penalty
		return 3;
	}
}
