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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 
 * @author Slavomir Hustaty
 *
 */
public class WidgetReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = WidgetReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "#onReceive()");
		
		//load SharedPreferences on receiving specific actions
		SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_WORLD_WRITEABLE);
		WidgetConfigurationHolder.loadPreferences(settings);
		
		//listen to CALL_STATE changes
		Log.d(LOG_TAG, "#onReceive(): determinig whether service is running");
		if (!NotificationService.isRunning(context)
				&& settings
						.getBoolean(
								WidgetConfigurationHolder.ENABLED,
								Boolean.FALSE)) {
			NotificationService.start(context);
			Log.d(LOG_TAG, "starting service");
		}
		
	}

}
