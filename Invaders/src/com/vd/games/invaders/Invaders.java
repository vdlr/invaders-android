/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders;

import com.vd.games.invaders.graphics.InvadersView;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Invader is a addictive game that help us to understand object oriented design
 * under android api and how to profile and tune it.
 * This activity is responsible to init menu and single view. 
 * Menu will have only two option Start and Stop.
 * 
 * @author Victor de la Rosa
 * 
 */
public class Invaders extends Activity {

	private static final int MENU_START = 1;
	private static final int MENU_STOP = 2;

	/** singleton surfaceview type */
	private InvadersView invadersView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_invaders);
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
		switch (item.getItemId()) {
		case MENU_START:
			initView();
			return true;
		case MENU_STOP:
			stopView();
		}
		return false;
	}

	@Override
	protected void onPause() {
		invadersView.pause();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		invadersView.resume();
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		if (invadersView != null) {
			invadersView.stopGame();
		}
		super.onStop();
	}
	
	private void initView() {
		if (invadersView == null) {
			invadersView = (InvadersView) findViewById(R.id.InvadersView);
		}
		invadersView.startGame();
	}

	private void stopView() {
		if (invadersView != null) {
			invadersView.stopGame();
		}
		this.finish();
	}
}
