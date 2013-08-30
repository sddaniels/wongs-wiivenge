package client;

import java.io.IOException;
import java.net.URL;

import javax.media.opengl.GL;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.*;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class Lightsaber extends DrawableObject {
	
	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	public String color;
	public boolean isPlayerSaber = false;
	
	private Texture texture;
	
	private class SaberExtend {
		public final static int extendedLength = 25;
		public final static int extendIncrement = 1;
	}
	
	private class OppHorizSwing {	
		public final static float xRotStopAngle = 50;
		public final static float yRotStopAngle = -40;
		public final static float xIncrement = 10;
		public final static float yIncrement = -10;
	}
	
	private class PlayerHorizSwing {
		public final static float xRotStopAngle = -50;
		public final static float yRotStopAngle = 40;
		public final static float xIncrement = -10;
		public final static float yIncrement = 10;
	}
	
	private class OppVertSwing {	
		public final static float xRotStopAngle = 90;
		public final static float yRotStopAngle = -10;
		public final static float xIncrement = 10;
		public final static float yIncrement = -5;
	}
	
	private class PlayerVertSwing {
		public final static float xRotStopAngle = -90;
		public final static float yRotStopAngle = 10;
		public final static float xIncrement = -10;
		public final static float yIncrement = 5;
	}
	
	private class OppBlock {
		public final static float yRotStopAngle = -30;
		public final static float yIncrement = -10;
		public final static long waitTime = 250;      // milliseconds
	}
	
	private class PlayerBlock {
		public final static float yRotStopAngle = 30;
		public final static float yIncrement = 10;
		public final static long waitTime = 250;      // milliseconds
	}
	
	// saber extend variables
	private boolean extending = true;
	private int waitCounter = 0;
	private double currentLength = 0;
	
	// horizontal swing variables
	private boolean horizSwingDown = false;
	private boolean horizSwingSlash = false;
	private boolean horizSwingReturn = false;
	private boolean horizSwingUp = false;
	
	// vertical swing variables
	private boolean vertSwingDown = false;
	private boolean vertSwingSlash = false;
	private boolean vertSwingStay = false;
	private boolean vertSwingReturn = false;
	private boolean vertSwingUp = false;
	
	// blocking swing variables
	private boolean blockStart = false;
	private boolean blockStay = false;
	private boolean blockReturn = false;
	private long blockStartTime;
	
	// swing tracking variables
	private float xRotOriginal;
	private float xRotCurrent = 0;
	private float yRotOriginal;
	private float yRotCurrent = 0;
	private float yTransOriginal;

	
	/////////////////////////////////
	// public methods
	/////////////////////////////////

	@Override
	public void gldraw(GL gl) {
		
		// find the saber's length
		if (extending) {
			
			if (currentLength == 0) {
				
				if (waitCounter < 50) {
					waitCounter++;
				} else {
					SoundEffects.playSoundEffect("data/start.wav");
					currentLength += SaberExtend.extendIncrement;
				}
				
			} else if (currentLength == SaberExtend.extendedLength) {
				
				extending = false;
				if (isPlayerSaber) {
					SoundEffects.playSoundEffect("data/ambient-saber.wav");
					SoundEffects.playDelayedSoundEffect("data/destroy.wav", 800);
				}
				
			} else {
				currentLength += SaberExtend.extendIncrement;
			}
		}
		
		GLU glu = new GLU();
		GLUquadric handle = glu.gluNewQuadric();
		glu.gluQuadricNormals(handle, GLU.GLU_SMOOTH);
		glu.gluQuadricTexture(handle, true);
		
		GLUquadric inner = glu.gluNewQuadric();
		GLUquadric outer = glu.gluNewQuadric();
		GLUquadric top = glu.gluNewQuadric();
		
		// set correct material properties for handle
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE, MatConsts.mat_diffuse1, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, MatConsts.mat_shininess2, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, MatConsts.mat_specular2, 0);
		
		// draw the handle so it's cossing the axihs
		gl.glPushMatrix();
		gl.glTranslated(0, 0, -0.03);
		
		// draw the handle
		setGray(gl);
		texture.bind();
		glu.gluCylinder(handle, 0.007f, 0.007f ,0.07f ,32, 32);
		
		// disable textures and lighting
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_LIGHTING);
		
		// draw the beam in the negative z axis
		gl.glPushMatrix();
		gl.glTranslated(0, 0, -(currentLength/100));
		
		// draw the inner cylinder
		setWhite(gl);
		glu.gluCylinder(inner, 0.003f, 0.004f, currentLength/100, 32, 1);
		glu.gluDisk(top, 0, 0.003, 32, 1);
		
		// enable blending for transparency
	    gl.glEnable(GL.GL_BLEND); 
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		
	    // draw the semi transparent outer cylinder
		if (color.equals("red")) {
			setTransparentRed(gl);
		} else {
			setTransparentGreen(gl);
		}
		glu.gluCylinder(outer, 0.004f, 0.006f, currentLength/100, 32, 1);
		glu.gluDisk(top, 0, 0.004, 32, 1);
		
		setWhite(gl);
		
		// reset the modelview matrix
		gl.glPopMatrix();
		gl.glPopMatrix();
		
		// disable transparency
		gl.glDisable(GL.GL_BLEND);
		
		// reenable the good stuff
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_LIGHTING);
		
		// if a swing is in progress, update the position of the lightsaber
		if (isPlayerSaber) {
			processPlayerHorizSwing();
			processPlayerVertSwing();
			processPlayerBlock();
		} else {
			processOpponentHorizSwing();
			processOpponentVertSwing();
			processOpponentBlock();
		}
		
	}
	
	public void loadTextures(GL gl) throws GLException, IOException {
			
		String filename = null;
		
		if (isPlayerSaber) {
			filename = "data/luke-saber.bmp";
		} else {
			filename = "data/vader-saber.bmp";
		}
		
		URL textureURL = getClass().getResource(filename);
		texture = TextureIO.newTexture(textureURL, false, ".bmp");
	}
	
	public void startHorizontalSwing() {
		
		// don't start another swing if one is in progress
		if (horizontalSwingInProg() || verticalSwingInProg() || blockInProg())
			return;
		
		SoundEffects.playSoundEffect("data/horiz-swing.wav");
		horizSwingDown = true;
		
		// save the original rotation values
		xRotOriginal = rot[0];
		yRotOriginal = rot[1];
	}
	
	public void startVerticalSwing() {
		
		// don't start another swing if one is in progress
		if (horizontalSwingInProg() || verticalSwingInProg() || blockInProg())
			return;
		
		SoundEffects.playSoundEffect("data/vert-swing.wav");
		vertSwingDown = true;
		vertSwingSlash = true;
		
		// save the original rotation values
		xRotOriginal = rot[0];
		yRotOriginal = rot[1];
		yTransOriginal = trans[1];
	}
	
	public void startBlock() {
		
		// don't start another swing if one is in progress
		if (horizontalSwingInProg() || verticalSwingInProg() || blockInProg())
			return;
		
		SoundEffects.playSoundEffect("data/block.wav");
		blockStart = true;
		
		yRotOriginal = rot[1];
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////

	private void setTransparentGreen(GL gl) {
		gl.glColor4d(0.6784, 1, 0.1843, 0.2);
	}
	
	private void setTransparentRed(GL gl) {
		gl.glColor4d(1, 0, 0, 0.2);
	}
	
	private void setWhite(GL gl) {
		gl.glColor3d(1, 1, 1);
	}
	
	private void setGray(GL gl) {
		gl.glColor3d(0.7, 0.7, 0.7);
	}
	
	private boolean horizontalSwingInProg() {
		
		return (horizSwingDown || horizSwingSlash || horizSwingReturn || horizSwingUp);
	}
	
	private boolean verticalSwingInProg() {
		
		return (vertSwingDown || vertSwingSlash || vertSwingReturn || vertSwingUp);
	}
	
	private boolean blockInProg() {
		return (blockStart || blockStay || blockReturn);
	}
	
	private void processOpponentHorizSwing() {	
		
		// if a swing is in progress, update the position of the lightsaber
		if (horizSwingDown) {
			
			// if stop angle reached, start the slash
			if (xRotCurrent == OppHorizSwing.xRotStopAngle ) {
				horizSwingDown = false;
			// otherwise, continue the swing
			} else {
				if (xRotCurrent >= (.8 * OppHorizSwing.xRotStopAngle)) {
					horizSwingSlash = true;
				}
				xRotCurrent += OppHorizSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
				
		}  else if (horizSwingUp) {
			
			// if the current angle is zero, we've finished the swing
			if (xRotCurrent == 0) {
				horizSwingUp = false;
			// otherwise, continue to retract the swing
			} else {
				xRotCurrent -= OppHorizSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
		}
		
		if (horizSwingSlash) {
			
			// if stop angle reached, start the return
			if (yRotCurrent == OppHorizSwing.yRotStopAngle) {
				horizSwingSlash = false;
				horizSwingReturn = true;
			// otherwise, continue the slash
			}  else {
				yRotCurrent += OppHorizSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] += 0.03f;
			}
			
		} else if (horizSwingReturn) {
			
			// if the current angle is zero, we've finished the return
			if (yRotCurrent == 0) {
				horizSwingReturn = false;
			// otherwise, continue the return
			} else {
				
				if (yRotCurrent <= (.2 * OppHorizSwing.yRotStopAngle)) {
					horizSwingUp = true;
				}
				yRotCurrent -= OppHorizSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] -= 0.03f;
			}
			
		}
		
	} // end of method
	
	private void processPlayerHorizSwing() {	
		
		// if a swing is in progress, update the position of the lightsaber
		if (horizSwingDown) {
			
			// if stop angle reached, start the slash
			if (xRotCurrent == PlayerHorizSwing.xRotStopAngle ) {
				horizSwingDown = false;
			// otherwise, continue the swing
			} else {
				if (xRotCurrent <= (.8 * PlayerHorizSwing.xRotStopAngle)) {
					horizSwingSlash = true;
				}
				xRotCurrent += PlayerHorizSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
				
		}  else if (horizSwingUp) {
			
			// if the current angle is zero, we've finished the swing
			if (xRotCurrent == 0) {
				horizSwingUp = false;
			// otherwise, continue to retract the swing
			} else {
				xRotCurrent -= PlayerHorizSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
		}
		
		if (horizSwingSlash) {
			
			// if stop angle reached, start the return
			if (yRotCurrent == PlayerHorizSwing.yRotStopAngle) {
				horizSwingSlash = false;
				horizSwingReturn = true;
			// otherwise, continue the slash
			}  else {
				yRotCurrent += PlayerHorizSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] -= 0.03f;
			}
			
		} else if (horizSwingReturn) {
			
			// if the current angle is zero, we've finished the return
			if (yRotCurrent == 0) {
				horizSwingReturn = false;
			// otherwise, continue the return
			} else {
				
				if (yRotCurrent >= (.2 * PlayerHorizSwing.yRotStopAngle)) {
					horizSwingUp = true;
				}
				yRotCurrent -= PlayerHorizSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] += 0.03f;
			}
			
		}
		
	} // end of method
	
	private void processOpponentVertSwing() {	
		
		// if a swing is in progress, update the position of the lightsaber
		if (vertSwingDown) {
			
			// if stop angle reached, start the slash
			if (xRotCurrent == OppVertSwing.xRotStopAngle ) {
				vertSwingDown = false;
				vertSwingUp = true;
			// otherwise, continue the swing
			} else {
				xRotCurrent += OppVertSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
				
		}  else if (vertSwingUp) {
			
			// if the current angle is zero, we've finished the swing
			if (xRotCurrent == 0) {
				vertSwingUp = false;
			// otherwise, continue to retract the swing
			} else {
				xRotCurrent -= OppVertSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
		}
		
		if (vertSwingSlash) {
			
			// if stop angle reached, start the return
			if (yRotCurrent == OppVertSwing.yRotStopAngle) {
				vertSwingSlash = false;
				vertSwingStay = true;
			// otherwise, continue the slash
			}  else {
				yRotCurrent += OppVertSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] += 0.025f;
				trans[1] += 0.06f;
			}
			
		} else if (vertSwingStay) {
			
			// check the down angle
			if (xRotCurrent >= (0.7 * OppVertSwing.xRotStopAngle)) {
				vertSwingStay = false;
				vertSwingReturn = true;
			}  else {
				trans[1] -= 0.06f;
			}
			
		} else if (vertSwingReturn) {
		
			
			// if the current angle is zero, we've finished the return
			if (yRotCurrent == 0) {
				vertSwingReturn = false;
			// otherwise, continue the return
			} else {
				yRotCurrent -= OppVertSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] -= 0.025f;
				
				if (trans[1] != yTransOriginal) {
					trans[1] += 0.06f;
				}
			}
			
		}
	} // end of method
	
	private void processPlayerVertSwing() {	
		
		// if a swing is in progress, update the position of the lightsaber
		if (vertSwingDown) {
			
			// if stop angle reached, start the slash
			if (xRotCurrent == PlayerVertSwing.xRotStopAngle ) {
				vertSwingDown = false;
				vertSwingUp = true;
			// otherwise, continue the swing
			} else {
				xRotCurrent += PlayerVertSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
				
		}  else if (vertSwingUp) {
			
			// if the current angle is zero, we've finished the swing
			if (xRotCurrent == 0) {
				vertSwingUp = false;
			// otherwise, continue to retract the swing
			} else {
				xRotCurrent -= PlayerVertSwing.xIncrement;
				rot[0] = xRotOriginal + xRotCurrent;
			}
		}
		
		if (vertSwingSlash) {
			
			// if stop angle reached, start the return
			if (yRotCurrent == PlayerVertSwing.yRotStopAngle) {
				vertSwingSlash = false;
				vertSwingStay = true;
			// otherwise, continue the slash
			}  else {
				yRotCurrent += PlayerVertSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] -= 0.025f;
				trans[1] += 0.06f;
			}
			
		} else if (vertSwingStay) {
			
			// check the down angle
			if (xRotCurrent <= (0.7 * PlayerVertSwing.xRotStopAngle)) {
				vertSwingStay = false;
				vertSwingReturn = true;
			}  else {
				trans[1] -= 0.06f;
			}
			
		} else if (vertSwingReturn) {
		
			
			// if the current angle is zero, we've finished the return
			if (yRotCurrent == 0) {
				vertSwingReturn = false;
			// otherwise, continue the return
			} else {
				yRotCurrent -= PlayerVertSwing.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[0] += 0.025f;
				
				// make sure lightsaber returns to original height
				if (trans[1] != yTransOriginal) {
					trans[1] += 0.06f;
				}
			}
			
		}
	} // end of method
	
	private void processOpponentBlock() {
		
		if (blockStart) {
			
			// if stop angle is reached, stay in place for a while
			if (yRotCurrent == OppBlock.yRotStopAngle) {
				blockStart = false;
				blockStay = true;
				blockStartTime = System.currentTimeMillis();
			} else {
				yRotCurrent += OppBlock.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[1] -= 0.01f;
				trans[2] -= 0.01f;
			}
			
		} else if (blockStay) {
			
			if ((System.currentTimeMillis() - blockStartTime) >= OppBlock.waitTime) {
				blockStay = false;
				blockReturn = true;
			}
			
		} else if (blockReturn) {
			
			// if the current angle is zero, we've finished the return
			if (yRotCurrent == 0) {
				blockReturn = false;
			} else {
				yRotCurrent -= OppBlock.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[1] += 0.01f;
				trans[2] += 0.01f;
			}
			
		}
	}
	
	private void processPlayerBlock() {
		
		if (blockStart) {
			
			// if stop angle is reached, stay in place for a while
			if (yRotCurrent == PlayerBlock.yRotStopAngle) {
				blockStart = false;
				blockStay = true;
				blockStartTime = System.currentTimeMillis();
			} else {
				yRotCurrent += PlayerBlock.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[1] -= 0.01f;
				trans[2] += 0.01f;
			}
			
		} else if (blockStay) {
			
			if ((System.currentTimeMillis() - blockStartTime) >= PlayerBlock.waitTime) {
				blockStay = false;
				blockReturn = true;
			}
			
		} else if (blockReturn) {
			
			// if the current angle is zero, we've finished the return
			if (yRotCurrent == 0) {
				blockReturn = false;
			} else {
				yRotCurrent -= PlayerBlock.yIncrement;
				rot[1] = yRotOriginal + yRotCurrent;
				trans[1] += 0.01f;
				trans[2] -= 0.01f;
			}
			
		}
	}
	
}
