package game_engine;

import java.util.concurrent.Semaphore;

import client.StateData;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;

public class Synch {
	
	static private Synch _instance = null;
	
	public Semaphore serverSend[];
	public Mutex serverReceive[];
	public Semaphore engine;

	public Synch()
	{
		engine = new Semaphore(0);
		serverSend = new Semaphore[2];
		serverReceive = new Mutex[2];
		serverSend[0] = new Semaphore(0);
		serverSend[1] = new Semaphore(0);
		serverReceive[0] = new Mutex();
		serverReceive[1] = new Mutex();
		try {
			serverReceive[0].acquire();
			serverReceive[1].acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public Synch getInstance() {
		
		if (_instance == null) {
			synchronized(Synch.class) {
				if (_instance == null) _instance = new Synch();
			}
		}
		
		return _instance;
	}
}
