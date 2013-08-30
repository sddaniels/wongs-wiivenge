package client;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class StateData {

	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	static private StateData _instance;
	
	// connection info
	private Object connectionLock = new Object();
	public int portNo;
	public String ipAddress;
	private String playerNo = "";
	
	// camera/player position
	public float[] camPos = { 0f, -0.43f, 0f };
	private Object playerRelativePosLock = new Object();
	private Object oppRelativePosLock = new Object();
	private int playerRelativePos = 0;
	private int oppRelativePos = 0;
	
	// health percentage
	private Object healthLock = new Object();
	private double playerHealth = 1.0;
	private double enemyHealth = 1.0;
	private Object gameOverLock = new Object();
	private boolean gameOver = false;
	
	// motion triggers
	private Object swingLock = new Object();
	private boolean[] swingStatus = new boolean[3]; // 0: block, 1: horizontal, 2: vertical
	private Object oppSwingLock = new Object();
	private boolean[] oppSwingStatus = new boolean[3]; // 0: block, 1: horizontal, 2: vertical
	private Object playerMoveLock = new Object();
	private boolean[] playerMoveStatus = new boolean[3]; // 0: strafe left, 1: strafe right, 2: dodge back
	private Object oppMoveLock = new Object();
	private boolean[] oppMoveStatus = new boolean[3]; // 0: strafe left, 1: strafe right, 2: dodge back
	private int playerMoveQueue = 0;
	private int oppMoveQueue = 0;
	
	// key semaphore
	public Semaphore keyAvailableSemaphore = new Semaphore(0);
	private Object keyLock = new Object();
	private LinkedList<String> keys = new LinkedList<String>();
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	private StateData() {}
	
	/////////////////////////////////
	// public methods (thread safe)
	/////////////////////////////////
	
	static public StateData getInstance() {
		
		if (_instance == null) {
			synchronized(StateData.class) {
				if (_instance == null) _instance = new StateData();
			}
		}
		
		
		return _instance;
	}
	
	//// my health property
	
	public double getPlayerHealth() {
		
		double returnVal;
		synchronized(healthLock) {
			returnVal = playerHealth;
		}
		return returnVal;
	}
	
	public void setPlayerHealth(double newVal) {
		synchronized(healthLock) {
			playerHealth = newVal;
		}
	}
	
	//// enemy health property
	
	public double getEnemyHealth() {
		
		double returnVal;
		synchronized(healthLock) {
			returnVal = enemyHealth;
		}
		return returnVal;
	}
	
	public void setEnemyHealth(double newVal) {
		synchronized(healthLock) {
			enemyHealth = newVal;
		}
	}
	
	//// game over property
	
	public boolean getGameOverStatus() {
		
		boolean returnVal;
		synchronized(gameOverLock) {
			returnVal = gameOver;
		}
		return returnVal;
	}
	
	public void setGameOverStatus(boolean newVal) {
		synchronized (gameOverLock) {
			gameOver = newVal;
			if (gameOver) {
				SoundEffects.playSoundEffect("data/wilhelm.wav");
			}
		}
	}
	
	//// player sword swing properties
	
	public boolean[] getSwingTriggers() {
		
		boolean[] returnVal = new boolean[3];
		synchronized(swingLock) {
			returnVal[0] = swingStatus[0];
			returnVal[1] = swingStatus[1];
			returnVal[2] = swingStatus[2];
			swingStatus[0] = false;
			swingStatus[1] = false;
			swingStatus[2] = false;
		}
		return returnVal;
	}
	
	public void setBlockSwingTrigger() {
		synchronized(swingLock) {
			swingStatus[0] = true;
		}
	}
	
	public void setHorizSwingTrigger() {
		synchronized(swingLock) {
			swingStatus[1] = true;
		}
	}
	
	public void setVertSwingTrigger() {
		synchronized(swingLock) {
			swingStatus[2] = true;
		}
	}
	
	//// opponent sword swing properties
	
	public boolean[] getOppSwingTriggers() {
		
		boolean[] returnVal = new boolean[3];
		synchronized(oppSwingLock) {
			returnVal[0] = oppSwingStatus[0];
			returnVal[1] = oppSwingStatus[1];
			returnVal[2] = oppSwingStatus[2];
			oppSwingStatus[0] = false;
			oppSwingStatus[1] = false;
			oppSwingStatus[2] = false;
		}
		return returnVal;
	}
	
	public void setOppBlockSwingTrigger() {
		synchronized(oppSwingLock) {
			oppSwingStatus[0] = true;
		}
	}
	
	public void setOppHorizSwingTrigger() {
		synchronized(oppSwingLock) {
			oppSwingStatus[1] = true;
		}
	}
	
	public void setOppVertSwingTrigger() {
		synchronized(oppSwingLock) {
			oppSwingStatus[2] = true;
		}
	}
	
	//// player movement properties
	
	public boolean[] getPlayerMoveTriggers() {
		
		boolean[] returnVal = new boolean[3];
		synchronized(playerMoveLock) {
			
			returnVal[0] = false;
			returnVal[1] = false;
			
			// handle move left and move right
			if (playerMoveQueue < 0) {
				returnVal[0] = true;
				playerMoveQueue++;
				SoundEffects.playSoundEffect("data/move.wav");
			} else if (playerMoveQueue > 0) {
				returnVal[1] = true;
				playerMoveQueue--;
				SoundEffects.playSoundEffect("data/move.wav");
 			}
			
			// handle dodge
			if (playerMoveStatus[2]) {
				returnVal[2] = true;
				SoundEffects.playSoundEffect("data/move.wav");
			}
			playerMoveStatus[2] = false;
		}
		return returnVal;
	}
	
	public void setPlayerStrafeTrigger(int distance) {
		synchronized(playerMoveLock) {
			playerMoveQueue += distance;
		}
	}
	
	public void setPlayerDodgeBackTrigger() {
		synchronized(playerMoveLock) {
			playerMoveStatus[2] = true;
		}
	}
	
	//// opponent movement properties
	
	public boolean[] getOppMoveTriggers() {
		
		boolean[] returnVal = new boolean[3];
		synchronized(oppMoveLock) {
			
			returnVal[0] = false;
			returnVal[1] = false;
			
			// handle move left and right
			if (oppMoveQueue < 0) {
				returnVal[1] = true;
				oppMoveQueue++;
				SoundEffects.playSoundEffect("data/move.wav");
			} else if (oppMoveQueue > 0) {
				returnVal[0] = true;
				oppMoveQueue--;
				SoundEffects.playSoundEffect("data/move.wav");
			}

			// handle dodge back
			if (oppMoveStatus[2]) {
				returnVal[2] = true;
				SoundEffects.playSoundEffect("data/move.wav");
			}
			oppMoveStatus[2] = false;
		}
		return returnVal;
	}
	
	public void setOppStrafeTrigger(int distance) {
		synchronized(oppMoveLock) {
			oppMoveQueue += distance;
		}
	}
	
	public void setOppDodgeBackTrigger() {
		synchronized(oppMoveLock) {
			oppMoveStatus[2] = true;
		}
	}
	
	//// connection info properties
	
	public String getPlayerNo() {
		
		String returnVal;
		synchronized(connectionLock) {
			returnVal = playerNo;
		}
		return returnVal;
	}
	
	public void setPlayerNo(String newVal) {
		synchronized(connectionLock) {
			playerNo = newVal;
		}
	}
	
	//// keys property
	
	public String getNextKey() {
		
		String returnVal;
		synchronized (keyLock) {
			returnVal = keys.removeFirst();
		}
		return returnVal;
	}
	
	public void addKey(String newVal) {
		synchronized (keyLock) {
			keys.add(newVal);
		}
		// let the sender thread know a key was pressed
		keyAvailableSemaphore.release();
	}
	
	//// player relative position property
	
	public int getPlayerRelativePos() {
		int returnVal;
		synchronized (playerRelativePosLock) {
			returnVal = playerRelativePos;
		}
		return returnVal;
	}
	
	public void setPlayerRelativePos(int newVal) {
		synchronized (playerRelativePosLock) {
			playerRelativePos = newVal;
		}
	}
	
	//// opponent relative position property
	
	public int getOppRelativePos() {
		int returnVal;
		synchronized (oppRelativePosLock) {
			returnVal = oppRelativePos;
		}
		return returnVal;
	}
	
	public void setOppRelativePos(int newVal) {
		synchronized (oppRelativePosLock) {
			oppRelativePos = newVal;
		}
	}
	
}
