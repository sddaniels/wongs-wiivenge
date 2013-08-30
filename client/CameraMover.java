package client;

public class CameraMover {

	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	// movement vars
	private double addedAmt = 0;
	
	// strafe vars
	private final static double strafeStopVal = 0.1;
	private final static double strafeIncrement = 0.02;
	private boolean movingLeft = false;
	private boolean movingRight = false;
	
	// dodge vars
	private final static double dodgeStopVal = -0.1;
	private final static double dodgeIncrement = 0.02;
	private final static long dodgeHoldTime = 200; // milliseconds
	private long dodgeStartTime;
	private boolean movingBack = false;
	private boolean holding = false;
	private boolean movingForward = false;
	
	/////////////////////////////////
	// public methods
	/////////////////////////////////
	
	public void moveCamera() {
		
		// don't start another move if one is in progress
		if (moveInProgress()) {
			processStrafeLeft();
			processStrafeRight();
			processDodgeBack();
			return;
		}
		
		// check for a new trigger
		boolean[] triggers = StateData.getInstance().getPlayerMoveTriggers();
		if (triggers[0]) movingLeft = true;
		else if (triggers[1]) movingRight = true;
		else if (triggers[2]) movingBack = true;
	
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////

	private boolean moveInProgress() {
		return movingLeft || movingRight || movingBack || movingForward || holding;
	}
	
	private void processStrafeLeft() {
		
		if (movingLeft) {
			
			// if the stop value reached, stop moving
			if (addedAmt == strafeStopVal) {
				movingLeft = false;
				addedAmt = 0;
				
			// otherwise just keep moving
			} else {
				addedAmt += strafeIncrement;
				StateData.getInstance().camPos[0] += strafeIncrement;
			}
		}
	}
	
	private void processStrafeRight() {
		
		if (movingRight) {
			
			// if the stop value has been reached, stop moving
			if (addedAmt == -strafeStopVal) {
				movingRight = false;
				addedAmt = 0;
				
			// otherwise just keep moving
			} else {
				addedAmt -= strafeIncrement;
				StateData.getInstance().camPos[0] -= strafeIncrement;
			}
		}
	}
	
	private void processDodgeBack() {
		
		if (movingBack) {
			
			// if the stop value has been reached, stop moving
			if (addedAmt == dodgeStopVal) {
				dodgeStartTime = System.currentTimeMillis();
				movingBack = false;
				holding = true;
				
			// otherwise just keep moving
			} else {
				addedAmt -= dodgeIncrement;
				StateData.getInstance().camPos[2] -= dodgeIncrement;
			}
			
		} else if (holding) {
			
			if ((System.currentTimeMillis() - dodgeStartTime) >= dodgeHoldTime) {
				holding = false;
				movingForward = true;
				addedAmt = 0;
			}
			
		} else if (movingForward) {
		
			// if the stop value has been reached, stop moving
			if (addedAmt == -dodgeStopVal) {
				movingForward = false;
				addedAmt = 0;
				
			// otherwise just keep moving
			} else {
				addedAmt += dodgeIncrement;
				StateData.getInstance().camPos[2] += dodgeIncrement;
			}
			
			
		}
	}
	
}
