package com.hustaty.android.bluetooth;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NotificationService extends Service {

	// logger entry
	private final static String LOG_TAG = NotificationService.class.getSimpleName();
	
	private Handler instanceHandler;

	private final PhoneStateListener ringListener = new MyPhoneStateListener(this);

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Log.i(LOG_TAG, "Starting notification service");
		instanceHandler = new Handler();


		final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// Register the ring listener
		tm.listen(ringListener, PhoneStateListener.LISTEN_CALL_STATE);

	}

	public static boolean isRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo serviceInfo : services) {
			ComponentName componentName = serviceInfo.service;
			String serviceName = componentName.getClassName();
			if (serviceName.equals(NotificationService.class.getName())) {
				return true;
			}
		}

		return false;
	}

	public static void start(Context context) {
		context.startService(new Intent(context, NotificationService.class));
	}

	public static void stop(Context context) {
		context.stopService(new Intent(context, NotificationService.class));
	}

}
