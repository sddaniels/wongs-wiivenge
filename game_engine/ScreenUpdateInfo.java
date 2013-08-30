package game_engine;

import java.util.concurrent.Semaphore;

import client.StateData;

public class ScreenUpdateInfo
{
	public Info[] player;
	
	public Info[] prev_player;
	
	public int clang;
	public int prev_clang;
	
	private Semaphore engineLock;
	private Semaphore sendLock;
	
	// player assignment data
	private Object playerLock = new Object();
	private int players = 1;
	
	static private ScreenUpdateInfo _instance;
	
	public boolean blockChange(int p)
	{
		if (prev_player == null || prev_player[p] == null)
			return true;
		
		return player[p].blocking != prev_player[p].blocking;
	}
	
	public boolean positionChange(int p)
	{
		if (prev_player == null || prev_player[p] == null)
			return true;
		
		return player[p].position != prev_player[p].position;
	}
	
	public boolean moved(int p)
	{
		return player[p].move != 0;
	}
	
	public boolean healthChange(int p)
	{
		if (prev_player == null || prev_player[p] == null)
			return true;
		
		return player[p].health != prev_player[p].health;
	}
	
	public boolean dodgeChange(int p)
	{
		if (prev_player == null || prev_player[p] == null)
			return true;
		
		return player[p].dodging != prev_player[p].dodging;
	}
	
	public boolean swingChange(int p)
	{
		if (prev_player == null || prev_player[p] == null)
			return true;
		
		return player[p].swinging != prev_player[p].swinging;
	}
	
	public boolean clangChange()
	{
		if (prev_clang != clang)
			return true;
		
		return false;
	}
	
	public ScreenUpdateInfo()
	{
		player = new Info[2];
		player[0] = new Info();
		player[1] = new Info();
		
		prev_player = new Info[2];
		prev_player[0] = new Info();
		prev_player[1] = new Info();
		
		engineLock = new Semaphore(1);
		sendLock = new Semaphore(0);
		
		clang = -1;
		prev_clang = -1;
	}
	
	public void lockEngine()
	{
		try {
			engineLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unlockEngine()
	{
		engineLock.release();
	}
	
	public void lockSend()
	{
		try {
			sendLock.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unlockSend()
	{
		sendLock.release();
	}
	
	public void updateInfo(Info p1Info, Info p2Info, int clangType)
	{
		if (player[0] == null)
		{
			prev_player[0] = new Info();
			prev_player[1] = new Info();
		}
		
		prev_player[0] = player[0].clone();
		prev_player[1] = player[1].clone();
		
		prev_clang = clang;
		
		player[0] = p1Info.clone();
		player[1] = p2Info.clone();
		
		clang = clangType;
	}
	
	/////////////////////////////////
	// public methods (thread safe)
	/////////////////////////////////
	
	static public ScreenUpdateInfo getInstance() {
		
		if (_instance == null) {
			synchronized(ScreenUpdateInfo.class) {
				if (_instance == null) _instance = new ScreenUpdateInfo();
			}
		}
		
		return _instance;
	}
	
	public boolean updated()
	{
		if (!player[0].equals(prev_player[0]) ||
			!player[1].equals(prev_player[1]) ||
			clang != prev_clang)
			return true;
		
		return false;
	}
	
	public int getNextPlayerNo() {
		int returnVal;
		synchronized(playerLock) {
			returnVal = players;
			players++;
		}
		return returnVal;
	}
}
