package com.vd.games.invaders.game;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.SystemClock;

class GameEngineThread extends Thread {

	private GameEngine gameEngine;

	private AtomicBoolean bRun = new AtomicBoolean(false);
	private AtomicBoolean bPause = new AtomicBoolean(false);

	protected GameEngineThread(GameEngine gameEngine) {

		this.gameEngine = gameEngine;

	}

	@Override
	public void run() {

		bRun.set(true);

		while (bRun.get()) {
			gameEngine.timePulse(SystemClock.uptimeMillis());

			// handle pause event (activity move to the background)
			while (bPause.get()) {
				try {
					synchronized (this) { // get object monitor
						wait();
					}
				} catch (InterruptedException e) {
				}
			}
		}
	}

	protected void pauseEvent() {
		bPause.set(true);
	}

	protected void resumeEvent() {
		bPause.set(false);
		synchronized (this) {
			notifyAll();
		}
	}

	protected void endRun() {
		bRun.set(false);
		bPause.set(false);
		synchronized (this) {
			notifyAll();
		}
	}
}