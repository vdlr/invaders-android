/* @Victor de la Rosa 2012 - www.vd-consultoriasoftware.com */
package com.vd.games.invaders.graphics;

import java.util.List;

import com.vd.games.invaders.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This is a surface view type responsible of update full screen objects.
 * 
 * 
 * @author Victor de la Rosa
 * 
 */
public class InvadersView extends SurfaceView {

	private Bitmap backgroundImage;

	public InvadersView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setFocusable(true);

		Resources res = getContext().getResources();
		backgroundImage = BitmapFactory.decodeResource(res,
				R.drawable.earthrise);

		Log.d("InvadersView", "InvadersView CREATED");

	}

	public void changeScale(int screenWidth, int screenHeight) {
		// don't forget to resize the background image
		backgroundImage = Bitmap.createScaledBitmap(backgroundImage,
				screenWidth, screenHeight, true);
	}

	public void renderElements(List<IAnimatedElement> elements) {

		Canvas canvas = null;
		SurfaceHolder surfaceHolder = getHolder();
		try {
			canvas = surfaceHolder.lockCanvas(null);
			synchronized (surfaceHolder) {
				canvas.drawBitmap(backgroundImage, 0, 0, null);

				for (IAnimatedElement element : elements)
					element.getRenderer().render(canvas);
			}
		} finally {
			// in case when an exception is thrown don't leave Surface in an
			// inconsistent state
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}

	}

}
