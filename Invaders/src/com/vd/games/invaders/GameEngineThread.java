package com.vd.games.invaders;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.SystemClock;

class GameEngineThread extends Thread {

	
	private GameEngine gameEngine;
	
	private AtomicBoolean bRun = new AtomicBoolean(false); 
	private AtomicBoolean bPause = new AtomicBoolean(false);

	public GameEngineThread(GameEngine gameEngine) {
		
		this.gameEngine = gameEngine;

	}

	@Override
	public void run() {

		bRun.set(true);

		while (bRun.get()) {
						
			gameEngine.timePulse(SystemClock.uptimeMillis());
			
			//handle pause event (activity in the background)
			while(bPause.get()){
				try {
					synchronized (this) { //get object monitor
						wait();	
					}					
				} catch (InterruptedException e) {}
			}
		}
	}


	
	public void pauseEvent()  {
		bPause.set(true);
	}
	
	public void resumeEvent() {
		bPause.set(false);
		synchronized (this) {
			notifyAll();	
		}		
	}

	public void endRun() {
		bPause.set(false);
		bRun.set(false);
		synchronized (this) {
			notifyAll();	
		}		
	}
}