package sockets;

import game_engine.*;

import java.io.*;
import java.net.Socket;

public class ServerSendSocket {

	private Socket client;
	private BufferedReader in;
	private PrintWriter out;
	private int player;
	
	public ServerSendSocket(Socket client, BufferedReader in, PrintWriter out, int pnum) {
		this.client = client;
		this.in = in;
		this.out = out;
		this.player = pnum;
	}
	
	public void startSenderCommLoop() throws IOException {
		
		// TODO decide whether player one or two here
		
		out.println(player);
		in.readLine(); // ack
		
		// TODO decide when game is ready to begin
		//      may want to make this a separate method?
		
		try {
			Synch.getInstance().serverSend[player-1].acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		out.println("ready to start game");
		in.readLine(); // ready to receive
		
		// update magic mojo here
		
		// loop de loo
		while (true) {
			
			try {
				Synch.getInstance().serverSend[player-1].acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (ScreenUpdateInfo.getInstance().updated())
			{
				for (int i=0; i < 2; i++)
				{
					if (ScreenUpdateInfo.getInstance().blockChange(i))
						out.println(i+1 + " block " + ScreenUpdateInfo.getInstance().player[i].blocking);
					
					if (ScreenUpdateInfo.getInstance().dodgeChange(i))
						out.println(i+1 + " dodge " + ScreenUpdateInfo.getInstance().player[i].dodging);
					
					if (ScreenUpdateInfo.getInstance().healthChange(i))
						out.println(i+1 + " health " + ScreenUpdateInfo.getInstance().player[i].health);
					
					if (ScreenUpdateInfo.getInstance().moved(i))
					{
						out.println(i+1 + " position " + ScreenUpdateInfo.getInstance().player[i].position);
					}
					
					if (ScreenUpdateInfo.getInstance().swingChange(i))
					{
						if (ScreenUpdateInfo.getInstance().player[i].swinging == Info.SWORD_RESTING)
							out.println(i+1 + " slash none");
						
						if (ScreenUpdateInfo.getInstance().player[i].swinging == Info.HORIZONTAL)
							out.println(i+1 + " slash horizontal");
						
						if (ScreenUpdateInfo.getInstance().player[i].swinging == Info.VERTICAL)
							out.println(i+1 + " slash vertical");
					}
					
					if (ScreenUpdateInfo.getInstance().clangChange())
					{
						if (ScreenUpdateInfo.getInstance().clang != -1)
							out.println("clang");
					}
				}
			}
			
			Synch.getInstance().engine.release();
			
		}
	}
}
