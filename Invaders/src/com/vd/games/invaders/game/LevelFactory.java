package com.vd.games.invaders.game;

import java.util.ArrayList;

/**
 * Allow to load levels from different sources (xml, url, hardcoded)
 * 
 * @author Victor de la Rosa
 *
 */
public class LevelFactory {
	
	private ArrayList<GameLevel> levels;
	
	
	public LevelFactory() {		
		//load level from source
		levels.add(new GameLevel());
		levels.add(new GameLevel());
	}
	
	
	
	public GameLevel getLevel(int level) {
		if(level < levels.size())
			return this.levels.get(level);
		else
			return null;
	}
	
	
}