package game_engine;

/**
 * Class for storing player information
 * 
 * @author jotting
 */
public class Player
{
	public Info info;
	
	private Commands cmd;
	private int command = Commands.NA;

	/**
	 * Initializes the plaeyr with name as player_name,
	 * health at 100, position at 0, and blocking at false
	 * 
	 * @param player_name Name of the player
	 * @param commands The Commands object associated with player
	 * @param socket The socket to send screen information to
	 */
	public Player(Commands commands)
	{
		info = new Info();
		
		cmd = commands;
	}
	
	/**
	 * Returns the player's current health
	 * 
	 * @return The player's current health
	 */
	public int getHealth()
	{
		return info.health;
	}
	
	/**
	 * Returns the player's current position
	 * 
	 * @return The player's current position
	 */
	public int getPosition()
	{
		return info.position;
	}
	
	/**
	 * Returns where the sword is relative to the player
	 * or SWORD_RESTING if it isn't being swung
	 * 
	 * @return Sword position relative to player
	 */
	public double getSwordPosition()
	{
		return info.sword_position;
	}
	
	/**
	 * Returns the type of swing
	 * 
	 * @return The swing type or SWORD_RESTING
	 */
	public double getSwinging()
	{
		return info.swinging;
	}
	
	/**
	 * Returns if the player is dodging
	 * 
	 * @return Whether or not the player is dodging
	 */
	public boolean getDodging()
	{
		return info.dodging;
	}
	
	/** 
	 * Returns if the player is blocking
	 * 
	 * @return Whether or not the player is blocking
	 */
	public boolean getBlocking()
	{
		return info.blocking;
	}
	
	/**
	 * Returns the info object, mostly used for printing to console
	 * 
	 * @return The Info object
	 */
	public Info getInfo()
	{
		return info;
	}
	
	/**
	 * Updates the current command for the player with the next command
	 *
	 */
	public void grabNextCommand()
	{
		command = cmd.get_command();
	}
	
	/**
	 * Gets the current command for the player
	 * 
	 * @return The current command for the player
	 */
	public int getCommand()
	{
		return command;
	}
	
	/**
	 * Sets and returns the new position of the sword, 1 for to the right or above of player,
	 * 0 for in front of player, -1 for to the left or below player, otherwise the
	 * player isn't swinging, and SWORD_RESTING is returned
	 * 
	 * @return int The swing position
	 */
	public double continue_swing()
	{
		if (info.sword_position == Info.SWORD_RESTING)
			info.sword_position = 1;
		else if (info.sword_position == -1)
			info.sword_position = info.swinging = Info.SWORD_RESTING;
		else
			info.sword_position--;
		
		return info.sword_position;
	}

	/**
	 * Sets and returns the new position of the sword, 1 for to the right or above of player,
	 * 0 for in front of player, -1 for to the left or below player, otherwise the
	 * player isn't swinging, and SWORD_RESTING is returned
	 * 
	 * @param type The swing type
	 * @return int The swing position
	 */
	public double swing(double type)
	{
		info.swinging = type;
		
		return continue_swing();
	}
	
	/**
	 * Interrupts the swing action, this can happen if damaged or a clang
	 *
	 */
	public void swingInterrupt()
	{
		if (info.swinging != Info.SWORD_RESTING)
			info.stunned = true;
		
		info.sword_position = info.swinging = Info.SWORD_RESTING;
	}
	
	/**
	 * The player has been hit, decrement his health, if the
	 * player is blocking, halve the damage
	 * 
	 * @param damage The amount of health lost
	 * @return The current health of the player
	 */
	public int hit(int damage)
	{
		info.health -= info.blocking ? damage/2 : damage;
		
		return info.health;
	}
	
	/**
	 * Move the player to the right by one increment
	 * 
	 * @return The player's new position
	 */
	public int moveRight()
	{
		info.move = 1;
		return ++info.position;
	}
	
	public int stand()
	{
		info.move = 0;
		return 0;
	}
	
	/**
	 * Move the player left by one increment
	 * 
	 * @return The player's new position
	 */
	public int moveLeft()
	{
		info.move = -1;
		return --info.position;
	}
	
	/**
	 * Toggles the player's into a dodging position
	 * 
	 * @return Status of dodging
	 */
	public boolean dodge()
	{
		info.dodging = !info.dodging;
		return info.dodging;
	}
	
	/**
	 * Sets the player into a blocking position
	 * 
	 */
	public void block()
	{
		info.blocking = true;
	}
	
	/**
	 * Stops blocking
	 *
	 */
	public void stopBlocking()
	{
		info.blocking = false;
	}
	
	/**
	 * Returns whether or not the player is stunned, resets stunned
	 * 
	 * @return boolean Whether or not player is stunned
	 */
	public boolean getStunned()
	{
		if (info.stunned)
		{
			info.stunned = false;
			return true;
		}
		
		return info.stunned;
	}
}
