package communications;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

import entities.Map;
import entities.Robot;
import main.Main;

public class TCPComm {
	public static final String SERVER_IP = "192.168.17.17";	// Rasp Pi: 192.168.17.17
	public static final int PORT = 4042;
	public static final char BLUETOOTH = 'b', SERIAL = 's', RASP_PI = 'r';

	private Socket clientSocket;
	private DataOutputStream outgoingStream;
	private BufferedReader incomingStream;

	/**
	 * Constructor for TCP Communication.
	 */
	public TCPComm() {
		try {
			System.out.println("Initiating TCP connection with IP: " + SERVER_IP + ":" + PORT + "... ");
			clientSocket = new Socket(SERVER_IP, PORT);
			System.out.println("TCP connection successfully established.");

			outgoingStream = new DataOutputStream(clientSocket.getOutputStream());
			incomingStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
			Main.gui.setModeColour(false);
		}
	}

	/**
	 * Send a message/command to the specified recipient.
	 * 
	 * @param recipient
	 * @param message
	 */
	public void send(char recipient, String message) {
		try {
			if (this.clientSocket != null) {
				String outgoingString = "@" + recipient + message + "!";
				this.outgoingStream.writeBytes(outgoingString);
				System.out.println("TCP Sent: " + outgoingString);
			}
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
			Main.gui.setModeColour(false);
		}
	}

	/**
	 * Read incoming stream.
	 * 
	 * @return message
	 */
	public String read() {
		String toReturn = new String();

		try {
			toReturn = this.incomingStream.readLine();
			System.out.println("TCP Received: " + toReturn);
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
			Main.gui.setModeColour(false);
		}

		return toReturn;
	}

	/**
	 * STRICTLY read incoming stream to find a message from the specified device. Any messages from a
	 * non-specified device will be discarded.
	 * <p>
	 * This function is implemented in a very strict (or bad) way as the while(true) loop is blocking
	 * and it discards everything else. Might need to be re-implemented.
	 * </p>
	 * 
	 * @param device
	 * @return
	 */
	public String readFrom(char device) {
		while (true) {
			System.out.println("TCP: Waiting for message from " + device + "...");
			String toReturn = read();

			if (toReturn != null && toReturn.charAt(0) == device)
				return toReturn.substring(1);
		}
	}

	/**
	 * Get connection status of <tt>TCPComm</tt>.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		if (this.clientSocket == null)
			return false;

		return this.clientSocket.isConnected();
	}

	/**
	 * Closes the socket.
	 * 
	 * @throws IOException
	 */
	public void close() {
		if (this.clientSocket != null) {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Send movement instructions to Serial and Bluetooth.
	 * 
	 * @param navigateSteps
	 */
	public void sendFastestPath(LinkedList<String> navigateSteps) {
		if (navigateSteps.isEmpty()) {
			System.err.println("Unable to send Fastest Path as it does not exist. Call runAStar() again.");
			return;
		}

		String pointer = navigateSteps.poll();
		String forArduino = "";
		String forAndroid = "";

		while (pointer != null) {

			int numOfForwards = 0;
			while (pointer == "F01") {
				numOfForwards++;
				pointer = navigateSteps.poll();
			}
			if (numOfForwards > 0) {
				forArduino += "F" + String.format("%02d", numOfForwards) + "|";
				forAndroid += "F" + String.format("%02d", numOfForwards);
			}

			if (pointer != null) {
				forArduino += pointer + "|";
				forAndroid += pointer;
			}

			pointer = navigateSteps.poll();

		}

		this.send(TCPComm.SERIAL, forArduino);
		this.send(TCPComm.BLUETOOTH, forAndroid);
	}

	/**
	 * Generate MDF String for Bluetooth (Android tablet).
	 * 
	 * @param map
	 * @param robot
	 * @return
	 */
	public static String genMDFBluetooth(Map map, Robot robot) {
		String toReturn = new String();

		toReturn = "MDF" + "|" + map.getP1Descriptors() + "|" + map.getP2Descriptors() + "|" + robot.getCurrDir() + "|"
				+ (19 - robot.getCurrPos().getY()) + "|" + robot.getCurrPos().getX() + "|" + "0";

		return toReturn;
	}

//	private void startListening() {
//		// Only 1 instance should be running. Cancel previous executor if it exists.
//		if (tcpListenerExecutor != null)
//			tcpListenerExecutor.shutdown();
//
//		// Assign a new thread pool
//		tcpListenerExecutor = Executors.newScheduledThreadPool(1);
//
//		// Create a Runnable task
//		Runnable tcpListener = new Runnable() {
//			@Override
//			public void run() {
//				while (true) {
//					String msg = read();
//					if (msg[0] == 's') {
//						
//					}
//				}
//			}
//		};
//
//		tcpListenerExecutor.execute(tcpListener);
//	}
}
