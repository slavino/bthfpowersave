/*
 *	This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.hustaty.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author Slavomir Hustaty
 *
 */
public class BluetoothAdapterUtil {

	// logger entry
	private final static String LOG_TAG = BluetoothAdapterUtil.class.getSimpleName();

	private static final int LOOP_WAIT_TIME = 500;
	
	private static final int MAX_REPETITIONS_COUNT = 30;
	
	public static void startBluetoothAdapter() {
		try {
			waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON, MAX_REPETITIONS_COUNT);
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}
	}
	
	public static void stopBluetoothAdapter() {		
		try {
			waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF, MAX_REPETITIONS_COUNT);
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
	private static void waitUntilBluetoothAdapterIsInState(int state, int remainingLoops) throws Exception {
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(remainingLoops > 0) {
			switch (state) {
			case BluetoothAdapter.STATE_OFF:
				if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
					waitNMillis(LOOP_WAIT_TIME);
					waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF, remainingLoops - 1);
				} else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
					Log.d(LOG_TAG, "BluetoothAdapter is in state OFF");
					return;
				} else {
					// ensure we're not waiting for Godot ;)
					bluetoothAdapter.disable();
					waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_OFF, remainingLoops - 1);
				}
				break;
			case BluetoothAdapter.STATE_ON:
				if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON) {
					waitNMillis(LOOP_WAIT_TIME);
					waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON, remainingLoops - 1);
				} else if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
					Log.d(LOG_TAG, "BluetoothAdapter is in state ON");
					return;
				} else {
					// ensure we're not waiting for Godot ;)
					bluetoothAdapter.enable();
					waitUntilBluetoothAdapterIsInState(BluetoothAdapter.STATE_ON, remainingLoops - 1);
				}
				break;
			default:
				throw new Exception(
						"You can check only final states of BluetoothAdapter(STATE_ON|STATE_OFF).");
			}
		} else {
			Log.e(LOG_TAG, "Error on waiting while BluetoothAdapter changes state to #" + state + ". ");
			return;
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
			// :-) 
			n++;
			n--;
		}
	}

}
