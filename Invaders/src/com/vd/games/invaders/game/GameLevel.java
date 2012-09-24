package com.vd.games.invaders.game;

import java.util.ArrayList;

import android.os.SystemClock;

import com.vd.games.invaders.graphics.IAnimatedElement;
import com.vd.games.invaders.model.Aircraft;
import com.vd.games.invaders.model.SpaceTimeCoordinate;

public class GameLevel {

	private int level;
	private ArrayList<IAnimatedElement> aircrafts;
	
	public GameLevel (int level) {
		this.level = level;
	}
	
	
	public ArrayList<IAnimatedElement> getLevelAircrafts() {
		if(aircrafts == null){
			aircrafts = new ArrayList<IAnimatedElement>();
		}

		//TODO use specific GameLevel implementation for each level
		for (int a = 0; a < 1 + (level * 4); a++) {
			aircrafts.add(new Aircraft(new SpaceTimeCoordinate(87 + (a%5 * 43), 80 + (a%3 * 31), SystemClock
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
