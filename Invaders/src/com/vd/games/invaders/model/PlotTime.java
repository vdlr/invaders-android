package com.vd.games.invaders.model;

import android.util.Log;

/**
 * A place in android screen space-time 
 * 
 * @author Victor de la Rosa
 *
 */
public class PlotTime {

	private int posx;
	private int posy;
	private int width;
	private int height;
	private long lasttime;
	private int lastx;
	private int lasty;
	
	public PlotTime(int posx, int posy, long lasttime) {
		this.setPosx(posx);
		this.setPosy(posy);
		this.width = 1;
		this.height = 1;
		this.setLasttime(lasttime);		
	}
	
	public PlotTime(int posx, int posy, int width, int height, int lasttime) {
		this.setPosx(posx);
		this.setPosy(posy);
		this.width = width;
		this.height = height;
		this.setLasttime(lasttime);		
	}

	public int getPosx() {
		return posx;
	}

	public void setPosx(int posx) {
		this.posx = posx;
	}

	public int getPosy() {
		return posy;
	}

	public void setPosy(int posy) {
		this.posy = posy;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public long getLasttime() {
		return lasttime;
	}

	/**
	 * Unit time 
	 * @param lasttime
	 */
	public void setLasttime(long lasttime) {	
		this.lasttime = lasttime;			
	}

	public boolean intersect(PlotTime position) {
		//TODO assume linear movement by now
		Log.d("Intersect", this.posx + "_" + this.posy + " " + position.posx + "_" + position.posy + "_" + position.lasty);
		if(position.posx > this.posx 
				&& position.posx < (this.posx + this.width)
				&& (this.posy + this.height) > position.posy 
				&& this.posy < position.lasty) {
			return true;
		}
		return false;						
	}

	public void setWidth(int width) {
		this.width = width;
		
	}

	public void setHeight(int height) {
		this.height = height;
		
	}

	public void setLastposy(int lasty) {
		this.lasty = lasty;
		
	}
	 
	
}
