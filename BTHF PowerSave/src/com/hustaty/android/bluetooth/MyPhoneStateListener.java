package com.hustaty.android.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyPhoneStateListener extends PhoneStateListener {

	// logger entry
	private final static String LOG_TAG = MyPhoneStateListener.class.getSimpleName();

    //well known UUID :)                           
//    private static final String BT_SERVICE_UUID = "00001101-0000-100­0-8000-00805F9B34FB";

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
	
//  /**
//  * perform connect to device - don't rely on platform anymore
//  */
// private void connectToDevice() {
//         if(bluetoothAdapter.isDiscovering()) {
//                 bluetoothAdapter.cancelDiscovery();
//         }
//         Set<BluetoothDevice> btDevices = bluetoothAdapter.getBondedDevices();
//         for(BluetoothDevice btDevice : btDevices) {
//                 if(btDevice.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
//                         
//                 		BluetoothSocket btSocket = null;
//                         
//                         try {
//                                 Log.d(LOG_TAG, "Trying to connect to: " + btDevice.getName());
//                                 bluetoothAdapter.getRemoteDevice(btDevice.getAddress());
//                                 btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(BT_SERVICE_UUID));
//                                 btSocket.connect();
//                                 //successfully connected to remote device
//                                 return;
//                         } catch(IOException e) {
//                                 Log.e(LOG_TAG, "Message: " + e.getMessage() + "\nCaused by: " + e.getCause().getMessage());
//                                 try {
//                                         btSocket.close();
//                                 } catch (IOException ex) {
//                                         Log.e(LOG_TAG, "Couldn't close BluetoothSocket.");
//                                 }
//                         }
//                 }
//         }
// }
//
	
}
