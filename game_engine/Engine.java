package game_engine;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

public class Engine implements Runnable
{
	private Player[] players;
	private int world_size;
	
	private static final int HORIZ_DMG = 5;
	private static final int VERT_DMG = 10;
	
	private boolean infoUpdate = false;
	
	private boolean debug = true;
	
	private Thread t;
	
	private static final long FRAME_WAIT = 1000/60;
	
	private boolean finish = false;
	
	/**
	 * Setup the Engine
	 * 
	 * @param p1_cmd Player 1's command handler
	 * @param p2_cmd Player 2's command handler
	 * @param world_radius How big the world is to the left and right of center
	 */
	public Engine(Commands p1_cmd, Commands p2_cmd, int world_radius)
	{
		t = new Thread(this);
		
		// Setup players
		players = new Player[2];
		
		players[0] = new Player(p1_cmd);
		players[1] = new Player(p2_cmd);
		
		world_size = world_radius;
	}
	
	/**
	 * Begin the game
	 *
	 */
	public void begin()
	{
		try {
			Synch.getInstance().engine.acquire(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Start");
		t.start();
	}
	
	/**
	 * Used by the thread to run the program
	 * 
	 */
	public void run()
	{
		gameloop();
	}
	
	/**
	 * Gets all the screen update info
	 * 
	 * @return Either the latest screenupdateinfo or null if no update
	 */
	public boolean getInfo()
	{
		boolean temp = infoUpdate;
		infoUpdate = false;
		
		return temp;
	}
	
	/**
	 * Gets whether the engine is finished or not
	 * 
	 * @return true if finished, else false
	 */
	public boolean getFinish()
	{
		return finish;
	}
	
	/**
	 * The main loop of the game engine
	 *
	 */
	private void gameloop()
	{
		Synch.getInstance().engine.release(2);
		for (int i = 0; i < 2; i++)
		{
			Synch.getInstance().serverReceive[i].release();
			Synch.getInstance().serverSend[i].release();
		}
		
		do
		{
			for (int i=0; i < 2; ++i)
			{
				players[i].grabNextCommand();
				parseInstructions(players[i]);
			}
			
			// Update the ScreenUpdateInfo object
			updateInfo(players[0].getInfo(), players[1].getInfo(),
					   parseSwing(players[0], players[1]));
			
			// Draw the information to console if we're debugging
			if (debug)
				drawConsole(ScreenUpdateInfo.getInstance().clang);
			
			/*try {
				Thread.sleep(FRAME_WAIT);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		while (players[0].getHealth() > 0 && players[1].getHealth() > 0);
		
		// Send winning / losing screens
		finish = true;
	}
	
	/**
	 * Updates the ScreenUpdateInfo object
	 * 
	 * @param p1Info Player 1's Info object
	 * @param p2Info Player 2's Info object
	 * @param clang The type of sword strike
	 */
	private void updateInfo(Info p1Info, Info p2Info, int clang)
	{
		try {
			Synch.getInstance().engine.acquire(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Prepare info to send
		ScreenUpdateInfo.getInstance().updateInfo(p1Info, p2Info, clang);
		if (ScreenUpdateInfo.getInstance().updated())
		{
			infoUpdate = true;
		}
		
		for (int i = 0; i < 2; i++)
		{
			Synch.getInstance().serverReceive[i].release();
			Synch.getInstance().serverSend[i].release();
		}
	}
	
	/**
	 * Parses the instruction and does the required action
	 * 
	 * @param player The player who's instructions are parsed
	 */
	private void parseInstructions(Player player)
	{
		// Check if the player is in a swing animation and increment
		if(player.getSwinging() != Info.SWORD_RESTING)
		{
			player.continue_swing();
			return;
		}
		
		if (player.getDodging())
		{
			player.dodge();
			return;
		}
		
		if (player.getStunned())
		{
			return;
		}
		
		player.stand();
		player.stopBlocking();
		
		// Perform instructions
		switch(player.getCommand())
		{
		case Commands.STRAFE_LEFT:
			if (player.getPosition() > -world_size)
				player.moveLeft();
			return;
			
		case Commands.STRAFE_RIGHT:
			if (player.getPosition() < world_size)
				player.moveRight();
			return;
			
		case Commands.DODGE:
			player.dodge();
			return;
			
		case Commands.BLOCK:
			player.block();
			return;
			
		case Commands.HORIZ_SWING:
			player.swing(Info.HORIZONTAL);
			return;
		
		case Commands.VERT_SWING:
			player.swing(Info.VERTICAL);
			return;
		}
	}
	
	/**
	 * Parses the swings and determines what happens
	 * 
	 * @param p1 Player 1
	 * @param p2 Player 2
	 * @return -1 - no clang
	 *		    0 - p1 horizontal & p2 horizontal
	 * 			1 - p1 vertical & p2 horizontal
	 * 			2 - p1 horizontal & p2 vertical
	 * 			3 - p1 vertical & p2 vertical
	 */
	private int parseSwing(Player p1, Player p2)
	{
		// Get each player's sword position in the game world
		double pos1 =   p1.getSwordPosition() + (p1.getSwinging() == Info.HORIZONTAL ? p1.getPosition() : 0);
		double pos2 = -(p2.getSwordPosition() + (p2.getSwinging() == Info.HORIZONTAL ? p2.getPosition() : 0));
		
		if (pos1 != Info.SWORD_RESTING || pos2 != Info.SWORD_RESTING)
		{
		
			// If that position is the same as another swing
			// The swords clang and we interrupt the swing
			if (pos1 == pos2 && p1.getSwinging() != Info.SWORD_RESTING && p2.getSwinging() != Info.SWORD_RESTING)
			{
				p1.swingInterrupt();
				p2.swingInterrupt();
				return clang(p1.getSwinging(), p2.getSwinging());
			}
			
			// If that position is the same as an opponent
			// Deal damage, interrupt opponent's swing
			// If opponent was blocking, interrupt swinger's swing
			checkSwing(p1, p2, -p2.getPosition());			
			checkSwing(p2, p1, -p1.getPosition());
		}
		
		return -1;
	}
	
	/**
	 * Checks the swing type and does the appropriate doSwing
	 * 
	 * @param a The attacker
	 * @param d The defender
	 * @param defenderPos The defender's position
	 */
	private void checkSwing(Player a, Player d, int defenderPos)
	{
		if (a.getSwinging() == Info.HORIZONTAL)
		{
			if (a.getSwordPosition() + a.getPosition() == defenderPos)
				doSwing(a, d, HORIZ_DMG);
		}
		else if (a.getSwinging() == Info.VERTICAL)
		{
			if (a.getSwordPosition() == 0 && a.getPosition() == defenderPos)
					doSwing(a, d, VERT_DMG);
		}
	}
	
	/**
	 * Performs the swing action, dealing appropriate damage
	 * 
	 * @param a The attacker
	 * @param d The defender
	 * @param damage The possible damage dealt
	 */
	private void doSwing(Player a, Player d, int damage)
	{
		d.hit(damage);
		d.swingInterrupt();
		
		if (d.getBlocking())
			a.swingInterrupt();
	}
	
	/**
	 * Decides what clang type is needed
	 * 
	 * @param p1 P1's swing_type
	 * @param p2 P2's swing_type
	 * @return -1 - no clang
	 *		    0 - p1 horizontal & p2 horizontal
	 * 			1 - p1 vertical & p2 horizontal
	 * 			2 - p1 horizontal & p2 vertical
	 * 			3 - p1 vertical & p2 vertical
	 */
	private int clang(double p1, double p2)
	{
		if (p1 == p2)
		{
			if (p1 == Info.HORIZONTAL)
				return 0;
			else
				return 3;
		}
		
		if (p1 == Info.HORIZONTAL)
			return 2;
		
		return 1;
	}
	
	/**
	 * Draws information to the console
	 * 
	 * @param clang The type of sword strike
	 */
	private void drawConsole(int clang)
	{
		Info p1 = players[0].getInfo();
		Info p2 = players[1].getInfo();
		
		// Draw health info first
		System.out.printf("P1: %1$3d     P2: %2$3d\n", p1.health, p2.health);
		
		// Draw player2's location and sword position
		drawLocation(-1, p2);
		drawSword(-1, p2, clang);
		
		// Draw Player 1's stuff
		drawSword(1, p1, clang);
		drawLocation(1, p1);		
	}
	
	/**
	 * Draws the player location on console
	 * 
	 * @param mod 1 for p1, -1 for p2
	 * @param p The player info
	 */
	private void drawLocation(int mod, Info p)
	{
		drawSpaces(mod*p.position + world_size);
		System.out.println(p.dodging ? "o" : p.stunned ? "8" : "O");
	}
	
	/**
	 * Draws a specific amount of spaces
	 * 
	 * @param num The number of spaces to draw
	 */
	private void drawSpaces(int num)
	{
		for (int i=0; i < num; i++)
			System.out.print(" ");
	}
	
	/**
	 * Draws the sword in the correct location and orientation
	 * 
	 * @param mod 1 for p1, -1 for p2
	 * @param p The player info
	 * @param clang The type of strike
	 */
	private void drawSword(int mod, Info p, int clang)
	{
		// Resting: O
		//          `
		
		// Horizontal:   O O O
		//              /  |  \
		
		// Vertical:   O O O
		//             . | .
		
		// Blocking:   O
		//             +
		
		// Check swing type
		
		switch((int)p.swinging)
		{
		case (int)Info.HORIZONTAL:
			drawSpaces((int)(mod*p.position + world_size + mod*p.sword_position));
		
			if (clang > -1)
			{
				System.out.println("X");
			}
			else
			{
				switch((int)(p.sword_position))
				{
				case -1:
					System.out.println("\\");
					break;
				case 0:
					System.out.println("|");
					break;
				default:
					System.out.println("/");
					break;
				}
			}
			break;
			
		case (int)Info.VERTICAL:
			drawSpaces(((int)mod*p.position + world_size));
		
			if (clang > -1)
			{
				System.out.println("X");
			}
			else
			{
				if (p.sword_position == 0)
					System.out.println("|");
				else
					System.out.println(".");
			}
			break;
			
		default:
			drawSpaces(mod*p.position + world_size);
			System.out.println(p.blocking ? "+" : "`");
			break;
		}
	}
	
	/**
	 * Used for testing purposes
	 * 
	 */
	public static void main(String[] args)
	{
		Commands c1 = new Commands();
		Commands c2 = new Commands();
		Engine e = new Engine(c1, c2, 5);

		e.begin();
		
		while(true)
		{
			c1.add_command(Commands.HORIZ_SWING);
			c2.add_command(Commands.BLOCK);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			c1.add_command(Commands.VERT_SWING);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			c2.add_command(Commands.STRAFE_LEFT);
			c1.add_command(Commands.VERT_SWING);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			c1.add_command(Commands.STRAFE_RIGHT);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			c1.add_command(Commands.VERT_SWING);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			c2.add_command(Commands.STRAFE_RIGHT);
			c1.add_command(Commands.STRAFE_LEFT);
		}
	}
}
