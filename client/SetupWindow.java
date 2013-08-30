package client;

import javax.swing.*;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class SetupWindow extends JFrame {

	
	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	private JPanel mainPanel;
	
	protected JTextField serverField;
	protected JTextField portField;
	protected JTextField fpsField;
	
	protected JButton connectButton;
	protected JButton exitButton;
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	public SetupWindow() {
		
		// JFrame constructor
		super("Wong's Wiivenge Connection Options");
		
		mainPanel = new JPanel();
		mainPanel.add(Box.createRigidArea(new Dimension(10,0)));
		mainPanel.add(buildLeftPanels());
		mainPanel.add(Box.createRigidArea(new Dimension(10,0)));
		mainPanel.add(buildRightPanels());
		mainPanel.add(Box.createRigidArea(new Dimension(10,0)));
	}
	
	/////////////////////////////////
	// protected methods
	/////////////////////////////////
	
	protected void createAndShow() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.getRootPane().setDefaultButton(connectButton);
		
		setContentPane(mainPanel);
		
		// display the window
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	/////////////////////////////////
	// private methods
	/////////////////////////////////
	
	private JPanel buildRightPanels() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.add(buildServerPanel());
		panel.add(Box.createRigidArea(new Dimension(10,10)));
		panel.add(buildPortPanel());
		panel.add(Box.createRigidArea(new Dimension(10,10)));
		panel.add(buildFpsPanel());
		panel.add(Box.createRigidArea(new Dimension(10,10)));
		panel.add(buildButtonPanel());
		
		return panel;
	}
	
	private JPanel buildLeftPanels() {
		
		JPanel panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		java.net.URL imgURL = SetupWindow.class.getResource("temp_lightsaber.jpg");
		ImageIcon icon = new ImageIcon(imgURL, "");
		
		JLabel logoLabel = new JLabel(icon);
		panel.add(logoLabel);
		
		return panel;
	}
	
	private JPanel buildButtonPanel() {
		
		JPanel panel = new JPanel();
		
		exitButton = new JButton("Exit");
		connectButton = new JButton("Connect to Server");
		
		panel.add(exitButton);
		panel.add(connectButton);
		
		return panel;
	}
	
	private JPanel buildServerPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.add(new JLabel("Game Server Location:"));
		serverField = new JTextField();
		serverField.setText("127.0.0.1");
		panel.add(serverField);
		
		return panel;
	}
	
	private JPanel buildPortPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.add(new JLabel("Port Number:"));
		portField = new JTextField();
		portField.setText("5678");
		panel.add(portField);
		
		return panel;
	}
	
	private JPanel buildFpsPanel() {
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		panel.add(new JLabel("Frames Per Second:"));
		fpsField = new JTextField();
		fpsField.setText("60");
		panel.add(fpsField);
		
		return panel;
	}
	
}
