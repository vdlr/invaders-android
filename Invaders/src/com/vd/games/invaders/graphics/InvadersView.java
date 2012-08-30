/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders.graphics;

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.vd.games.invaders.R;
import com.vd.games.invaders.SpaceEngine;
import com.vd.games.invaders.SpaceEngine.ActionType;

/**
 * This is a surface view type responsible of update full screen objects.
 * ThreadInvaders inner class is responsible to update view contents and 
 * carry out calculation in a new thread in order to avoid meanwhile main view lock    
 * 
 * @author Victor de la Rosa
 *
 */
public class InvadersView extends SurfaceView implements Callback {

	private InvadersThread thread;
	private SurfaceHolder surfaceHolder;
	private Bitmap backgroundImage;
	private SpaceEngine engine;
	private boolean gameRunning;

	public InvadersView(Context context, AttributeSet attrs) {
		super(context, attrs);
		gameRunning = false;
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);		

		Resources res = context.getResources();
		backgroundImage = BitmapFactory.decodeResource(res,
				R.drawable.earthrise);

		engine = new SpaceEngine();

		setFocusable(true);
		Log.d("InvadersView", "created");

	}

	public void surfaceChanged(SurfaceHolder arg0, int format, int width,
			int height) {

		// don't forget to resize the background image
		backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width,
				height, true);
		
		engine.restart(width, height);

	}

	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		//do nothing, use surfaceChanged
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!gameRunning){
			return false;
		}
		engine.actionEvent(ActionType.SHOT, event);				
		return false;
	}

	public void startGame() {
		if(thread!=null)
			thread.endRun(); //left thread dead 
		
		thread = new InvadersThread(surfaceHolder, null);

		// Restart elements
		engine.init();

		// init internal thread
		thread.start();
		gameRunning = true;
		Log.d("InvadersView", "started");

	}

	public void stopGame() {
		thread.endRun();
		gameRunning = false;
		Log.d("InvadersView", "stopGame");
	}

	public void pause() {
		thread.pauseEvent();		
	}
	
	public void resume() {
		thread.resumeEvent();
		
	}
	
	class InvadersThread extends Thread {

		private SurfaceHolder surfaceHolder;

		private AtomicBoolean bRun = new AtomicBoolean(false); 
		private AtomicBoolean bPause = new AtomicBoolean(false);

		// handle invaders aircraft, touch events

		public InvadersThread(SurfaceHolder surfaceHolder, 
				Handler handler) {
			this.surfaceHolder = surfaceHolder;

		}

		@Override
		public void run() {

			bRun.set(true);
			Log.d(InvadersThread.class.getName(), "invaders thread running");
			while (bRun.get()) {
				Canvas canvas = null;
				try {
					canvas = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder) {						
						engine.timePulse(SystemClock.uptimeMillis());
						canvas.drawBitmap(backgroundImage, 0, 0, null);
						drawElements(canvas);
					}
				} finally {
					// in case when an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (canvas != null) {
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
				//handle pause event (activity in the background)
				while(bPause.get()){
					try {
						wait();
					} catch (InterruptedException e) {}
				}
			}
		}

		private void drawElements(Canvas canvas) {			
			for (IAnimatedElement elements : engine.getElements())
				elements.getRenderer().render(canvas);			
		}
		
		public void pauseEvent()  {
			bPause.set(true);
		}
		
		public void resumeEvent() {
			bPause.set(false);
			notifyAll();
		}

		public void endRun() {
			bPause.set(false);
			bRun.set(false);
		}
	}
	

}
