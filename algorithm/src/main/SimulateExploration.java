package main;

import algorithms.Exploration;
import entities.Map;
import entities.Robot;
import gui.GUI;

public class SimulateExploration implements Runnable {

	private GUI gui;
	private Robot robot;
	private Map exploredMap, testMap;
	private Exploration explorer;

	public SimulateExploration() {
		this.gui = Main.gui;
		this.testMap = Main.testMap;

		// Reset all objects to a clean state
		robot = Main.robot;
		exploredMap = Main.exploredMap;
		explorer = Main.explorer;
	}

	@Override
	public void run() {
		System.out.println(":: " + getClass().getName() + " Thread Started ::");

		while (!Thread.currentThread().isInterrupted()) {
			try {
				exploredMap.simulatedReveal(robot, testMap);
				gui.refreshGUI(robot, exploredMap);

				// Run exploration for one step
				boolean done = explorer.executeOneStep(robot, exploredMap);

				exploredMap.simulatedReveal(robot, testMap);
				gui.refreshGUI(robot, exploredMap);

				if (done)
					break;

				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}

		System.out.println(":: " + getClass().getName() + " Thread Ended ::\n");
	}
}
