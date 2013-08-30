package client;

import java.awt.event.*;

public class GameController implements KeyListener {

	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	private static final long KEY_DELAY = 300;
	
	private GameWindow gameWindow;
	private int fps;
	private long lastKeyPressTime = 1000;
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	public GameController(int fps) {
		
		this.fps = fps;
	}
	
	/////////////////////////////////
	// public methods
	/////////////////////////////////
	
	public void keyPressed(KeyEvent e) {
		
		// slow down the key presses a bit
		if ((System.currentTimeMillis() - lastKeyPressTime) > KEY_DELAY) {

			int keyCode = e.getKeyCode();
			StateData stateData = StateData.getInstance();
			
			if (keyCode == KeyEvent.VK_LEFT) {
				stateData.addKey("strafe left");
			} else if (keyCode == KeyEvent.VK_RIGHT) {
				stateData.addKey("strafe right");
			} else if (keyCode == KeyEvent.VK_DOWN) {
				stateData.addKey("dodge back");
			} else if (keyCode == KeyEvent.VK_D) {
				stateData.addKey("horizontal slash");
			} else if (keyCode == KeyEvent.VK_F) {
				stateData.addKey("vertical slash");
			} else if (keyCode == KeyEvent.VK_S) {
				stateData.addKey("block");
			}
			
			lastKeyPressTime = System.currentTimeMillis();
		
		}
	}

	public void keyReleased(KeyEvent e) {
		// empty
	}

	public void keyTyped(KeyEvent e) {
		// empty
	}

	/////////////////////////////////
	// protected methods
	/////////////////////////////////
	
	protected void createAndShow() {
		
		// schedule a a job on the event dispatching thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				gameWindow = new GameWindow(fps);
				gameWindow.createAndShow();
				setupObjects();
				
				
			}
		});
		
//		SoundEffects.playSoundEffect("data/start.wav");
//		try {
//			Thread.sleep(1100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		SoundEffects.playRepeatingSoundEffect("data/ambient-saber.wav");
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////
	
	private void setupObjects() {
		
		/* Add a window listener to listen for the window closing
		 * event. We have to start a new thread when this happens
		 * in order to make sure the animator is fully stopped before
		 * the program exits.
		 */
		gameWindow.addWindowListener(
				new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						new Thread(
								new Runnable() {
									public void run() {
										gameWindow.animator.stop();
										System.exit(0);
									}
								} // end of Runnable
						).start();
					} // end of WindowClosing
				} // end of WindowAdapter
		); // end of addWindowListener
		
		// add the key listener
		gameWindow.addKeyListener(this);
	}
	
}
