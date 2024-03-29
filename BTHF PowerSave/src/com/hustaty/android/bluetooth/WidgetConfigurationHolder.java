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
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 
 * @author Slavomir Hustaty
 *
 */
public class WidgetConfigurationHolder {

	//logging support
	private static final String LOG_TAG = WidgetConfigurationHolder.class.getSimpleName();
	
	//Application main state
	private Boolean enabled;

	//whether switch off BT after call ended
	private Boolean switchOffBTAfterCallEnded;
	
	//whether process outgoing calls
	private Boolean processOutgoingCalls; 
	
	//force BT connection from AudioManager
	private Boolean forceBTConnection;
	
	//context of application
	private static Context context;
	
	public static final String SWITCH_OFF_BT_AFTER_CALL_ENDED = "switchOffBTAfterCallEnded";
	public static final String PROCESS_OUTGOING_CALLS = "processOutgoingCalls";
	public static final String ENABLED = "enabled";
	public static final String FORCE_BT = "forceBTConnection";
	
	//application configuration instance
	private static WidgetConfigurationHolder instance;
	
	private WidgetConfigurationHolder(Context context) {
		this.enabled = false;
		this.switchOffBTAfterCallEnded = true;
		this.processOutgoingCalls = true;
		this.forceBTConnection = false;
		WidgetConfigurationHolder.context = context;
	}
	
	/**
	 * returning instance
	 * @return
	 */
	public static WidgetConfigurationHolder getInstance(Context context) {
		if(instance == null) {
			instance = new WidgetConfigurationHolder(context);
			if(context != null) {
				SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_WORLD_WRITEABLE);
				loadPreferences(settings);
			} 
		}
		return instance;
	}
	
	/**
	 * getter of enabled - application state
	 * @return
	 */
	public Boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * setter of enabled - holding state of entire application
	 * @param enabled
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setEnabled(Context context, Boolean enabled) {
		setEnabled(enabled);
		this.storePreference(context, ENABLED, enabled);
		if(enabled && !NotificationService.isRunning(context)) {
			NotificationService.start(context);
		} else if(!enabled && NotificationService.isRunning(context)) {
			NotificationService.stop(context);			
		}

	}
	
	public Boolean isSwitchOffBTAfterCallEnded() {
		return instance.switchOffBTAfterCallEnded;
	}

	public void setSwitchOffBTAfterCallEnded(Boolean switchOffBTAfterCallEnded) {
		this.switchOffBTAfterCallEnded = switchOffBTAfterCallEnded;
	}

	public Boolean isProcessOutgoingCalls() {
		return this.processOutgoingCalls;
	}

	public void setProcessOutgoingCalls(Boolean processOutgoingCalls) {
		this.processOutgoingCalls = processOutgoingCalls;
	}
	
	public Boolean isForceBTConnection() {
		return forceBTConnection;
	}

	public void setForceBTConnection(Boolean forceBTConnection) {
		this.forceBTConnection = forceBTConnection;
	}

	public static void loadPreferences() {
		SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_WORLD_WRITEABLE);
		loadPreferences(settings);
	}
	
	/**
	 * load configuration from SharedPreferences
	 * @param settings
	 */
	public static void loadPreferences(SharedPreferences settings) {
		
		//get toggle button stored value
		getInstance(context)
				.setSwitchOffBTAfterCallEnded(
						settings.getBoolean(
								WidgetConfigurationHolder.SWITCH_OFF_BT_AFTER_CALL_ENDED,
								Boolean.TRUE));

	    //get toggle button stored value - process outgoing calls
		getInstance(context).setProcessOutgoingCalls(
				settings.getBoolean(
						WidgetConfigurationHolder.PROCESS_OUTGOING_CALLS,
						Boolean.TRUE));
		
	    //global on/off
		getInstance(context).setEnabled(
				settings.getBoolean(WidgetConfigurationHolder.ENABLED,
						Boolean.FALSE));

		//force BT connection
		getInstance(context).setForceBTConnection(
				settings.getBoolean(WidgetConfigurationHolder.FORCE_BT,
						Boolean.FALSE));

	    Log.d(LOG_TAG, "Loaded configuration from SharedPreferences: " + getInstance(context).toString());
	}
	
	/**
	 * auto store values
	 * @param context
	 * @param name
	 * @param value
	 */
	private void storePreference(Context context, String name, Object value) {
		if(context != null) {
			SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editor = settings.edit();
			if(value instanceof Boolean) {
	            editor.putBoolean(name, (Boolean)value);
			}
            editor.commit();
		}
	}
	
	/**
	 * overriding toString method
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName() + " {" + ENABLED + ":" + enabled + "; ");
		sb.append(SWITCH_OFF_BT_AFTER_CALL_ENDED + ":" + switchOffBTAfterCallEnded + "; ");
		sb.append(PROCESS_OUTGOING_CALLS + ":" + processOutgoingCalls + ";");
		sb.append(FORCE_BT + ":" + forceBTConnection + ";}");
		return sb.toString();
	}

}
