package game_engine;

public class Info implements Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int health = 100;
	public int position = 0;
	public int move = 0;
	
	public boolean dodging = false;
	public boolean blocking = false;
	public double swinging = SWORD_RESTING;
	public boolean stunned = false;
	
	public static final double SWORD_RESTING = -23;
	public static final double HORIZONTAL = 1;
	public static final double VERTICAL = 2;
	
	public double sword_position = SWORD_RESTING;
	
	public Info clone()
	{
		Info i = new Info();
		
		i.health = health;
		i.position = position;
		i.dodging = dodging;
		i.blocking = blocking;
		i.swinging = swinging;
		i.stunned = stunned;
		i.sword_position = sword_position;
		i.move = move;
		
		return i;
	}
	
	public boolean equals(Info i)
	{
		if (i == null)
			return false;
		
		if (health == i.health &&
			position == i.position &&
			dodging == i.dodging &&
			blocking == i.blocking &&
			swinging == i.swinging &&
			stunned == i.stunned &&
			sword_position == i.sword_position)
			return true;
		
		return false;
	}
}
