package com.hustaty.android.bluetooth;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

	// logger entry
	private final static String LOG_TAG = MyPhoneStateListener.class.getSimpleName();

	//reference to Bluetooth Adapter
	private BluetoothAdapter bluetoothAdapter;
	
	private final NotificationService service;

	public MyPhoneStateListener(NotificationService context) {
		this.service = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

		SharedPreferences settings = service.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_PRIVATE);
		WidgetConfigurationHolder.loadPreferences(settings);
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				
		if(bluetoothAdapter == null) {
			if(Log.isLoggable(LOG_TAG, Log.ERROR)) {
				Log.e(LOG_TAG, "BluetoothAdapter is not available.");
			}
		}
		
		String stateString = "N/A";

		if(WidgetConfigurationHolder.getInstance().isEnabled() && bluetoothAdapter!=null) {
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					stateString = "Idle";
					if(WidgetConfigurationHolder.getInstance().isSwitchOffBTAfterCallEnded()) {
						BluetoothAdapterUtil.stopBluetoothAdapter();
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
					if (!bluetoothAdapter.isEnabled() 
							&& WidgetConfigurationHolder.getInstance().isProcessOutgoingCalls()
							&& isOutgoingCall(incomingNumber)) {
						BluetoothAdapterUtil.startBluetoothAdapter();
						connectToDevice();
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stateString = "Ringing";
					if (!bluetoothAdapter.isEnabled()) {
						BluetoothAdapterUtil.startBluetoothAdapter();
					}
					connectToDevice();
					break;
			}

			Log.d(LOG_TAG, String.format("\nonCallStateChanged: %s , %s", stateString, incomingNumber));
			
		}
		
		Log.d(LOG_TAG, "Incoming call from:" + incomingNumber);
		
		super.onCallStateChanged(state, incomingNumber);
	}
	
	/**
	 * method to check whether call is outgoing - based on incomingNumber information
	 * @param incomingNumber
	 * @return
	 */
	private boolean isOutgoingCall(String incomingNumber) {
		if("".equals(incomingNumber) || incomingNumber==null) {
			return true;
		}
		//FIXME what if anonymous call is being processed? CLIR - needs to be tested on real device -> Answer: TelephonyManager.CALL_STATE_RINGING might do the stuff
		return false;
	}
	
	/**
	 * perform connect to device - don't rely on platform anymore
	 */
	private void connectToDevice() {
		AudioManager audioManager = (AudioManager) service.getSystemService(Context.AUDIO_SERVICE);
		if(audioManager.isBluetoothA2dpOn() || audioManager.isBluetoothScoOn() || audioManager.isWiredHeadsetOn()) {
			//do nothing 
		} else {
			if (bluetoothAdapter.isDiscovering()) {
				Log.d(LOG_TAG, "Cancelling discovery");
				bluetoothAdapter.cancelDiscovery();
			}
			Log.d(LOG_TAG, "Audio Manager: requiring #setBluetoothScoOn(true)");
			audioManager.setBluetoothScoOn(true);
		}
	}
	
}
