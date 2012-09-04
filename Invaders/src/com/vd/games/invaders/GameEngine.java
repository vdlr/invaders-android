/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;

import com.vd.games.invaders.graphics.IAnimatedElement;
import com.vd.games.invaders.graphics.IAnimatedElement.ElementState;
import com.vd.games.invaders.graphics.InvadersView;
import com.vd.games.invaders.model.Aircraft;
import com.vd.games.invaders.model.Bullet;
import com.vd.games.invaders.model.PlotTime;

/**
 * This object is responsible to handle elements in the game space-time The main
 * feature of this game is his real time behavior, that is, elements will move
 * at a real velocity independently of device cpu speed. That will cause fast
 * elements (like bullets) will be impressed at the screen only a few times
 * (depends on device cpu speed) before they get out.
 * 
 * @author Victor de la Rosa
 * 
 */
public class GameEngine {

	/** singleton surfaceview type */
	private Invaders invadersActivity;
	/** surface change events */
	public SurfaceCallback surfaceCallback;
	/** responsible to draw main view contents */
	private InvadersView mainView;
	/** game update loop */
	private GameEngineThread gameThread;
	
	/** aircraft list is handle only by gamethreas (timepulse) and should not be thread-safe */
	private List<IAnimatedElement> aircrafts;
	
	/** bullects elements is updated by main thread (ontouch) and gamethread (remove and paint)
	 *  will use a queue to add bullets asynchronously (each maintain creating time) */	
	private List<IAnimatedElement> bullets;
	private Queue<IAnimatedElement> newBulletsQueue;
	
	/** enable event treatment */
	private boolean gameThreadStarted;
	
	private long lastTimePulse;
	

	public enum ActionType {
		SHOT
	}

	public GameEngine() {
		gameThreadStarted = false;
		surfaceCallback = new SurfaceCallback();
	}

	public void start() {		
		lastTimePulse = SystemClock.uptimeMillis();

		if(gameThread!=null)
			gameThread.endRun(); //left thread ends 
		
		gameThread = new GameEngineThread(this);


		// init internal thread
		gameThread.start();
		//enable events treatment
		gameThreadStarted = true;
	}
	
	public void stop() {
		gameThreadStarted = false;
		
		if(gameThread!=null)
			gameThread.endRun();
		
	}
	
	public void pause() {
		if(gameThread!=null)
			gameThread.pauseEvent();		
	}
	
	public void resume() {
		if(gameThread!=null)
			gameThread.resumeEvent();
		
	}

	/**
	 * Called at least one when view is created(changed)
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void engineRescale(int screenWidth, int screenHeight) {		
		if(gameThread!=null)
			gameThread.pauseEvent();
		mainView.changeScale(screenWidth, screenHeight);
		createAlienArmy(screenWidth, screenHeight);
		newBulletsQueue = new LinkedList<IAnimatedElement>();
		bullets = new ArrayList<IAnimatedElement>();
		if(gameThread!=null)
			gameThread.resumeEvent();		
	}

	/**
	 * An user event
	 * 
	 * @param type
	 * @param event
	 */
	public void actionEvent(ActionType type, MotionEvent event) {
		if (!gameThreadStarted) {
			return;
		}		
		
		ArrayList<PlotTime> plots = new ArrayList<PlotTime>();

		// read max two pointer/fingers
		for (int p = 0; p < event.getPointerCount() && p < 2; p++) {
			plots.add(new PlotTime((int) event.getX(p), (int) event.getY(p),
					translateGameTime(event.getEventTime())));
		}

		switch (type) {
		case SHOT:
			createBullets(plots);
			break;

		}
	}

	/**
	 * Represent time step. Animate or remove each visible element
	 * This method is called by gameThread and operations inside should be thread-safe 
	 */
	public void timePulse(long newtime) {

		// space will be update every 100ms
		int MIN_INSTANCE = 100; // ms
		if (newtime - lastTimePulse < MIN_INSTANCE) {
			return;
		}
		IAnimatedElement bullet;
		while((bullet = newBulletsQueue.poll()) != null) {
			bullets.add(bullet);	
		}		
		
		long gametime = translateGameTime(newtime);

					
			Iterator<IAnimatedElement> iterator = bullets.iterator();
			while (iterator.hasNext()) {
				IAnimatedElement el = iterator.next();
				if (el.animate(gametime)) {
					iterator.remove();
				}
			}

		
		Iterator<IAnimatedElement> aircraftIterator = aircrafts.iterator();
		while (aircraftIterator.hasNext()) {
			IAnimatedElement aircraftElement = aircraftIterator.next();
			if (aircraftElement.animate(gametime)) {
				aircraftIterator.remove();
			}
			Iterator<IAnimatedElement> bulletIterator = bullets.iterator();
			while (bulletIterator.hasNext()) {
				IAnimatedElement bulletElement = bulletIterator.next();
				if (aircraftElement.getState().equals(ElementState.LIVE)
						&& aircraftElement.getPosition().intersect(
								bulletElement.getPosition())) {
					aircraftElement.changeState(ElementState.DIYNG);
					bulletIterator.remove(); // remove bullet
				}
			}
		}
		
		
		// finally render elements
		mainView.renderElements(getElements());
	}

	public List<IAnimatedElement> getElements() {
		ArrayList<IAnimatedElement> elements = new ArrayList<IAnimatedElement>();
		elements.addAll(aircrafts);
		elements.addAll(bullets);
	
		return elements;
	}

	public void setView(InvadersView invadersView) {
		this.mainView = invadersView;
		
	}
	
	private long translateGameTime(long newtime) {
		if (1 == 1)
			return newtime;
		// translate real time to game space time
		long GAME_TIME_DURATION = 600000;// ms = 10 MIN
		int gametime = (int) newtime;
		if (newtime > GAME_TIME_DURATION)
			gametime = Long.valueOf(newtime % GAME_TIME_DURATION).intValue();

		return gametime;
	}

	private void createBullets(ArrayList<PlotTime> plots) {
		for (PlotTime plot : plots)
			newBulletsQueue.offer(new Bullet(plot));
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

	static class HandleMotionEvents {
		
		public static ArrayList<Event> handleMotionEvents(MotionEvent motionEvent) {
			
			final int historySize = motionEvent.getHistorySize();
		    final int pointerCount = motionEvent.getPointerCount();
		    
		    int action = motionEvent.getActionMasked();
		    int pointerIndex = motionEvent.getActionIndex();
		    
		    for (int p = 0; p < pointerCount; p++) {
		    	
		    	System.out.printf("  pointer %d: (%f,%f)",
		             motionEvent.getPointerId(p), motionEvent.getX(p), motionEvent.getY(p));
		     }
		     
		    ArrayList<Event> events = new ArrayList<Event>();
			return events;
		}

		

	}

	class Event {

	}

	class SurfaceCallback implements Callback, OnTouchListener {

		public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
				int width, int height) {

			engineRescale(width, height);
		}

		public void surfaceCreated(SurfaceHolder surfaceHolder) {
			// do nothing, use surfaceChanged
		}

		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO stop engine?

		}

		public boolean onTouch(View v, MotionEvent event) {
			if (!gameThreadStarted) {
				return false;
			}
			// assume main view touched
			actionEvent(ActionType.SHOT, event);
			return false;
		}
	}



}
