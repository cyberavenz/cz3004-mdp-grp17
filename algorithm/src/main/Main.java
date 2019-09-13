package main;

import entities.Map;
import entities.Robot;
import gui.GUI;

public class Main {

	public static Map simulatedMap = new Map("test1.txt");	// Set actual map for simulation

	/**
	 * Main program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Map unknownMap = new Map("unknown.txt");

		Robot robot = new Robot();

		/* Temporary */
//		System.out.println("FRONT_CENTER sensor is seeing: ");
//		Sensor frontCenterSensor = robot.getSensor(Robot.S_FC_N);
//		Coordinate[] facingCoords = frontCenterSensor.getFacingCoordinates(robot);
//		if (facingCoords != null)
//			for (int i = 0; i < facingCoords.length; i++) {
//				System.out.println("Y: " + facingCoords[i].getY() + " X: " + facingCoords[i].getX());
//			}

		// Show GUI
		new GUI(robot, unknownMap);

	}

}
