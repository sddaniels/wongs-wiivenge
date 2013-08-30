package client;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.*;

public class SoundEffects implements Runnable {
	
	/////////////////////////////////
	// class variables
	/////////////////////////////////
	
	private String filename;
	private boolean repeat = false;
	private boolean delay = false;
	private int delayTime = 0;      // milliseconds
	
	/////////////////////////////////
	// constructors
	/////////////////////////////////
	
	public SoundEffects(String filename, boolean repeat) {
		this.filename = filename;
		this.repeat = repeat;
	}
	
	public SoundEffects(String filename) {
		this.filename = filename;
	}
	
	public SoundEffects(String filename, int delayTime) {
		this.filename = filename;
		delay = true;
		this.delayTime = delayTime;
	}
	
	/////////////////////////////////
	// public methods
	/////////////////////////////////
	
	public static void playSoundEffect (String filename) {
		(new Thread(new SoundEffects(filename))).start();
	}
	
	public static void playRepeatingSoundEffect (String filename) {
		(new Thread(new SoundEffects(filename, true))).start();
	}
	
	public static void playDelayedSoundEffect (String filename, int delayTime) {
		(new Thread(new SoundEffects(filename, delayTime))).start();
	}

	public void run() {
		
		try {
			
			URL fileURL = getClass().getResource(filename);
			AudioInputStream stream = AudioSystem.getAudioInputStream(fileURL);
			AudioFormat format = stream.getFormat();
			
			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(), ((int)stream.getFrameLength()*format.getFrameSize()));
			Clip clip = (Clip) AudioSystem.getLine(info);
			
			if (delay) {
				Thread.sleep(delayTime);
			}
			
			clip.open(stream);
			
			if (repeat) {
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else {
				clip.start();
			}

			
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
