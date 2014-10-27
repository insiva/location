package com.mlh.model;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

public interface ICallBack {
	void refresh(Handler hdl);
	void clickItem(AdapterView<?> parent, View view,
			int position, long id);
}
