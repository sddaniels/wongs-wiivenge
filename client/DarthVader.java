package client;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;

public class DarthVader extends DrawableObject {
	
	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	private PolyModel body;
	private PolyModel head;
	private Lightsaber lightsaber;
	
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
	// constructor
	/////////////////////////////////
	
	public DarthVader() {
		
		lightsaber = new Lightsaber();
		lightsaber.color = "red";
		
		lightsaber.rot[0] = 120;
		lightsaber.rot[1] = 10;
		
		lightsaber.trans[0] = -0.05f;
		lightsaber.trans[1] = 0.34f;
		lightsaber.trans[2] = 0.1f;
		
		// read the body file
		body = new PolyModel();
		body.loadModel("data/vader model.txt");
		body.scale[0] = 0.6f;
		body.scale[1] = 0.6f;
		body.scale[2] = 0.6f;
		
		head = new PolyModel();
		head.loadModel("data/vader head.txt");
		head.scale[0] = 0.04f;
		head.scale[1] = 0.05f;
		head.scale[2] = 0.05f;
		
		head.trans[1] = 0.31f;
		head.trans[2] = -0.05f;
	}
	
	/////////////////////////////////
	// public methods
	/////////////////////////////////

	@Override
	public void gldraw(GL gl) {
		
		moveCamera();
		body.draw(gl);
		head.draw(gl);
		
        // check whether a swing trigger was activated
        boolean[] triggers = StateData.getInstance().getOppSwingTriggers();
        if (triggers[0]) lightsaber.startBlock();
        if (triggers[1]) lightsaber.startHorizontalSwing();
        if (triggers[2]) lightsaber.startVerticalSwing();
        
		lightsaber.draw(gl);
		
	}
	
	public void loadTextures(GL gl) throws GLException, IOException {
		
		body.loadTextures(gl);
		head.loadTextures(gl);
		lightsaber.loadTextures(gl);
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////
	
	private void moveCamera() {
		
		// don't start another move if one is in progress
		if (moveInProgress()) {
			processStrafeLeft();
			processStrafeRight();
			processDodgeBack();
			return;
		}
		
		// check for a new trigger
		boolean[] triggers = StateData.getInstance().getOppMoveTriggers();
		if (triggers[0]) movingLeft = true;
		else if (triggers[1]) movingRight = true;
		else if (triggers[2]) movingBack = true;
	
	}
	
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
				trans[0] += strafeIncrement;
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
				trans[0] -= strafeIncrement;
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
				trans[2] -= dodgeIncrement;
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
				trans[2] += dodgeIncrement;
			}
			
			
		}
	}
	
}
