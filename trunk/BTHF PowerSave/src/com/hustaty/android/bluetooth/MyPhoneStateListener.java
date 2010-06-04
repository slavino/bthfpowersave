package com.hustaty.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

	// logger entry
	private final static String LOG_TAG = MyPhoneStateListener.class.getName();

	private BluetoothAdapter bluetoothAdapter;
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				
		if(bluetoothAdapter==null) {
			Log.e(LOG_TAG, "BluetoothAdapter is not available.");
		}
		
		String stateString = "N/A";

		if(WidgetConfigurationHolder.isEnabled() && bluetoothAdapter!=null) {
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					stateString = "Idle";
					if(WidgetConfigurationHolder.isSwitchOffBTAfterCallEnded()) {
						bluetoothAdapter.disable();
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
					if (!bluetoothAdapter.isEnabled() && WidgetConfigurationHolder.isProcessOutgoingCalls()) {
						bluetoothAdapter.enable();
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stateString = "Ringing";
					if (!bluetoothAdapter.isEnabled()) {
						bluetoothAdapter.enable();
						//connectToDevice();
					}
					break;
			}
			Log.d(LOG_TAG, String.format("\nonCallStateChanged: %s , %s", stateString, incomingNumber));
		} 

		super.onCallStateChanged(state, incomingNumber);
	}
	
}
