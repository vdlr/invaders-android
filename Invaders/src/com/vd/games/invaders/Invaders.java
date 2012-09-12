/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vd.games.invaders.game.GameController;
import com.vd.games.invaders.game.GameEngine;
import com.vd.games.invaders.game.LevelFactory;
import com.vd.games.invaders.graphics.InvadersView;

/**
 * Invader is a addictive game that help us to understand object oriented design
 * under android api and how to profile and tune it.
 * This activity is responsible to init menu, views and game engine. 
 * Menu will have only two option Start and Stop.
 * 
 * @author Victor de la Rosa
 * 
 */
public class Invaders extends Activity {

	private static final int MENU_START = 1;
	private static final int MENU_STOP = 2;
	private static final int MENU_EXIT = 3;

	/** main type */
	private InvadersView invadersView;
	
	/** game levels and engine */
	private GameEngine engine;
	private GameController gameController;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {	
			//create game levels and engine
			gameController = new GameController();
			engine = new GameEngine();
			
			//in order to receive game events
			engine.addObserver(gameController);
			gameController.setGameEngine(engine);
			
			//lock orientation
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			
			//set content layout
			setContentView(R.layout.layout_invaders);
			//get main view reference
			invadersView = (InvadersView) findViewById(R.id.invadersView);					

			//add surface change listener
			invadersView.getHolder().addCallback(engine.surfaceCallback);
			//add touch event listener
			invadersView.setOnTouchListener(engine.surfaceCallback);		
			
			//services injection
			gameController.setView((TextView)findViewById(R.id.levelView));
			gameController.setLevelFactory(new LevelFactory());
			engine.setView(invadersView);
			engine.setAudioManager((AudioManager) getSystemService(AUDIO_SERVICE));
			engine.setVibrator((Vibrator) getSystemService(VIBRATOR_SERVICE));
		
		} catch (Exception ex) {
			Log.e("Invaders", "Error al crear actividad",ex);
			Toast.makeText(getApplicationContext(), "La aplicación Invaders no se ha iniciado correctamente", Toast.LENGTH_SHORT).show();		
			finish();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_START, 0, R.string.menu_start);
		//menu.add(0, MENU_STOP, 0, R.string.menu_stop);
		menu.add(0, MENU_EXIT, 0, R.string.menu_exit);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case MENU_START:
				gameController.start();
				return true;
			case MENU_STOP:
				gameController.exit();
			case MENU_EXIT:
				gameController.exit();
				finish();
			}
			return false;
		}
		catch (Exception ex) {
			Log.e("Invaders", "Error en la acción",ex);
			Toast.makeText(getApplicationContext(), "La aplicación Invaders se ha cerrado inesperadamente", Toast.LENGTH_SHORT).show();		
			finish();
			return false;
		}
	}

	@Override
	protected void onPause() {
		gameController.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		gameController.resume();
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		if (gameController != null) {
			gameController.exit();
		}
		super.onStop();
	}		
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO save game data
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO load last game data
		super.onRestoreInstanceState(savedInstanceState);
	}
}
