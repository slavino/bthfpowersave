package com.hustaty.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

public class BluetoothAdapterUtil {

	// logger entry
	private final static String LOG_TAG = BluetoothAdapterUtil.class.getSimpleName();

	public static void startBluetoothAdapter() {
		try {
			waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON);
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}
	}
	
	public static void stopBluetoothAdapter() {		
		try {
			waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF);
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}
	}
	
	/**
	 * waits until BluetoothAdapter is in required state
	 * 
	 * @param state
	 * @throws Exception
	 */
	private static void waitUntilBluetoothAdapterIsInState(int state) throws Exception {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		switch (state) {
		case BluetoothAdapter.STATE_OFF:
			if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
				waitNMillis(200);
				waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF);
			} else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
				Log.d(LOG_TAG, "BluetoothAdapter is in state OFF");
				return;
			} else {
				// ensure we're not waiting for Godot ;)
				bluetoothAdapter.disable();
				waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF);
			}
			break;
		case BluetoothAdapter.STATE_ON:
			if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON) {
				waitNMillis(200);
				waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON);
			} else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
				Log.d(LOG_TAG, "BluetoothAdapter is in state ON");
				return;
			} else {
				// ensure we're not waiting for Godot ;)
				bluetoothAdapter.enable();
				waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON);
			}
			break;
		default:
			throw new Exception(
					"You can check only final states of BluetoothAdapter(STATE_ON|STATE_OFF).");
		}
	}

	/**
	 * delay N milliseconds
	 * 
	 * @param n
	 */
	private static void waitNMillis(long n) {
		long t = System.currentTimeMillis();
		while (n > System.currentTimeMillis() - t) {
		}
	}

}
