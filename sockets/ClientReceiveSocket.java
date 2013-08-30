package sockets;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import client.StateData;
import client.SoundEffects;

public class ClientReceiveSocket implements Runnable {
	
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
			out.println("new player: receiver thread");
			stateData.setPlayerNo(in.readLine()); // "player 1" or "player 2"
			out.println("ack");
			inLine = in.readLine();
			
			if (inLine.equals("ready to start game")) {
				out.println("ready to receive");
				
				inLine = in.readLine();
				while (inLine != null) {
					
					// update magic mojo here
					System.out.println(inLine);
					
					// figure out whether player info or opponent info is updating
					boolean isPlayerCommand = inLine.startsWith(stateData.getPlayerNo());
					inLine = inLine.substring(2);
					
					// parse the command
					if (inLine.equals("block true")) {
						
						if (isPlayerCommand) {
							stateData.setBlockSwingTrigger();
						} else {
							stateData.setOppBlockSwingTrigger();
						}
						
					} else if (inLine.equals("dodge true")) {
						
						if (isPlayerCommand) {
							stateData.setPlayerDodgeBackTrigger();
						} else {
							stateData.setOppDodgeBackTrigger();
						}
						
					} else if (inLine.startsWith("position")) {
						
						inLine = inLine.replaceFirst("position ", "");
						int newPos = Integer.parseInt(inLine);
						int oldPos = 0;
						int diff = 0;
						
						if (isPlayerCommand) {
							
							// retrieve the old value and set the new one
							oldPos = stateData.getPlayerRelativePos();
							stateData.setPlayerRelativePos(newPos);
							
						} else {
							
							//	retrieve the old value and set the new one
							oldPos = stateData.getOppRelativePos();
							stateData.setOppRelativePos(newPos);
						}
						
						// compare positions
						diff = Math.abs(newPos - oldPos);
						
						if (diff != 0) {
							
							if (isPlayerCommand) {
								
								if (newPos > oldPos) {
									for(int i = 0; i < diff; i++) {
										stateData.setPlayerStrafeTrigger(diff);
									}
								} else {
									for(int i = 0; i < diff; i++) {
										stateData.setPlayerStrafeTrigger(-diff);
									}
								}
								
							} else {
								
								if (newPos < oldPos) {
									for(int i = 0; i < diff; i++) {
										stateData.setOppStrafeTrigger(diff);
									}
								} else {
									for(int i = 0; i < diff; i++) {
										stateData.setOppStrafeTrigger(-diff);
									}
								}
							}	
						} // end diff if
						
					} else if (inLine.equals("slash horizontal")) {
						
						if (isPlayerCommand) {
							stateData.setHorizSwingTrigger();
						} else {
							stateData.setOppHorizSwingTrigger();
						}
						
					} else if (inLine.equals("slash vertical")) {
						
						if (isPlayerCommand) {
							stateData.setVertSwingTrigger();
						} else {
							stateData.setOppVertSwingTrigger();
						}
						
					} else if (inLine.startsWith("health")) {
						
						// server only sends line when health decreases, so we can play the sound here
						SoundEffects.playSoundEffect("data/bodyhit.wav");
						
						inLine = inLine.replaceFirst("health ", "");
						double newHealth = ((double)Integer.parseInt(inLine))/100;
						
						if (isPlayerCommand) {
							
							if (newHealth <= 0) {
								// youse be dead, see?
								stateData.setGameOverStatus(true);
								stateData.setPlayerHealth(newHealth);
								SoundEffects.playDelayedSoundEffect("data/imperial.wav", 1500);
								return;
							}
							
							stateData.setPlayerHealth(newHealth);
							
						} else {
							
							if (newHealth <= 0) {
								// the other guy bees dead, see?
								stateData.setGameOverStatus(true);
								stateData.setEnemyHealth(newHealth);
								SoundEffects.playDelayedSoundEffect("data/throneroommusic.wav", 1500);
								return;
							}
							
							stateData.setEnemyHealth(newHealth);

						}
						
					} else if (inLine.equals("clang")) {
						
						SoundEffects.playSoundEffect("data/clang1.wav");
					}
					
					inLine = in.readLine();
				}
			}
			
			
		// TODO: add error checking stuff below to deal with an error
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
