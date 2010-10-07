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
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

/**
 * 
 * @author Slavomir Hustaty
 *
 */
public class WidgetConfigure extends Activity {

	//logging tag
	private static final String LOG_TAG = WidgetConfigure.class.getSimpleName();

	//preferences name
	public static final String PREFS_NAME = "BTHFPreferencesFile";

	//action to be caught
	public static final String CONFIG_SAVED = "com.hustaty.android.bluetooth.action.CONFIG_SAVED";
	public static final String PERFORM_SHAREDPREFERECES_EDIT = "performUpdate";
	
	//click action to be caught - click on widget icon
	public static final String WIDGET_CLICK = "com.hustaty.android.bluetooth.action.CLICK";

	//initializing appWidgetId
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	/**
	 * on create configuration activity 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);
		Bundle extras = getIntent().getExtras();

		SharedPreferences settings = getSharedPreferences(WidgetConfigure.PREFS_NAME, MODE_WORLD_WRITEABLE);
	    
		WidgetConfigurationHolder.loadPreferences(settings);
		
        //set service ON/OFF
        ToggleButton switchOffServiceToggleButton = (ToggleButton) findViewById(R.id.switchOffService);
        switchOffServiceToggleButton.setChecked(WidgetConfigurationHolder.getInstance(getApplicationContext()).isEnabled());

		
		//get toggle button stored value - turn off after call ended
	    ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
	    switchOffBTAfterCallEndedToggleButton.setChecked(WidgetConfigurationHolder.getInstance(getApplicationContext()).isSwitchOffBTAfterCallEnded());

		//get toggle button stored value - turn off after call ended
	    ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
	    processOutgoingCallsToggleButton.setChecked(WidgetConfigurationHolder.getInstance(getApplicationContext()).isProcessOutgoingCalls());

	    //force BT connection
	    ToggleButton forceBTConnectionToggleButton = (ToggleButton) findViewById(R.id.forceBTConnectionToggleButton);
	    forceBTConnectionToggleButton.setChecked(WidgetConfigurationHolder.getInstance(getApplicationContext()).isForceBTConnection());

	    if (extras != null) {
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		setResult(RESULT_CANCELED);
		
	    Button saveSettingsButton = (Button) findViewById(R.id.config_save_button);
	    saveSettingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if(Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                	Log.d(LOG_TAG, "Saving configuration." + WidgetConfigurationHolder.getInstance(getApplicationContext()).toString());
                }
                
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor editor = settings.edit();

                //set service ON/OFF
                ToggleButton switchOffServiceToggleButton = (ToggleButton) findViewById(R.id.switchOffService);
                editor.putBoolean(WidgetConfigurationHolder.ENABLED, switchOffServiceToggleButton.isChecked());
                
                ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
                editor.putBoolean(WidgetConfigurationHolder.SWITCH_OFF_BT_AFTER_CALL_ENDED, switchOffBTAfterCallEndedToggleButton.isChecked());

                ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
                editor.putBoolean(WidgetConfigurationHolder.PROCESS_OUTGOING_CALLS, processOutgoingCallsToggleButton.isChecked());

                //set ON/OFF processing outgoing calls
                ToggleButton forceBTConnectionToggleButton = (ToggleButton) findViewById(R.id.forceBTConnectionToggleButton);
                editor.putBoolean(WidgetConfigurationHolder.FORCE_BT, forceBTConnectionToggleButton.isChecked());

                // Commit the edits!
                editor.commit();
                
                WidgetConfigurationHolder.loadPreferences(settings);
                
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                setResult(RESULT_OK, resultValue);
                sendBroadcast(resultValue);

                Intent configSavedIntent = new Intent(WidgetConfigure.CONFIG_SAVED);
                configSavedIntent.putExtra(WidgetConfigure.PERFORM_SHAREDPREFERECES_EDIT, false);
                sendBroadcast(configSavedIntent);
                
                finish();
			}
		});


	}

}
