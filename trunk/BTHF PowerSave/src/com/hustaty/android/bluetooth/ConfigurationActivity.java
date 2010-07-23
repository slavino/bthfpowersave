package com.hustaty.android.bluetooth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ToggleButton;

public class ConfigurationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config_activity);

		SharedPreferences settings = getSharedPreferences(WidgetConfigure.PREFS_NAME, MODE_PRIVATE);
	    
		WidgetConfigurationHolder.loadPreferences(settings);
		ToggleButton switchOffServiceToggleButton = (ToggleButton) findViewById(R.id.switchOffService);
		switchOffServiceToggleButton.setChecked(WidgetConfigurationHolder.getInstance().isEnabled());
		
		//get toggle button stored value - turn off after call ended
	    ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
	    switchOffBTAfterCallEndedToggleButton.setChecked(WidgetConfigurationHolder.getInstance().isSwitchOffBTAfterCallEnded());

		//get toggle button stored value - turn off after call ended
	    ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
	    processOutgoingCallsToggleButton.setChecked(WidgetConfigurationHolder.getInstance().isProcessOutgoingCalls());
	    
	    Button saveSettingsButton = (Button) findViewById(R.id.config_save_button);
	    saveSettingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
                SharedPreferences settings = getSharedPreferences(WidgetConfigure.PREFS_NAME, MODE_PRIVATE);
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
                Intent configSavedIntent = new Intent(WidgetConfigure.CONFIG_SAVED);
                configSavedIntent.putExtra(WidgetConfigure.PERFORM_SHAREDPREFERECES_EDIT, false);
                sendBroadcast(configSavedIntent);
                
				finish();
			}
		});
	}

}
