package com.matteo.matteolocationhistory.component;


import com.matteo.matteolocationhistory.R;
import com.matteo.matteolocationhistory.model.Fun;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class MTActivity extends Activity implements LockScreenInterface{
	

	public RelativeLayout rlLockScreen=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void LockScreen(int resID) {
		// TODO Auto-generated method stub
		Fun.Log("1");
		if(this.rlLockScreen==null){
			this.rlLockScreen=(RelativeLayout) LayoutInflater.from(this).inflate(R.layout.lockscreen_layout, null);
		}
		Fun.Log("11");
		ViewGroup vg=(ViewGroup) this.findViewById(resID);
		Fun.Log("2");
		vg.addView(this.rlLockScreen);
		Fun.Log("3");
		//this.addContentView(this.rlLockScreen,null);
	}

	@Override
	public void UnlockScreen(int resID) {
		// TODO Auto-generated method stub
		ViewGroup vg=(ViewGroup) this.findViewById(resID);
		vg.removeView(this.rlLockScreen);
	}
}
