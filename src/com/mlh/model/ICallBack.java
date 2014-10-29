package com.mlh.model;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;

/**
 * @author Matteo
 *实现CallBack，类似函数指针的Interface
 */
public interface ICallBack {
	void refresh(Handler hdl);
	void clickItem(AdapterView<?> parent, View view,
			int position, long id);
}
