package com.matteo.matteolocationhistory;

import com.matteo.matteolocationhistory.component.LockScreenView;
import com.matteo.matteolocationhistory.component.MTActivity;
import com.matteo.matteolocationhistory.model.Conf;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class TestActivity extends Activity {
	EditText et;
	Button btn;
	RelativeLayout rl;
	LockScreenView lsv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		this.rl=(RelativeLayout)this.findViewById(R.id.rlTest);
		this.et=(EditText)this.findViewById(R.id.et);
		this.et.setText(Conf.V);
		this.btn=(Button)this.findViewById(R.id.btn);
		//com.matteo.matteolocationhistory.component.LockScreenView
		this.lsv=(LockScreenView)this.findViewById(R.id.lsvLock);
		this.btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//LockScreen(R.id.rlTest);
				if(lsv.Locked)
				{
					lsv.Unlock();
				}else
				lsv.Lock();
			}});
	}
	
}
