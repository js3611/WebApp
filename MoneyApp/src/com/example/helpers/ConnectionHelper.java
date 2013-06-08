package com.example.helpers;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

public class ConnectionHelper extends Activity {
	public static boolean checkNetworkConnection(ConnectivityManager connMgr) {
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
}
