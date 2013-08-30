package sockets;

import game_engine.Commands;
import game_engine.Synch;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerReceiveSocket {

	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private Commands com[];
	private int player;
	
	public ServerReceiveSocket(Socket client, BufferedReader in, PrintWriter out, Commands commands[]) {
		this.client = client;
		this.in = in;
		this.out = out;
		this.com = commands;
	}
	
	public void startReceiverCommLoop() {
		
		String inLine;
		
		try {
			
			out.println("send player number");
			inLine = in.readLine();
			System.out.println("Receive " + inLine);
			player = Integer.parseInt(inLine) - 1;
			Synch.getInstance().engine.release();
			Synch.getInstance().serverReceive[player].acquire();
			out.println("ack");
			
			Synch.getInstance().serverReceive[player].release();
			
			// wait for client updates here
			inLine = in.readLine();
			out.println("ack");
			while (inLine != null) {
				
				Synch.getInstance().serverReceive[player].acquire();
				
				// update magic mojo here
				System.out.println(player + 1 + " " + inLine);

				if (inLine.equalsIgnoreCase("strafe left"))
					com[player].add_command(Commands.STRAFE_LEFT);
				else if (inLine.equalsIgnoreCase("strafe right"))
					com[player].add_command(Commands.STRAFE_RIGHT);
				else if (inLine.equalsIgnoreCase("dodge back"))
					com[player].add_command(Commands.DODGE);
				else if (inLine.equalsIgnoreCase("horizontal slash"))
					com[player].add_command(Commands.HORIZ_SWING);
				else if (inLine.equalsIgnoreCase("vertical slash"))
					com[player].add_command(Commands.VERT_SWING);
				else if (inLine.equalsIgnoreCase("block"))
					com[player].add_command(Commands.BLOCK);
				
				inLine = in.readLine();
				out.println("ack");
			}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
