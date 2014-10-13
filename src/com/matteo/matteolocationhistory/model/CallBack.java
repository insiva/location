package com.matteo.matteolocationhistory.model;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

public interface CallBack {
void Refresh(Handler hdl);
void ClickItem(AdapterView<?> parent, View view,
		int position, long id);
}
