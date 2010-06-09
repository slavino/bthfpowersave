package com.hustaty.android.bluetooth;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class WidgetReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = WidgetReceiver.class.getName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "#onReceive()");
		
		//load SharedPreferences on receiving specific actions
		SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_PRIVATE);
		WidgetConfigurationHolder.loadPreferences(settings);
		
		TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		
		//listen to CALL_STATE changes
		telephonyManager.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
		
	}

}
