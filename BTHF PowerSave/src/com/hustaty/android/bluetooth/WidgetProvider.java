package com.hustaty.android.bluetooth;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider {

	//logging support
	public static final String LOG_TAG = WidgetProvider.class.getName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.widget);
		appWidgetManager.updateAppWidget(appWidgetIds, updateView);

		Intent clickIntent = new Intent(WidgetConfigure.WIDGET_CLICK);
		
		WidgetConfigurationHolder.loadPreferences(
				context.getSharedPreferences(WidgetConfigure.PREFS_NAME,
						Activity.MODE_PRIVATE));

		if (WidgetConfigurationHolder.getInstance().isEnabled()) {
			updateView.setImageViewResource(R.id.imagebutton, R.drawable.on);
		} else {
			updateView.setImageViewResource(R.id.imagebutton, R.drawable.off);			
		}
		
		PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, clickIntent, 0);
		updateView.setOnClickPendingIntent(R.id.imagebutton, pendingIntentClick);
		appWidgetManager.updateAppWidget(appWidgetIds, updateView);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	/**
	 * on deleting widget from home screen
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		
		//notify user we're switching functionality off
		Toast.makeText(context, R.string.toastTextAfterDeletingWidget, Toast.LENGTH_LONG).show();
		
		//set SharedPreferences main application state to FALSE
		WidgetConfigurationHolder.getInstance().setEnabled(context, Boolean.FALSE);
		
		Log.d(LOG_TAG, "Stroring values:" + WidgetConfigurationHolder.getInstance().toString());
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.widget);
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		
		if (intent.getAction().equals(WidgetConfigure.WIDGET_CLICK)) {
			if (WidgetConfigurationHolder.getInstance().isEnabled()) {
				updateView.setImageViewResource(R.id.imagebutton, R.drawable.off);
			} else {
				updateView.setImageViewResource(R.id.imagebutton, R.drawable.on);
			}
			
			//get Appwidget manager and change widget image
			AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, updateView);
			
			// toggle state
			WidgetConfigurationHolder.getInstance().setEnabled(!WidgetConfigurationHolder.getInstance().isEnabled());
			
			//store settings
			SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(WidgetConfigurationHolder.ENABLED, WidgetConfigurationHolder.getInstance().isEnabled());
            editor.commit();
            
            Log.d(LOG_TAG, "Stroring values:" + WidgetConfigurationHolder.getInstance().toString());
		}
	}

}
