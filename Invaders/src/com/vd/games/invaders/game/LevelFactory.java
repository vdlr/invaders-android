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
		levels = new ArrayList<GameLevel>();
		//load level from source
		levels.add(new GameLevel(1));
		levels.add(new GameLevel(2));
		levels.add(new GameLevel(3));
		levels.add(new GameLevel(4));
		levels.add(new GameLevel(5));
	}
	
	
	/**
	 * Return GameLevel for level, starting at 1
	 * @param level
	 * @return
	 */
	public GameLevel getLevel(int level) {		
		if(level <= levels.size()) {
			return this.levels.get(level - 1);
		}
		else {
			//TODO implement game over event
			return levels.get(levels.size()-1);
		}
	}
	
	
}