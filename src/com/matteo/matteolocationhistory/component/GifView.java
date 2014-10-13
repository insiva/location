package com.matteo.matteolocationhistory.component;

import java.io.InputStream;

import com.matteo.matteolocationhistory.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

public class GifView extends View {
	private Movie mMovie;
	private long movieStart=0;
	private Movie movie;
	 
    public GifView(Context context) {
        super(context);
        initializeView();
    }
 
    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }
 
    public GifView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView();
    }
 
    private void initializeView() {
        //R.drawable.loader - our animated GIF
        InputStream is = getContext().getResources().openRawResource(R.drawable.wait);
        mMovie = Movie.decodeStream(is);
    }

	@Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = android.os.SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (movie != null) {
            int relTime = (int) ((now - movieStart) % movie.duration());
            movie.setTime(relTime);
            movie.draw(canvas, getWidth() - movie.width(), getHeight() - movie.height());
            //movie.
            this.invalidate();
        }
    }
}
