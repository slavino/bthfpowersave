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

	// logger entry
	private final static String LOG_TAG = MyPhoneStateListener.class.getSimpleName();

    //well known UUID :)                           
    private static final String BT_SERVICE_UUID = "00001101-0000-100­0-8000-00805F9B34FB";

	//reference to Bluetooth Adapter
	private BluetoothAdapter bluetoothAdapter;
	
	private final NotificationService service;

	public MyPhoneStateListener(NotificationService context) {
		this.service = context;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

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
//					try {  
//                        waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON);
//	                } catch (Exception e) {
//                        Log.e(LOG_TAG, e.getMessage());
//	                }
	                //connectToDevice();

					break;
			}

			if(Log.isLoggable(LOG_TAG, Log.DEBUG)) {
				Log.d(LOG_TAG, String.format("\nonCallStateChanged: %s , %s", stateString, incomingNumber));
			}
			
		}
		
		if(Log.isLoggable(LOG_TAG, Log.DEBUG)) {
			Log.d(LOG_TAG, "Incoming call from:" + incomingNumber);
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
     * waits until BluetoothAdapter is in required state 
     * @param state
     * @throws Exception
     */
    private void waitUntilBluetoothAdapterIsInState(int state) throws Exception {
            switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                            if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
                                    waitNMillis(200);
                                    waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF);
                            } else if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                                    Log.d(LOG_TAG, "BluetoothAdapter is in state OFF");
                                    return;
                            } else {
                                    //ensure we're not waiting for Godot ;)
                                    bluetoothAdapter.disable();
                                    waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF);
                            }
                            break;
                    case BluetoothAdapter.STATE_ON:
                            if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON) {
                                    waitNMillis(200);
                                    waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON);
                            } else if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                                    Log.d(LOG_TAG, "BluetoothAdapter is in state ON");
                                    return;
                            } else {
                                    //ensure we're not waiting for Godot ;)
                                    bluetoothAdapter.enable();
                                    waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON);
                            }
                            break;
                    default: 
                            throw new Exception("You can check only final states of BluetoothAdapter(STATE_ON|STATE_OFF).");
            }
    }
    
    /**
     * delay N milliseconds
     * @param n
     */
    private void waitNMillis(long n) {
            long t = System.currentTimeMillis();
            while(n > System.currentTimeMillis() - t){}
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
