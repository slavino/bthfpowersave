package com.hustaty.android.bluetooth;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

	private static final String BT_SERVICE_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	
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
						//FIXME small hack - to ensure phone's BT module is already active  
						waitNMillis(1200);
						connectToDevice();
					}
					break;
			}
			Log.d(LOG_TAG, String.format("\nonCallStateChanged: %s , %s", stateString, incomingNumber));
		} 

		super.onCallStateChanged(state, incomingNumber);
	}
	
	/**
	 * perform connect to device - don't rely on platform anymore
	 */
	private void connectToDevice() {
		if(bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
		Set<BluetoothDevice> btDevices = bluetoothAdapter.getBondedDevices();
		for(BluetoothDevice btDevice : btDevices) {
			if(btDevice.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
				BluetoothSocket btSocket = null;
				try {
					Log.d(LOG_TAG, "Trying to connect to: " + btDevice.getName());
					bluetoothAdapter.getRemoteDevice(btDevice.getAddress());
					btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(BT_SERVICE_UUID));
					btSocket.connect();
					//successfully connected to remote device
					return;
				} catch(IOException e) {
					Log.e(LOG_TAG, "Message: " + e.getMessage() + "\nCaused by: " + e.getCause().getMessage());
					try {
						btSocket.close();
					} catch (IOException ex) {
						Log.e(LOG_TAG, "Couldn't close BluetoothSocket.");
					}
				}
			}
		}
	}
	
	/**
	 * to create Lag 
	 * needs to be fixed
	 * @param n
	 */
	private void waitNMillis(long n) {
		long t = System.currentTimeMillis();
		while(n > System.currentTimeMillis() - t) {
		}
	}
}
