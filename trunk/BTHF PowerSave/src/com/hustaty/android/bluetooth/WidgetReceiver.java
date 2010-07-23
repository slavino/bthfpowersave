package com.hustaty.android.bluetooth;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class WidgetReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = WidgetReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "#onReceive()");
		
		//load SharedPreferences on receiving specific actions
		SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_PRIVATE);
		WidgetConfigurationHolder.loadPreferences(settings);
		
		//listen to CALL_STATE changes
		Log.d(LOG_TAG, "#onReceive(): determinig whether service is running service");
		if(!NotificationService.isRunning(context)) {
			NotificationService.start(context);
			Log.d(LOG_TAG, "starting service");
		}
		
	}

}
