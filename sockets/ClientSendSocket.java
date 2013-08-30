package sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import client.StateData;

public class ClientSendSocket implements Runnable {

	private Socket receiveSocket;
	private PrintWriter out;
	private BufferedReader in;

	public void run() {
		
		StateData stateData = StateData.getInstance();
		
		try {
			
			// connect to the server
			receiveSocket = new Socket(stateData.ipAddress, stateData.portNo);
			out = new PrintWriter(receiveSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(receiveSocket.getInputStream()));
			
			// handshake with the server
			String inLine;
			out.println("sender thread");
			in.readLine(); // "send player number"
			
			// get the player number from the state data and send it
			while (stateData.getPlayerNo() == "") {
				Thread.sleep(100);
			}
			out.println(stateData.getPlayerNo());
			in.readLine(); // "ack"
			
			// send key events as we get them
			while (true) {
				
				stateData.keyAvailableSemaphore.acquire();
				out.println(stateData.getNextKey());
				in.readLine(); // "ack"
			}
			
			
		// TODO: add error checking stuff below to deal with an error
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
