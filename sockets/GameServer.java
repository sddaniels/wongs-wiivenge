package sockets;

import game_engine.Commands;
import game_engine.Engine;
import game_engine.Synch;

import java.io.*;
import java.net.*;

public class GameServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String inLine;
		int incomingPort;
		Commands com[] = new Commands[2];
		com[0] = new Commands();
		com[1] = new Commands();
		
		Engine engine = new Engine(com[0], com[1], 5);
		
		try {
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader b = new BufferedReader(isr); 
			String hostAddress = InetAddress.getLocalHost().getHostName();

			// ask the user for connection info
			System.out.println("Wiimote Game Server Started . . .\n");
			System.out.println("What port will clients connect to?");
			inLine = b.readLine();
			
			if (inLine.equals("")) {
				incomingPort = 5678;
				System.out.println("Default port: 5678");
			} else {
				incomingPort = Integer.parseInt(inLine);
			}
			System.out.println("Hosted on:    " + hostAddress);
			
			// get ready to receive connections
			ServerSocket server = new ServerSocket(incomingPort);
			//ServerThread handler[] = new ServerThread[4];
			
			// Connect clients
			for (int i=0; i < 4; i++) {
				// wait for a client to connect
				new ServerThread(server.accept(), com);
			}
			
			// Start the engine
			engine.begin();
			
			while (engine.getFinish())
			{
				// Send screen data
				/*for(ServerThread h : handler)
				{
					
				}*/
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
