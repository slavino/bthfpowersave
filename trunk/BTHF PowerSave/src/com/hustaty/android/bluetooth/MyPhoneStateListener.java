package com.hustaty.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

	// logger entry
	private final static String LOG_TAG = MyPhoneStateListener.class.getName();

	//reference to Bluetooth Adapter
	private BluetoothAdapter bluetoothAdapter;
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				
		if(bluetoothAdapter == null) {
			Log.e(LOG_TAG, "BluetoothAdapter is not available.");
		}
		
		String stateString = "N/A";

		if(WidgetConfigurationHolder.getInstance().isEnabled() && bluetoothAdapter!=null) {
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					stateString = "Idle";
					if(WidgetConfigurationHolder.getInstance().isSwitchOffBTAfterCallEnded()) {
						bluetoothAdapter.disable();
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					stateString = "Off Hook";
					if (!bluetoothAdapter.isEnabled() 
							&& WidgetConfigurationHolder.getInstance().isProcessOutgoingCalls()
							&& isOutgoingCall(incomingNumber)) {
						bluetoothAdapter.enable();
					}
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					stateString = "Ringing";
					if (!bluetoothAdapter.isEnabled()) {
						bluetoothAdapter.enable();
					}
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
}
