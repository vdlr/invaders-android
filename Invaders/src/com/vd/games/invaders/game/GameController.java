package com.vd.games.invaders.game;

import java.util.Observable;
import java.util.Observer;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.vd.games.invaders.game.GameEngine.GameEventType;

public class GameController implements Observer {

	private TextView levelTextView;
	private GameEngine gameEngine;
	private int level;	
	private LevelFactory levelFactory;

	public GameController() {
		this.level = 1;
	}

	/**
	 * Call by gameEngine (observed) when game finished
	 */
	public void update(Observable observable, Object obj) {
		handleGameEvent((GameEngine.GameEventType) obj);		
	}

	/**
	 * To start game
	 */
	public void start() {		
		handleGameEvent(GameEventType.START_GAME);
	}

	/**
	 * When exit game
	 */
	public void exit() {
		handleGameEvent(GameEventType.EXIT_GAME);
	}

	public void pause() {
		handleGameEvent(GameEventType.PAUSE_GAME);

	}

	public void resume() {
		handleGameEvent(GameEventType.RESUME_GAME);

	}

	public void setView(TextView textView) {
		this.levelTextView = textView;
	}

	public void setGameEngine(GameEngine gameEngine) {
		this.gameEngine = gameEngine;
	}

	public void setLevelFactory(LevelFactory levelFactory) {
		this.levelFactory = levelFactory;

	}

	private void handleGameEvent(GameEngine.GameEventType event) {
		switch (event) {
		case START_GAME:
			gameEngine.stop();
			this.level = 1;
			setLevelElements();
			runLevelTextAnimation();
			gameEngine.start();
			
			break;
		case FINISH_LEVEL:
			gameEngine.stop();
			this.level++;
			setLevelElements();
			runLevelTextAnimation();
			gameEngine.start();
			break;
		case EXIT_GAME:
			if(gameEngine!=null)
				gameEngine.stop();
		case PAUSE_GAME:
			if(gameEngine!=null)
				gameEngine.pause();
		case RESUME_GAME:
			if(gameEngine!=null)
				gameEngine.resume();
		}
		
	}

	private void runLevelTextAnimation() {
		Handler viewHandler = levelTextView.getHandler();
		viewHandler.post(new Runnable() {
			public void run() {
				Animation animation = new TranslateAnimation(0, 0, 0,
						GameEngine.getSWidth(800));
				animation.setDuration(1000);
				levelTextView.setText("Level " + level);
				levelTextView.startAnimation(animation);
			}
		});

	}



	private void setLevelElements() {
		GameLevel gameLevel = levelFactory.getLevel(level);
		gameEngine.setArmy(gameLevel.getLevelAircrafts());
		gameEngine.initsBullets();

	}

}
