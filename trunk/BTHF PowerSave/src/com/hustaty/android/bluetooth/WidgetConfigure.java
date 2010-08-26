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
	
	//menu option Save
//	private static final int MENU_SAVE = 0;

	/**
	 * on create configuration activity 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);
		Bundle extras = getIntent().getExtras();

		SharedPreferences settings = getSharedPreferences(WidgetConfigure.PREFS_NAME, MODE_PRIVATE);
	    
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
                
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();

                //set service ON/OFF
                ToggleButton switchOffServiceToggleButton = (ToggleButton) findViewById(R.id.switchOffService);
                editor.putBoolean(WidgetConfigurationHolder.ENABLED, switchOffServiceToggleButton.isChecked());
                
                ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
                editor.putBoolean(WidgetConfigurationHolder.SWITCH_OFF_BT_AFTER_CALL_ENDED, switchOffBTAfterCallEndedToggleButton.isChecked());

                ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
                editor.putBoolean(WidgetConfigurationHolder.PROCESS_OUTGOING_CALLS, processOutgoingCallsToggleButton.isChecked());

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
