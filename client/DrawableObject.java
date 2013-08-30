package client;

import javax.media.opengl.*;

import com.sun.opengl.util.GLUT;

public abstract class DrawableObject {
	
	/////////////////////////////////
	// public class variables
	/////////////////////////////////
	
	public float[] scale = { 1.0f, 1.0f, 1.0f };
	public float[] trans = { 0.0f, 0.0f, 0.0f };
	public float[] rot =   { 0.0f, 0.0f, 0.0f };
	
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	public DrawableObject() {
		
	}

	
	/////////////////////////////////
	// public methods
	/////////////////////////////////
	
	public void draw(GL gl) {
		
		gl.glPushMatrix();
			gl.glTranslated(trans[0], trans[1], trans[2]);
			gl.glRotated(rot[0], 1, 0, 0);
			gl.glRotated(rot[1], 0, 1, 0);
			gl.glRotated(rot[2], 0, 0, 1);
			gl.glScalef(scale[0], scale[1], scale[2]);
			this.gldraw(gl);
		gl.glPopMatrix();
	}
	
	public abstract void gldraw(GL gl);
	
	public void axis(GL gl, double length) {
		
		GLUT glut = new GLUT();
		
		// draw a z-axis, with cone at end
		gl.glPushMatrix();
			gl.glBegin(GL.GL_LINES);
				gl.glVertex3d(0, 0, 0);
				gl.glVertex3d(0, 0, length);
			gl.glEnd();
			gl.glTranslated(0, 0, length - 0.2);
			glut.glutWireCone(0.04, 0.2, 12, 9);
		gl.glPopMatrix();
	}
	
	public void axes(GL gl, float size) {
		
		// draw world z-axis
		gl.glColor3d(0.0, 0.0, 1.0);
		this.axis(gl, size);
		
		// draw world y-axis
		gl.glPushMatrix();
			gl.glColor3d(0.0, 1.0, 0.0);
			gl.glRotated(-90, 1.0, 0.0, 0.0);
			this.axis(gl, size);
		gl.glPopMatrix();
		
		// draw world x-axis
		gl.glPushMatrix();
			gl.glColor3d(1.0, 0.0, 0.0);
			gl.glRotated(90, 0.0, 1.0, 0.0);
			this.axis(gl, size);
		gl.glPopMatrix();
	}
}
