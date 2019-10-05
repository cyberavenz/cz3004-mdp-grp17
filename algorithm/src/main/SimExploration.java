package main;

import algorithms.Exploration;
import entities.Map;
import entities.Robot;

public class SimExploration implements Runnable {

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName() + ": Started new SimExploration thread.");
		
		// Reset all objects to a clean state
		Main.robot = new Robot();
		Main.exploredMap = new Map("unknown.txt");
		Main.exploration = new Exploration(Main.exploredMap);

		while (!Thread.currentThread().isInterrupted()) {
			try {
				Main.exploredMap.simulatedReveal(Main.robot, Main.testMap);
				// Run exploration for one step
				boolean done = Main.exploration.executeOneStep(Main.robot, Main.exploredMap);
				Main.exploredMap.simulatedReveal(Main.robot, Main.testMap);
				Main.gui.refreshGUI(Main.robot, Main.exploredMap);
				
				if (done)
					break;

				Thread.sleep(100);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		System.out.println(Thread.currentThread().getName() + ": SimExploration thread ended.");
	}
}
