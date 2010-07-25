package com.hustaty.android.bluetooth;

import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class NotificationService extends Service {

	// logger entry
	private final static String LOG_TAG = NotificationService.class.getSimpleName();
	
	private Handler instanceHandler;

	private final PhoneStateListener ringListener = new MyPhoneStateListener(this);

	/**
	 * onbind
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * service start
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Log.i(LOG_TAG, "Starting notification service");
		instanceHandler = new Handler();
		

		final TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// Register the ring listener
		tm.listen(ringListener, PhoneStateListener.LISTEN_CALL_STATE);

		Thread t = new Thread() {
			@Override
			public synchronized void start() {
				super.start();
				//notify user if there is any problem
				checkBluetoothDevices();
			}
		};

		t.start();
		
	}

	/**
	 * destroying service
	 */
	@Override
	public void onDestroy() {
		Toast.makeText(this.getApplicationContext(), R.string.toastTextAfterDeletingWidget, Toast.LENGTH_LONG).show();
		super.onDestroy();
	}

	/**
	 * checks current state stopped/started
	 * @param context
	 * @return
	 */
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

	/**
	 * starts the service
	 * @param context
	 */
	public static void start(Context context) {
		context.startService(new Intent(context, NotificationService.class));
		Log.i(LOG_TAG, "Sending intent to start notification service");
	}

	/**
	 * stops the service
	 * @param context
	 */
	public static void stop(Context context) {
		context.stopService(new Intent(context, NotificationService.class));
		Log.i(LOG_TAG, "Sending intent to stop notification service");
	}

	private void checkBluetoothDevices() {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(bluetoothAdapter != null) {
			boolean btStateBeforeCheck = bluetoothAdapter.isEnabled();
			
			if(!btStateBeforeCheck) {
				BluetoothAdapterUtil.startBluetoothAdapter();		
			}

			Set<BluetoothDevice> btDevices = bluetoothAdapter.getBondedDevices();
			for (BluetoothDevice btDevice : btDevices) {
				
				Log.d(LOG_TAG, "checking device " + btDevice.getName());

				switch(btDevice.getBluetoothClass().getDeviceClass()) {
				case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
				case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE:
				case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO:
				case 1036: //magic number - Jabra BT 160 fix
					return;
				}
			}
			
			if(!btStateBeforeCheck) {
				BluetoothAdapterUtil.stopBluetoothAdapter();		
			}
			
		} else {
			//something is wrong with BTadapter
			Toast.makeText(this.getApplicationContext(), R.string.toastTextOnBTAdapterNotReachable, Toast.LENGTH_LONG).show();
			return;
		}
		
		//report that no device was found
		Toast.makeText(this.getApplicationContext(), R.string.toastTextOnBTDeviceNotPaired, Toast.LENGTH_LONG).show();
	}
}
