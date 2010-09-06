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
public class ConfigurationActivity extends Activity {

	// logger entry
	private final static String LOG_TAG = ConfigurationActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_activity);

		SharedPreferences settings = getSharedPreferences(WidgetConfigure.PREFS_NAME, MODE_WORLD_WRITEABLE);
	    
		WidgetConfigurationHolder.loadPreferences(settings);
		
		ToggleButton switchOffServiceToggleButton = (ToggleButton) findViewById(R.id.switchOffService);
		switchOffServiceToggleButton.setChecked(WidgetConfigurationHolder.getInstance(getApplicationContext()).isEnabled());
		
		//get toggle button stored value - turn off after call ended
	    ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
	    switchOffBTAfterCallEndedToggleButton.setChecked(WidgetConfigurationHolder.getInstance(getApplicationContext()).isSwitchOffBTAfterCallEnded());

		//get toggle button stored value - turn off after call ended
	    ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
	    processOutgoingCallsToggleButton.setChecked(WidgetConfigurationHolder.getInstance(getApplicationContext()).isProcessOutgoingCalls());
	    
	    Button saveSettingsButton = (Button) findViewById(R.id.config_save_button);
	    saveSettingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                SharedPreferences settings = getSharedPreferences(WidgetConfigure.PREFS_NAME, MODE_WORLD_WRITEABLE);
                SharedPreferences.Editor editor = settings.edit();
                
                //set service ON/OFF
                ToggleButton switchOffServiceToggleButton = (ToggleButton) findViewById(R.id.switchOffService);
                editor.putBoolean(WidgetConfigurationHolder.ENABLED, switchOffServiceToggleButton.isChecked());
                
                //set switch off after call ended
                ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
                editor.putBoolean(WidgetConfigurationHolder.SWITCH_OFF_BT_AFTER_CALL_ENDED, switchOffBTAfterCallEndedToggleButton.isChecked());

                //set ON/OFF processing outgoing calls
                ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
                editor.putBoolean(WidgetConfigurationHolder.PROCESS_OUTGOING_CALLS, processOutgoingCallsToggleButton.isChecked());

                // Commit the edits!
                editor.commit();

                //load latest settings
                WidgetConfigurationHolder.loadPreferences(settings);
                
                Intent configSavedIntent = new Intent(WidgetConfigure.CONFIG_SAVED);
                configSavedIntent.putExtra(WidgetConfigure.PERFORM_SHAREDPREFERECES_EDIT, false);
                sendBroadcast(configSavedIntent);
                
                Log.d(LOG_TAG, android.os.Build.VERSION.RELEASE + "|" + android.os.Build.MODEL);
                
				finish();
			}
		});
	}

}
