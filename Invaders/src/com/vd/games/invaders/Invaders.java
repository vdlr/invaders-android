/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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

	/** main type */
	private InvadersView invadersView;
	
	/** game engine */
	private GameEngine engine;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {	
			//create game engine
			engine = new GameEngine();
			
			//set content layout
			setContentView(R.layout.layout_invaders);
			//get main view reference
			invadersView = (InvadersView) findViewById(R.id.InvadersView);					

			//add surface change listener
			invadersView.getHolder().addCallback(engine.surfaceCallback);
			//add touch event listener
			invadersView.setOnTouchListener(engine.surfaceCallback);		
			//view injection
			engine.setView(invadersView);
		
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
		menu.add(0, MENU_STOP, 0, R.string.menu_stop);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case MENU_START:
				engine.start();
				return true;
			case MENU_STOP:
				engine.stop();
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
		engine.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		engine.resume();
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		if (engine != null) {
			engine.stop();
		}
		super.onStop();
	}		
	

}
