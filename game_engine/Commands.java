package game_engine;

import java.awt.event.KeyEvent;
import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

public class Commands {

	//Size of Client Commands (in bytes)
	public static final int SIZE = 4;
	
	//General Movements
	public static final int STRAFE_LEFT = KeyEvent.VK_LEFT;
	public static final int STRAFE_RIGHT = KeyEvent.VK_RIGHT;
	public static final int DODGE = KeyEvent.VK_DOWN;
	
	//Sword Maneuvers
	public static final int VERT_SWING = KeyEvent.VK_D;
	public static final int HORIZ_SWING = KeyEvent.VK_A;
	public static final int BLOCK = KeyEvent.VK_SPACE;
	
	//Misc
	public static final int NA = -1;
	public static final int CLANG = -2; // Not used in client, only graphics
	public static final int DAMAGE = -3; // Not used in client, only graphics
	
	//State booleans
	private static boolean strafeLeftEnabled = false;
	private static boolean strafeRightEnabled = false;
	private static boolean blockEnabled = false;
	
	//Semaphores for Engine
	private boolean new_command;
	private Mutex cmd_use;
	private int cmd;
	
	/**
	 * Creates a command handler
	 *
	 */
	public Commands()
	{
		new_command = false;
		cmd_use = new Mutex();
		
		cmd = -1;
	}
	
	/**
	 * Changes the current command
	 * 
	 * @param command The command that should be performed
	 */
	public void add_command(int command)
	{
		try {
			cmd_use.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cmd = isValidCommand(command) ? command : NA;

		new_command = true;
		
		cmd_use.release();
	}
	
	/**
	 * Gets the current command
	 * 
	 * @return The current command
	 */
	public int get_command()
	{
		try {
			cmd_use.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// If no new_command then send NA
		int command = new_command ? cmd : NA;
		new_command = false;
		
		cmd_use.release();
		
		return command;
	}
	
	/**
	 * Checks if the command is valid
	 * 
	 * @param command The command to be performed
	 * @return true if valid, else false
	 */
	public static boolean isValidCommand(int command)
	{
		return (command == STRAFE_LEFT ||
				command == STRAFE_RIGHT ||
				command == DODGE ||
				command == VERT_SWING ||
				command == HORIZ_SWING ||
				command == BLOCK ||
				command == NA);
	}

	/**
	 * Checks if the command is a state-based key command
	 * 
	 * @requires command key pressed on keyboard is being released
	 * @param command The command to be performed
	 * @return true if the command is a state-based command
	 */
	public static boolean isStateCommand(int command) {
		return (command == STRAFE_LEFT ||
				command == STRAFE_RIGHT ||
				command == BLOCK);
	}

	/**
	 * 
	 * @requires isStateCommand(command)==true
	 * @param command The command who's state we are checking
	 * @return whether the state of the command is enabled (yes->true, no->false)
	 */
	public static boolean commandEnabled(int command) 
	{
		if (command == STRAFE_LEFT)
		{
			return strafeLeftEnabled;
		}
		else if(command == STRAFE_RIGHT)
		{
			return strafeRightEnabled;
		}
		else //block
		{
			return blockEnabled;
		}
	}
	
	/**
	 * 
	 * @requires isStateCommand(command)==true
	 * @param command The command who's state we are setting
	 */
	public static void setEnabled(int command, boolean enabled)
	{
		if (command == STRAFE_LEFT)
		{
			strafeLeftEnabled = enabled;
		}
		else if(command == STRAFE_RIGHT)
		{
			strafeRightEnabled = enabled;
		}
		else //block
		{
			blockEnabled = enabled;
		}
	}
	
}
