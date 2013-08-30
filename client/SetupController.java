package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class SetupController implements ActionListener {
	
	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	private SetupWindow setupWindow;
	
	/////////////////////////////////
	// public methods
	/////////////////////////////////

	public void actionPerformed(ActionEvent event) {
		
		String actionCom = event.getActionCommand();
		
		if (actionCom.equals("exit")) {
			
			System.exit(0);
			
		} else if (actionCom.equals("connect")) {
			
			try {
				
				int fps = Integer.parseInt(setupWindow.fpsField.getText());
				int port = Integer.parseInt(setupWindow.portField.getText());
				String errorMessage = errorCheckInput(fps, port);
				
				if (errorMessage != "") {
					
					displayErrorMessage(errorMessage);
					
				} else {
					
					// TODO: connection with game server will need to be done here
					StateData stateData = StateData.getInstance();
					stateData.portNo = port;
					stateData.ipAddress = setupWindow.serverField.getText();
					
					// start thread for the client receiver socket
					(new Thread(new sockets.ClientReceiveSocket())).start();
					
					// start thread for the client sender socket
					(new Thread(new sockets.ClientSendSocket())).start();
					
					setupWindow.setVisible(false);
					
					GameController game = new GameController(fps);
					game.createAndShow();
					
					setupWindow.dispose();
				}
				
			} catch (NumberFormatException e) {
				
				displayErrorMessage("You must enter a value for all fields.");
			}

		
		}
	}
	
	/////////////////////////////////
	// protected methods
	/////////////////////////////////

	protected void createAndShow() {
		
		// schedule a a job on the event dispatching thread
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				setupWindow = new SetupWindow();
				setupWindow.createAndShow();
				setupObjects();
			}
		});
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////
	
	private void setupObjects() {
		
		setupWindow.connectButton.addActionListener(this);
		setupWindow.connectButton.setActionCommand("connect");
		
		setupWindow.exitButton.addActionListener(this);
		setupWindow.exitButton.setActionCommand("exit");
	}
	
	/**
	 * Checks that frame rate and port number are within acceptable range.
	 * 
	 * @return Returns empty string if no error occurred, returns error message
	 *         otherwise.
	 */
	private String errorCheckInput(int fps, int port) {
		
		String error = "";
		
		if ((fps < 1) || (fps > 100)) {
			error = "Framerate must be between 1 and 100 frames per second.";
		}
		
		if ((port < 1024) || (port > 65535)) {
			error = "Port numbers must be between 1024 and 65535.";
		}
		
		return error;
	}
	
	/**
	 * Displays a dialog box with an error icon and message.
	 * 
	 * @param errorMessage
	 *        The error message you wish to display.
	 */
	private void displayErrorMessage(String errorMessage) {
		
		JOptionPane.showMessageDialog(setupWindow,
			    errorMessage,
			    "Could Not Connect to Game Server",
			    JOptionPane.ERROR_MESSAGE);
	}
}
