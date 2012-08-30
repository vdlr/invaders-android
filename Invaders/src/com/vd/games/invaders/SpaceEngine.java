/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import com.vd.games.invaders.graphics.IAnimatedElement;
import com.vd.games.invaders.graphics.IAnimatedElement.ElementState;
import com.vd.games.invaders.model.Aircraft;
import com.vd.games.invaders.model.Bullet;
import com.vd.games.invaders.model.PlotTime;

/**
 * This object is responsible to handle elements in the game space-time The main
 * feature of this game is his real time behavior, that is, elements will move
 * at the same velocity independently of device cpu speed. That will cause fast
 * elements (like bullets) will be impressed at the screen only a few times
 * (depends on device cpu speed) before they get out.
 * 
 * @author Victor de la Rosa
 * 
 */
public class SpaceEngine {



	public enum ActionType {
		SHOT
	}

	private List<IAnimatedElement> aircrafts;
	private List<IAnimatedElement> bullets;
	private ReentrantReadWriteLock lock;
	private boolean engineRunning;
	private long lastTimePulse;

	public SpaceEngine() {
		engineRunning = false;
	}

	public void init() {		
		lock = new ReentrantReadWriteLock();
		lastTimePulse = SystemClock.uptimeMillis();		
	}
	
	public void restart(int screenWidth, int screenHeight) {
		engineRunning = false;
		createAlienArmy(screenWidth, screenHeight);
		bullets = new ArrayList<IAnimatedElement>();
		engineRunning = true;
	}

	public List<IAnimatedElement> getElements() {
		ArrayList<IAnimatedElement> elements = new ArrayList<IAnimatedElement>();
		elements.addAll(aircrafts);
		try {
			lock.readLock().lock();
			elements.addAll(bullets);
		} finally {
			lock.readLock().unlock();
		}
		return elements;
	}

	/**
	 * An user event
	 * 
	 * @param type
	 * @param event
	 */
	public void actionEvent(ActionType type, MotionEvent event) {
		if (!engineRunning) {
			return;
		}
		
		ArrayList<PlotTime> plots = new ArrayList<PlotTime>();
		
		//read max two pointer/fingers
		for(int p = 0; p < event.getPointerCount() && p < 2; p++){					
			plots.add( new PlotTime((int) event.getX(p), (int) event.getY(p),
					translateGameTime(event.getEventTime())));
		}
	

		switch (type) {
		case SHOT:
			createBullets(plots);
			break;

		}
	}

	/**
	 * 
	 */
	public void timePulse(long newtime) {

		// space will be update every 100ms
		int MIN_INSTANCE = 100; // ms
		if (newtime - lastTimePulse < MIN_INSTANCE) {
			return;
		}

		long gametime = translateGameTime(newtime);

		
		try {
			lock.writeLock().lock();
			Iterator<IAnimatedElement> iterator = bullets.iterator();
			while (iterator.hasNext()) {
				IAnimatedElement el = iterator.next();
				if (el.animate(gametime)) {
					iterator.remove();
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
		
		try {
			lock.writeLock().lock();
			Iterator<IAnimatedElement> aircraftIterator = aircrafts.iterator();
			while (aircraftIterator.hasNext()) {
				IAnimatedElement aircraftElement = aircraftIterator.next();
				if(aircraftElement.animate(gametime)){
					aircraftIterator.remove();
				}
				Iterator<IAnimatedElement> bulletIterator = bullets.iterator();
				while (bulletIterator.hasNext()) {
					IAnimatedElement bulletElement = bulletIterator.next();
					if(aircraftElement.getState().equals(ElementState.LIVE)
							&& aircraftElement.getPosition().intersect(bulletElement.getPosition())) {
						aircraftElement.changeState(ElementState.DIYNG);
						bulletIterator.remove(); //remove bullet
					}
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	private long translateGameTime(long newtime) {
		if(1==1) return newtime;
		// translate real time to game space time
		long GAME_TIME_DURATION = 600000;// ms = 10 MIN
		int gametime = (int)newtime;
		if(newtime > GAME_TIME_DURATION)
			gametime = Long.valueOf(newtime % GAME_TIME_DURATION).intValue();
		
		return gametime;
	}

	private void createBullets(ArrayList<PlotTime> plots) {
		try {
			lock.writeLock().lock();
			for(PlotTime plot : plots)
				bullets.add(new Bullet(plot));
		} finally {
			lock.writeLock().unlock();
		}		
	}

	private void createAlienArmy(int screenWidth, int screenHeight) {
		aircrafts = new ArrayList<IAnimatedElement>();
		aircrafts.add(new Aircraft(new PlotTime(50, 99,
				translateGameTime(SystemClock.uptimeMillis()))));
		aircrafts.add(new Aircraft(new PlotTime(87, 103,
				translateGameTime(SystemClock.uptimeMillis()))));
		aircrafts.add(new Aircraft(new PlotTime(112, 98,
				translateGameTime(SystemClock.uptimeMillis()))));
		aircrafts.add(new Aircraft(new PlotTime(145, 110,
				translateGameTime(SystemClock.uptimeMillis()))));
	}

	class HandleMotionEvents {
		
		ArrayList<Event> events = new ArrayList<Event>();
		
		
	}
	
	class Event {
		
	}
}
