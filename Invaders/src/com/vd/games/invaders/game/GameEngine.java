/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.View;
import android.view.View.OnTouchListener;

import com.vd.games.invaders.graphics.IAnimatedElement;
import com.vd.games.invaders.graphics.IAnimatedElement.ElementState;
import com.vd.games.invaders.graphics.InvadersView;
import com.vd.games.invaders.model.Bullet;
import com.vd.games.invaders.model.SpaceTimeCoordinate;

/**
 * This object is responsible to handle elements in the game space-time. The main
 * feature of this game is his real-time behavior, that is, elements will move
 * at a real velocity independently of device cpu speed. That will cause fast
 * elements (like bullets) will be impressed at the screen only a few times
 * (depends on device cpu speed) before they get out.
 * 
 * @author Victor de la Rosa
 * 
 */
public class GameEngine extends Observable {

	/** device resolution */
	private static final int STANDARD_SCREEN_WIDTH = 600;
	private static int screenProportion = STANDARD_SCREEN_WIDTH;

	/** surface change events */
	public SurfaceCallback surfaceCallback;
	/** responsible to draw main view contents */
	private InvadersView mainView;
	/** game loop thread */
	private GameEngineThread gameThread;

	/** device services */
	private Vibrator vibrator;
	private SoundPool soundPool;
	private AudioManager audioManager;

	/**
	 * aircraft list is handle only by gamethreas (timepulse) and should not be
	 * thread-safe
	 */
	private List<IAnimatedElement> aircrafts;

	/**
	 * Used to notify observers
	 * 
	 * @author Victor de la Rosa
	 * 
	 */
	public enum GameEventType {
		FINISH_LEVEL, START_GAME, EXIT_GAME, PAUSE_GAME, RESUME_GAME
	}

	/**
	 * bullects elements is updated by main thread (ontouch) and gamethread
	 * (remove and paint) will use a queue to add bullets asynchronously and
	 * avoid concurrency
	 */
	private List<IAnimatedElement> bullets;
	private Queue<IAnimatedElement> newBulletsQueue;

	/** enable event treatment */
	private boolean gameThreadRunning;

	private long lastTimePulse;

	public enum ActionType {
		SHOT
	}

	public GameEngine() {
		gameThreadRunning = false;
		surfaceCallback = new SurfaceCallback();
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);

	}

	protected void start() {
		lastTimePulse = SystemClock.uptimeMillis();

		if (gameThread != null)
			gameThread.endRun(); // left thread ends

		gameThread = new GameEngineThread(this);

		// init internal thread
		gameThread.start();
		// enable events treatment
		gameThreadRunning = true;

	}

	protected void stop() {
		gameThreadRunning = false;

		if (gameThread != null)
			gameThread.endRun();

	}

	protected void pause() {
		if (gameThread != null) {
			gameThread.pauseEvent();
		}
		gameThreadRunning = false;
	}

	protected void resume() {
		if (gameThread != null) {
			gameThread.resumeEvent();
			gameThreadRunning = true;
		}
	}

	/**
	 * Called at least one when view is created(changed), only portrait
	 * orientation allowed
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void engineRescale(int screenWidth, int screenHeight) {
		if (gameThread != null)
			gameThread.pauseEvent();
		screenProportion = screenWidth * 100 / STANDARD_SCREEN_WIDTH;
		mainView.changeScale(screenWidth, screenHeight);
		if (gameThread != null)
			gameThread.resumeEvent();
	}

	/**
	 * An touch event.
	 * 
	 * @param type
	 * @param event
	 * @return
	 */
	public boolean actionEvent(ActionType type, MotionEvent event) {
		if (!gameThreadRunning) {
			return false; // don't worry about next touch events
		}

		int action = event.getActionMasked();
		int pointerIndex = event.getActionIndex();

		SpaceTimeCoordinate plot = null;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
			plot = new SpaceTimeCoordinate((int) event.getX(pointerIndex),
					(int) event.getY(pointerIndex), event.getEventTime());
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_CANCEL:
		default:
			break;
		}

		if (plot != null) {
			switch (type) {
			case SHOT:
				createBullet(plot);
				break;
			}
		}

		return true; // want to receive subsequence events

	}

	/**
	 * Represent time step, carrying out animation or removing each visible
	 * element. This method is called by gameThread and operations inside should
	 * be thread-safe
	 */
	public void timePulse(long newtime) {

		// space will be update every 
		int MIN_INSTANCE = 50; // ms
		if (newtime - lastTimePulse < MIN_INSTANCE) {
			return;
		}
		// get new bullets (touch events)
		//TODO use bullets pool to avoid create new elements
		IAnimatedElement bullet;
		while ((bullet = newBulletsQueue.poll()) != null) {
			bullets.add(bullet);
		}

		//animate bullets
		Iterator<IAnimatedElement> iterator = bullets.iterator();
		while (iterator.hasNext()) {
			IAnimatedElement el = iterator.next();
			if (el.animate(newtime)) {
				iterator.remove();
			}
		}

		//animate alien aircrafts
		Iterator<IAnimatedElement> aircraftIterator = aircrafts.iterator();
		while (aircraftIterator.hasNext()) {
			IAnimatedElement aircraftElement = aircraftIterator.next();
			if (aircraftElement.animate(newtime)) {
				aircraftIterator.remove();
			}
			Iterator<IAnimatedElement> bulletIterator = bullets.iterator();
			while (bulletIterator.hasNext()) {
				IAnimatedElement bulletElement = bulletIterator.next();
				if (aircraftElement.getState().equals(ElementState.LIVE)
						&& aircraftElement.getPosition().intersect(
								bulletElement.getPosition())) {
					aircraftElement.changeState(ElementState.DIYNG);
					if (vibrator != null)
						vibrator.vibrate(50);
					bulletIterator.remove(); // remove bullet
				}
			}
		}		
		
		//notify level completed
		if (aircrafts.isEmpty()) {
			setChanged();
			notifyObservers(GameEventType.FINISH_LEVEL);
		}
		
		// render elements
		mainView.renderElements(getElements());
		

	}

	public List<IAnimatedElement> getElements() {
		ArrayList<IAnimatedElement> elements = new ArrayList<IAnimatedElement>();
		elements.addAll(aircrafts);
		elements.addAll(bullets);

		return elements;
	}

	public void setArmy(ArrayList<IAnimatedElement> aircrafts2) {
		this.aircrafts = aircrafts2;

	}

	/**
	 * Should be set by gameThread thread (ontimepulse), that way dont require
	 * to be thread safe
	 * 
	 * @param bullets2
	 */
	public void initsBullets() {
		this.bullets = new ArrayList<IAnimatedElement>();
		if (newBulletsQueue == null) {
			newBulletsQueue = new LinkedList<IAnimatedElement>();
		}
		while (newBulletsQueue.poll() != null)
			;
	}

	public void setView(InvadersView invadersView) {
		this.mainView = invadersView;

	}

	/**
	 * Add new bullets to queue to avoid blocks between main thread (touches) and game thread.
	 * @param plot
	 */
	private void createBullet(SpaceTimeCoordinate plot) {
		newBulletsQueue.offer(new Bullet(plot));
		long[] pattern = { 0, 15L, 15L, 15L };
		if (vibrator != null)
			vibrator.vibrate(pattern, -1);
	}

	public Vibrator getVibrator() {
		return vibrator;
	}

	public void setVibrator(Vibrator vibrator) {
		this.vibrator = vibrator;
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public void setAudioManager(AudioManager audioManager) {
		this.audioManager = audioManager;
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
			if (!gameThreadRunning) {
				return false;
			}
			// assume main view touched
			boolean bHandled = actionEvent(ActionType.SHOT, event);
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return bHandled;
		}
	}

	public static int getSWidth(int pos) {
		return getScreenProportion() * pos / 100;
	}

	public static int getSHeight(int pos) {
		return getScreenProportion() * pos / 100;
	}

	/**
	 * Use for any object created or movement calculation
	 * 
	 * @return screen proportion in %
	 */
	public static int getScreenProportion() {

		return screenProportion;
	}

}
