package communications;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TCPComm {
	public static final String SERVER_IP = "127.0.0.1";	// Rasp Pi: 192.168.17.17
	public static final int PORT = 4040;

	/* TEMP: Just test TCP connection */
	public static void main(String[] args) {
		String sentence;
		String modifiedSentence;
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		try {
			System.out.println("Creating TCP connection with IP: " + SERVER_IP + ":" + PORT + " ...");

			Socket clientSocket = new Socket(SERVER_IP, PORT);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			sentence = inFromUser.readLine();
			outToServer.writeBytes(sentence + 'n');
			modifiedSentence = inFromServer.readLine();
			System.out.println("FROM SERVER: " + modifiedSentence);
			clientSocket.close();
		} catch (IOException e) {
			System.err.format("TCP Connection IOException: %s%n", e);
		}
	}

}
