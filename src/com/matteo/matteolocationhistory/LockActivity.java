package com.matteo.matteolocationhistory;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public abstract class LockActivity extends Activity implements LockInterface {
	RelativeLayout lyActivity;
	public LockScreen v;
	public void AddView(LockScreen ls){
		v=ls;
		this.lyActivity.addView(ls,ls.lpView);
		this.lyActivity.addView(ls.pbWait,ls.lpPbWait);
	}
}
