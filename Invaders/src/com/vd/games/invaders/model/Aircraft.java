package com.vd.games.invaders.model;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.vd.games.invaders.graphics.IAnimatedElement;
import com.vd.games.invaders.graphics.IAnimatedElement.ElementState;
import com.vd.games.invaders.graphics.IElementRenderer;

public class Aircraft implements IAnimatedElement {

	private SpaceTimeCoordinate plot;
	private int[] movx = {-1,0,+1,0};
	private int[] movy = {0,-1,0,+1};
	private int[] dyingmovy = {-1,-2,-3,-4};
	private int mov;
	private ElementState state;
	
	
	public Aircraft(SpaceTimeCoordinate plot) {
		this.plot = plot;
		this.plot.setWidth(12);
		this.plot.setHeight(12);
		this.mov = 0;
		this.state = ElementState.LIVE;
	}
	
	public boolean animate(long newtime) {
				
		int[] localmovy = movy;
		if(state.equals(ElementState.DIYNG)) {
			localmovy = dyingmovy;
		}
		plot.setPosx(plot.getPosx()+(movx[mov]));
		plot.setPosy(plot.getPosy()+(localmovy[mov]));
		plot.setLasttime(newtime);
		mov++;
		if(mov > 3) {
			mov = 0;
			if(state.equals(ElementState.DIYNG))
				return true; //element should be removed
		}
		
		return false;
	}

	public IElementRenderer getRenderer() {
		return new IElementRenderer(){
			public void render(Canvas canvas) {
				Paint aircraftColor = new Paint();
				aircraftColor.setAntiAlias(true);
				aircraftColor.setARGB(255, 055, 055, 255);
				if(state.equals(ElementState.DIYNG)){
					aircraftColor.setARGB(255, 055, 255, 255);	
				}
				canvas.drawRect(
						new RectF(plot.getPosx(), plot.getPosy(), plot.getPosx()+12, plot.getPosy()+12), 
						aircraftColor);				
			}
			
		};
	}

	public void changeState(ElementState state) {
		if(state == ElementState.DIYNG) {
			this.state = state;
		}
		else {
			throw new IllegalStateException("Aircraft can only change to DYING state");
		}
		
	}

	public SpaceTimeCoordinate getPosition() {
		return this.plot;
	}

	public ElementState getState() {
		return this.state;
	}

	public ArrayList<IAnimatedElement> getElements() {
		// no childs
		return null;
	}
}
