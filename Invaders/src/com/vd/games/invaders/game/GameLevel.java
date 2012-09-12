package com.vd.games.invaders.game;

import java.util.ArrayList;

import android.os.SystemClock;

import com.vd.games.invaders.graphics.IAnimatedElement;
import com.vd.games.invaders.model.Aircraft;
import com.vd.games.invaders.model.PlotTime;

public class GameLevel {

	private int level;
	private ArrayList<IAnimatedElement> aircrafts;
	
	public GameLevel () {
		
	}
	
	
	public ArrayList<IAnimatedElement> getAircrafts() {
		if(aircrafts == null){
			aircrafts = new ArrayList<IAnimatedElement>();
			aircrafts.add(new Aircraft(new PlotTime(GameEngine.getSWidth(300), GameEngine.getSHeight(99), SystemClock
					.uptimeMillis())));
			aircrafts.add(new Aircraft(new PlotTime(87, 103, SystemClock
					.uptimeMillis())));
			aircrafts.add(new Aircraft(new PlotTime(112, 98, SystemClock
					.uptimeMillis())));
			aircrafts.add(new Aircraft(new PlotTime(145, 110, SystemClock
					.uptimeMillis())));
		}
		return aircrafts;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}
}
