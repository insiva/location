package com.matteo.matteolocationhistory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NewPictureReceiver extends BroadcastReceiver {
	public NewPictureReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		//throw new UnsupportedOperationException("Not yet implemented");
		String s=intent.getData().toString();
	}
}
