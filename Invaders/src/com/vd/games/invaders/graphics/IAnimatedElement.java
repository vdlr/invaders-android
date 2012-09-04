package com.vd.games.invaders.graphics;

import java.util.ArrayList;

import com.vd.games.invaders.model.PlotTime;

public interface IAnimatedElement {

	public boolean animate(long gametime);
	
	public ArrayList<IAnimatedElement> getElements();
	
	public PlotTime getPosition();

	public IElementRenderer getRenderer();
	
	public void changeState(ElementState state);
	
	public ElementState getState();
	
	/**
	 * Use to change screen representation. 
	 */
	public enum ElementState{
		LIVE,
		DIYNG
	}

	

	
	
}
