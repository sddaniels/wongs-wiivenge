package client;

import java.io.IOException;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.GLUT;

public class GLCallbacks implements GLEventListener {
	
	/////////////////////////////////
	// private class variables
	/////////////////////////////////
	
	// the client we're doing the drawing for
	private GameWindow owner;
	
	// OpenGL Utility Library
	private GLU glu;
	
	// world data
	private PolyModel world;
	private PolyModel floor;
	private CameraMover camMover;
	
	// player data
	private Lightsaber lightsaber;
	private DarthVader darthVader;
	
	// perspective data
	private double fovy = 45.0;  // field of view
	private double aspect;       // aspect ration
	private double zNear = 0.1;  // near clipping plane
	private double zFar = 100;   // far clipping plane
	
	// clipping plane equation
	private double clipEq[] = { 0, 0, 0, 0 };
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	/**
	 * Constructor for the class.
	 * 
	 * @param sc The client we're doing the drawing for.
	 */
	public GLCallbacks(GameWindow sc) {
		owner = sc;
		
		// read the world file
		world = new PolyModel();
		world.loadModel("data/world.txt");
		
		// read the floor file
		floor = new PolyModel();
		floor.loadModel("data/floor.txt");
		
		// read player data
		lightsaber = new Lightsaber();
		lightsaber.color = "green";
		lightsaber.isPlayerSaber = true;
		
		lightsaber.rot[0] = 60;
		lightsaber.rot[1] = -10;
		
		lightsaber.trans[0] = 0.05f;
		lightsaber.trans[1] = 0.34f;
		lightsaber.trans[2] = -0.2f;
		
		darthVader = new DarthVader();
		darthVader.trans[2] = -0.7f;

		camMover = new CameraMover();
	}

	/////////////////////////////////
	// openGL callback methods
	/////////////////////////////////

	/**
	 * Called whenever openGL needs to draw the screen.
	 * 
	 * @param drawable
	 * 		The drawable that we get the gl object from.
	 */
	public void display(GLAutoDrawable drawable) {
		
		final StateData stateData = StateData.getInstance();
		
		GL gl = drawable.getGL();
		
		// clear the buffers
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		
		// switch to the modelview matrix
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glColor3d(1, 1, 1);
		
		// move the camera if necessary
		camMover.moveCamera();
		
        float xTrans = stateData.camPos[0];
        float yTrans = stateData.camPos[1];
        float zTrans = stateData.camPos[2];
        float sceneroty = 360.0f;
        
        gl.glPushMatrix();
        gl.glRotatef(0, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(sceneroty, 0.0f, 1.0f, 0.0f);
        gl.glTranslatef(xTrans, yTrans, zTrans);
		
		// material properties
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE, MatConsts.mat_diffuse2, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, MatConsts.mat_shininess1, 0);
		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, MatConsts.mat_specular1, 0);
		
		// enable stencil buffer
		gl.glColorMask(false, false, false, false);
		gl.glEnable(GL.GL_STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);

		// mask out the floor
		gl.glDisable(GL.GL_DEPTH_TEST);
		floor.draw(gl);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		// set up for drawing reflection
		gl.glColorMask(true, true, true, true);
		gl.glStencilFunc(GL.GL_EQUAL, 1, 1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);
		gl.glEnable(GL.GL_CLIP_PLANE0);
		gl.glClipPlane(GL.GL_CLIP_PLANE0, clipEq, 0);
		
		// draw reflection
		gl.glPushMatrix();
		
			gl.glScaled(1, -1, 1);
			setLightPositions(gl);
			world.draw(gl);
		
		gl.glPopMatrix();
		
		// disable the clipping plane and the stencil test
		gl.glDisable(GL.GL_CLIP_PLANE0);
		gl.glDisable(GL.GL_STENCIL_TEST);
		
        setLightPositions(gl);
		
        // blend the floor with the reflection
        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.9f);
        gl.glDisable(GL.GL_DEPTH_TEST);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        floor.draw(gl);
        gl.glDisable(GL.GL_BLEND);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glColor3d(1, 1, 1);
		
        world.draw(gl);
        darthVader.draw(gl);
        gl.glPopMatrix();
        
        // check whether a swing trigger was activated
        boolean[] triggers = stateData.getSwingTriggers();
        if (triggers[0]) lightsaber.startBlock();
        if (triggers[1]) lightsaber.startHorizontalSwing();
        if (triggers[2]) lightsaber.startVerticalSwing();
        
        // draw the player's lightsaber so it stays with the camera
        gl.glTranslatef(-0, -0.43f, -0);
		lightsaber.draw(gl);
		
		// draw the score overlay
		this.drawOverlay(gl);
		
	}

	/**
	 * Called when the display mode is changed. This can be ignored because
	 * it has not been implemented yet.
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			  					boolean deviceChanged) {

		// these aren't the droids you're looking for
	}

	/**
	 * Called immediately after the openGL context is intialized.
	 * Can be used to perform start up tasks.
	 * 
	 * @param drawable
	 * 		The drawable that we get the gl object from.
	 */
	public void init(GLAutoDrawable drawable) {

		GL gl = drawable.getGL(); // don't ever make the gl a global
		glu = new GLU();          // ok as global, but only use in callbacks
		
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glClearColor(0, 0, 0, 0.5f);
		gl.glClearDepth(1);
		gl.glClearStencil(0);
		
		// lighting initialization
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, LightingConsts.white_light, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, LightingConsts.white_light, 0);
		gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_CUTOFF, 90f);
		gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_EXPONENT, 128f);
		
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, LightingConsts.white_light, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, LightingConsts.white_light, 0);
		
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, LightingConsts.white_light, 0);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR, LightingConsts.white_light, 0);
		
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, LightingConsts.lmodel_ambient, 0);
		
		// enable lighting, depth test, and texure mapping
		gl.glEnable(GL.GL_NORMALIZE);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_LIGHT2);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glEnable(GL.GL_TEXTURE_2D);
		
		// load me some textures
		this.texturesHelper(gl);
		
	}

	/**
	 * Called whenever the window is resized. gl.glViewport(x, y, width, height) is
	 * automatically called, so it is not necessary to call it again in this method.
	 * 
	 * @param drawable
	 * 		The drawable that we get the gl object from.
	 * @param x
	 * 		The x coord. of the viewport rectangle.
	 * @param y
	 * 		the y coord. of the viewport rectangle.
	 * @param width
	 * 		The new width of the window.
	 * @param height
	 * 		The new height of the window.
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		GL gl = drawable.getGL();
		
		// calculate the aspect ratio
		aspect = (double)width / (double)height;
		
		// set the projection
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(fovy, aspect, zNear, zFar);
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////
	
	private void texturesHelper(GL gl) {
		
		try {
		
			// texture settings
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
			
			// get all textures
			world.loadTextures(gl);
			floor.loadTextures(gl);
			lightsaber.loadTextures(gl);
			darthVader.loadTextures(gl);
			
		} catch (GLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void drawOverlay(GL gl) {
		
		// calculate length of health bar
		StateData stateData = StateData.getInstance();
		double playerHealthUp = stateData.getPlayerHealth();
		double enemyHealthUp = stateData.getEnemyHealth();
		
		if (playerHealthUp < 0) {
			playerHealthUp = 0;
		}
		
		if (enemyHealthUp < 0) {
			enemyHealthUp = 0;
		}
		
		double playerLength = 1 + (40.0 * playerHealthUp);
		double enemyLength = 99 - (40.0 * enemyHealthUp);
		
		boolean playerDanger = playerLength < 11;
		boolean enemyDanger = enemyLength > 89;
		
		// set up orthographic view and draw score overlay
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0, 100, 0, 100);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		// disable stuff that will cause problems
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glDisable(GL.GL_LIGHTING);
		
		// enable blending for transparency
	    gl.glEnable(GL.GL_BLEND); 
	    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);  
		
		gl.glBegin(GL.GL_QUADS);
		
			// player health
			if (playerDanger) this.setLightRed(gl); // top
			else this.setLightGreen(gl);
			gl.glVertex2d(1, 99);
			gl.glVertex2d(playerLength, 99);
			if (playerDanger) this.setDarkRed(gl);  // bottom
			else this.setDarkGreen(gl);
			gl.glVertex2d(playerLength, 93);
			gl.glVertex2d(1, 93);
	
			// empty bar
			this.setLightGray(gl);             // top
			gl.glVertex2d(playerLength, 99);
			gl.glVertex2d(41, 99);
			this.setDarkGray(gl);              // bottom
			gl.glVertex2d(41, 93);
			gl.glVertex2d(playerLength, 93);
			
			// enemy health
			if (enemyDanger) this.setLightRed(gl); // top
			else this.setLightGreen(gl);
			gl.glVertex2d(enemyLength, 99);
			gl.glVertex2d(99, 99);
			if (enemyDanger) this.setDarkRed(gl);  // bottom
			else this.setDarkGreen(gl);
			gl.glVertex2d(99, 93);
			gl.glVertex2d(enemyLength, 93);
			
			// empty bar
			this.setLightGray(gl);             // top
			gl.glVertex2d(59, 99);
			gl.glVertex2d(enemyLength, 99);
			this.setDarkGray(gl);              // bottom
			gl.glVertex2d(enemyLength, 93);
			gl.glVertex2d(59, 93);
		
		gl.glEnd();
		
		// draw some text
		if (stateData.getGameOverStatus()) {
			
			if (stateData.getPlayerHealth() <= 0) {
				this.drawText(gl, 45, 90, "You Lost!");
				this.drawText(gl, 27, 87, "Don't underestimate the power of the dark side.");
			} else {
				this.drawText(gl, 45, 90, "You Won!");
				this.drawText(gl, 37, 87, "May the force be with you.");
			}
		}
		
		
		this.drawText(gl, 1, 90, "You");
		this.drawText(gl, 86, 90, "Darth Wong");
		
		// disable transparency
		gl.glDisable(GL.GL_BLEND);
		
		// reenable the good stuff
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_LIGHTING);
		
		// return the projection
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(fovy, aspect, zNear, zFar);
	}

	private void setLightGreen(GL gl) {
		gl.glColor4d(0.471, 0.745, 0.0, 0.8);
	}
	
	private void setDarkGreen(GL gl) {
		gl.glColor4d(0.071, 0.345, 0.0, 0.8);
	}
	
	private void setLightRed(GL gl) {
		gl.glColor4d(0.93, 0.0, 0.0, 0.8);
	}
	
	private void setDarkRed(GL gl) {
		gl.glColor4d(0.43, 0.0, 0.0, 0.8);
	}
	
	private void setLightGray(GL gl) {
		gl.glColor4d(0.4, 0.4, 0.4, 0.5);
	}
	
	private void setDarkGray(GL gl) {
		gl.glColor4d(0.2, 0.2, 0.2, 0.5);
	}
	
	private void drawText(GL gl, float x, float y, String text) {
		
		GLUT glut = new GLUT();
		
		// "shadow"
		gl.glColor4d(0, 0, 0, 0.8);
		gl.glRasterPos2f(x - 0.2f, y - 0.2f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, text);
		
		// text
		gl.glColor4d(1, 1, 1, 0.8);
		gl.glRasterPos2f(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, text);
	}
	
	private void setLightPositions(GL gl) {
		
        // position the stationary light sources
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, LightingConsts.light_position0, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, LightingConsts.light_spotDirec0, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, LightingConsts.light_position1, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPOT_DIRECTION, LightingConsts.light_spotDirec1, 0);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, LightingConsts.light_position2, 0);
		gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPOT_DIRECTION, LightingConsts.light_spotDirec2, 0);
	}

}
