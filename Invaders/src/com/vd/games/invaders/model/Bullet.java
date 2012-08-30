package com.vd.games.invaders.model;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.vd.games.invaders.graphics.IAnimatedElement;
import com.vd.games.invaders.graphics.IRenderer;

public class Bullet implements IAnimatedElement {

	private static int RESOLUTION_CONSTANT = 1;
	
	public static void setResolutionConstant(int proportionalConstant) {
		RESOLUTION_CONSTANT = proportionalConstant;
	}
	
	private PlotTime plot;
	private long bulletID;


	/**
	 * Bullets has a initial posx and an own velocity
	 * @param posx
	 */
	public Bullet(PlotTime plot) {
		this.plot = plot;
		this.bulletID = plot.getLasttime();
	}
	
	public boolean animate(long newtime) {
		
		int currentposty = plot.getPosy();
		
		//speed at basic screen 30px/10ms
		int move = (int)((30*RESOLUTION_CONSTANT*(newtime-plot.getLasttime()))/10);	
		
		plot.setPosy(currentposty - move);
		plot.setLastposy(currentposty);
		plot.setLasttime(newtime);
		if(plot.getPosy() < 0)
			return true; //set as out of screen 
		return false;
	}	
	

	public IRenderer getRenderer() {
		// TODO use anonymous class, we will worry about performance later
		return new IRenderer(){
			public void render(Canvas canvas) {
				Paint bulletColor = new Paint();
				bulletColor.setAntiAlias(true);
				bulletColor.setARGB(255, 255, 200, 200);
				canvas.drawRect(
						new RectF(plot.getPosx(), plot.getPosy(), plot.getPosx()+4, plot.getPosy()+4), 
						bulletColor);
				
			}	
		};
	}

	public long getBulletID() {
		return bulletID;
	}	
	
	public String toString(){
		return "["+this.bulletID+":"+this.plot.getPosy()+"]";
	}

	public void changeState(ElementState state) {
		throw new IllegalStateException("'Bullets don't support state change.");
		
	}

	public PlotTime getPosition() {
		return this.plot;
	}

	public ElementState getState() {
		throw new IllegalStateException("state not handle by this element");
	}

	public ArrayList<IAnimatedElement> getElements() {		
		return null;
	}	
	
}
