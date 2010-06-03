package com.hustaty.android.bluetooth;

import android.content.SharedPreferences;
import android.util.Log;

public class WidgetConfigurationHolder {

	//logging support
	private static final String LOG_TAG = WidgetConfigurationHolder.class.getName();
	
	//Application main state, deafult enabled to false
	private static boolean enabled = false;

	private static boolean switchOffBTAfterCallEnded = false; 
	
	public static final String SWITCH_OFF_BT_AFTER_CALL_ENDED = "switchOffBTAfterCallEnded";
	public static final String ENABLED = "enabled";
	
	private static WidgetConfigurationHolder instance = new WidgetConfigurationHolder();
	
	private WidgetConfigurationHolder() {
	}
	
	public static WidgetConfigurationHolder getInstance() {
		return instance;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		WidgetConfigurationHolder.enabled = enabled;
	}
	
	public static boolean isSwitchOffBTAfterCallEnded() {
		return switchOffBTAfterCallEnded;
	}

	public static void setSwitchOffBTAfterCallEnded(boolean switchOffBTAfterCallEnded) {
		WidgetConfigurationHolder.switchOffBTAfterCallEnded = switchOffBTAfterCallEnded;
	}

	/**
	 * load configuration from SharedPreferences
	 * @param settings
	 */
	public void loadPreferences(SharedPreferences settings) {
		
		//get toggle button stored value
		boolean switchOffBTAfterCallEnded = settings.getBoolean(WidgetConfigurationHolder.SWITCH_OFF_BT_AFTER_CALL_ENDED, true);
	    WidgetConfigurationHolder.setSwitchOffBTAfterCallEnded(switchOffBTAfterCallEnded);

	    //global on/off
	    boolean stateEnabled = settings.getBoolean(WidgetConfigurationHolder.ENABLED, false);
	    WidgetConfigurationHolder.setEnabled(stateEnabled);
		
	    Log.d(LOG_TAG, "Loaded configuration from SheredPreferences: " + getInstance().toString());
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder();
		sb.append("{" + ENABLED + ":" + enabled + "; ");
		sb.append(SWITCH_OFF_BT_AFTER_CALL_ENDED + ":" + switchOffBTAfterCallEnded + ";}");
		return sb.toString();
	}
	
}
