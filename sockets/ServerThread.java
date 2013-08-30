package sockets;

import game_engine.Commands;
import game_engine.Engine;
import game_engine.ScreenUpdateInfo;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {

	private Socket client;
	private Commands com[];
	Thread t;
	
	
	public ServerThread(Socket client, Commands commandHandler[]) {
		this.client = client;
		this.com = commandHandler;
		System.out.println("connected");
		t = new Thread(this);
		t.start();
	}
	
	public void run() {
		
		try {
			
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			String inLine;
			
			inLine = in.readLine();
			
			// check for correct starting handshake
			if (inLine.equals("new player: receiver thread")) {
				
				// get next player number
				int player = ScreenUpdateInfo.getInstance().getNextPlayerNo();
				
				System.out.println("Sender Thread: " + player);
				ServerSendSocket serverSend = new ServerSendSocket(client, in, out, player);
				serverSend.startSenderCommLoop();
				
			// check for a correct sender thread
			} else if (inLine.equals("sender thread")) {
			
				ServerReceiveSocket serverReceive = new ServerReceiveSocket(client, in, out, com);
				serverReceive.startReceiverCommLoop();
				
			} else {
				//TODO print some kind of error or something
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	    
	}
}
