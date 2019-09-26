package communications;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPComm {
	public static final String SERVER_IP = "192.168.17.17";	// Rasp Pi: 192.168.17.17
	public static final int PORT = 4040;
	public static final char ANDROID = 'b', ARDUINO = 's', RASP_PI = 'r';

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
			this.outgoingStream.writeBytes('@' + recipient + message + '!');
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
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
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
		}

		return toReturn;
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

}
