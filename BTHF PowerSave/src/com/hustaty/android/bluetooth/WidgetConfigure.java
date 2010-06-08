package com.hustaty.android.bluetooth;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ToggleButton;

public class WidgetConfigure extends Activity {

	//logging tag
	private static final String LOG_TAG = WidgetConfigure.class.getName();

	//preferences name
	public static final String PREFS_NAME = "BTHFPreferencesFile";

	//action to be caught
	public static final String CONFIG_SAVED = "com.hustaty.android.bluetooth.action.CONFIG_SAVED";
	
	//click action to be caught - click on widget icon
	public static final String WIDGET_CLICK = "com.hustaty.android.bluetooth.action.CLICK";

	//initializing appWidgetId
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	//menu option Save
	private static final int MENU_SAVE = 0;

	/**
	 * on create configuration activity 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configure);
		Bundle extras = getIntent().getExtras();

		SharedPreferences settings = getSharedPreferences(WidgetConfigure.PREFS_NAME, MODE_PRIVATE);
	    
		WidgetConfigurationHolder.getInstance().loadPreferences(settings);
		
		//get toggle button stored value - turn off after call ended
	    ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
	    switchOffBTAfterCallEndedToggleButton.setChecked(WidgetConfigurationHolder.getInstance().isSwitchOffBTAfterCallEnded());

		//get toggle button stored value - turn off after call ended
	    ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
	    processOutgoingCallsToggleButton.setChecked(WidgetConfigurationHolder.getInstance().isProcessOutgoingCalls());

	    if (extras != null) {
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		setResult(RESULT_CANCELED);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.add(0, MENU_SAVE, 0, getResources().getString(R.string.save_config));
		menuItem.setIcon(R.drawable.icon);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
			switch (item.getItemId()) {
			case MENU_SAVE:
				Intent configSavedIntent = new Intent();
				configSavedIntent.setAction(WidgetConfigure.CONFIG_SAVED);
				configSavedIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				sendBroadcast(configSavedIntent);
				
				if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    Log.d(LOG_TAG, "Saving configuration." + WidgetConfigurationHolder.getInstance().toString());
                    
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();

                    ToggleButton switchOffBTAfterCallEndedToggleButton = (ToggleButton) findViewById(R.id.switchOffBTAfterCallEndedToggleButton);
                    editor.putBoolean(WidgetConfigurationHolder.SWITCH_OFF_BT_AFTER_CALL_ENDED, switchOffBTAfterCallEndedToggleButton.isChecked());

                    ToggleButton processOutgoingCallsToggleButton = (ToggleButton) findViewById(R.id.processOutgoingCallsToggleButton);
                    editor.putBoolean(WidgetConfigurationHolder.PROCESS_OUTGOING_CALLS, processOutgoingCallsToggleButton.isChecked());

                    editor.putBoolean(WidgetConfigurationHolder.ENABLED, WidgetConfigurationHolder.getInstance().isEnabled());

                    // Commit the edits!
                    editor.commit();
                }
                finish();
				return true;
			}
		}
		return false;
	}

}
