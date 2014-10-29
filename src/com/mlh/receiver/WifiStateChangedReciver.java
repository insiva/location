package com.mlh.receiver;

import com.mlh.Config;
import com.mlh.communication.UploadTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

/**
 * @author Matteo
 *接收Wifi状态改变广播
 */
public class WifiStateChangedReciver extends BroadcastReceiver {
	public WifiStateChangedReciver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 这个监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
            	ConnectivityManager connManager = (ConnectivityManager) Config.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
            	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
            	if (mWifi.isConnected()) {  
            		Config.setWifiState(true);
                	UploadTask.startUploadTaskThread();
            	}
            	break;
            default:
            	Config.setWifiState(false);
            	return;
            }
        }else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                State state = networkInfo.getState();
                boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
                if (isConnected) {
                	ConnectivityManager connManager = (ConnectivityManager) Config.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
                	NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
                	if (mWifi.isConnected()) {  
                    	Config.setWifiState(true);
                    	UploadTask.startUploadTaskThread();
                	}
                } else {
                	Config.setWifiState(false);
                }
            }
        }
	}
}
