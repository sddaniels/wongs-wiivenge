package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.media.opengl.GLCanvas;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.opengl.util.FPSAnimator;

@SuppressWarnings("serial")
public class GameWindow extends JFrame {

	
	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	private GLCallbacks listener;
	protected FPSAnimator animator;
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	public GameWindow(int fps) {
		
		// JFrame constructor
		super("Wong's Wiivenge");
		
		// create our listener object
		listener = new GLCallbacks(this);
		
		// set up the content pane of our frame
		Container cPane = this.getContentPane();
		cPane.setLayout( new BorderLayout() );
		cPane.add(makeRenderPanel(fps), BorderLayout.CENTER);
	}
	
	/////////////////////////////////
	// protected methods
	/////////////////////////////////
	
	protected void createAndShow() {
		
		// display the window
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		// start drawing with openGL
		animator.start();
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////
	
	private JPanel makeRenderPanel(int fps) {
		
		// specify the features we want, if necessary
		//GLCapabilities caps = new GLCapabilities();
		
		// create the openGL canvas
		GLCanvas canvas = new GLCanvas();       // comment out if passing in capabilities
		//GLCanvas canvas = new GLCanvas(caps); // uncomment if passing in capabilities
		
		// add the listener for openGL callbacks	
	    canvas.addGLEventListener(listener);
		
		// create the animator which will call our display callback at a fixed frame rate
		animator = new FPSAnimator(canvas, fps, true);
		
		// create the enclosing panel for the canvas
		JPanel renderPanel = new JPanel();
		renderPanel.setLayout( new BorderLayout() );
		renderPanel.setPreferredSize( new Dimension(800, 600) );
		renderPanel.add(canvas, BorderLayout.CENTER);
		
		return renderPanel;
	}
}
