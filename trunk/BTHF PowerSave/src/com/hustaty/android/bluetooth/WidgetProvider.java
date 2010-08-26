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
	public static final String LOG_TAG = WidgetProvider.class.getSimpleName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d(LOG_TAG, "#onUpdate() started");
		
		RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.widget);
		appWidgetManager.updateAppWidget(appWidgetIds, updateView);

		Intent clickIntent = new Intent(WidgetConfigure.WIDGET_CLICK);
		
		WidgetConfigurationHolder.loadPreferences(
				context.getSharedPreferences(WidgetConfigure.PREFS_NAME,
						Activity.MODE_PRIVATE));

		if (WidgetConfigurationHolder.getInstance(context).isEnabled()) {
			updateView.setImageViewResource(R.id.imagebutton, R.drawable.on);
			if(!NotificationService.isRunning(context)) {
				NotificationService.start(context);
			}
		} else {
			updateView.setImageViewResource(R.id.imagebutton, R.drawable.off);
			if(NotificationService.isRunning(context)) {
				NotificationService.stop(context);
			}
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
		WidgetConfigurationHolder.getInstance(context).setEnabled(context, Boolean.FALSE);

		if(NotificationService.isRunning(context)) {
			NotificationService.stop(context);
		}

		Log.d(LOG_TAG, "Stroring values:" + WidgetConfigurationHolder.getInstance(context).toString());
		
        Intent configSavedIntent = new Intent(WidgetConfigure.CONFIG_SAVED);
        configSavedIntent.putExtra(WidgetConfigure.PERFORM_SHAREDPREFERECES_EDIT, false);
        context.sendBroadcast(configSavedIntent);
		
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		if(NotificationService.isRunning(context)) {
			NotificationService.stop(context);
		}
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		if(!NotificationService.isRunning(context)) {
			NotificationService.start(context);
		}
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		RemoteViews updateView = new RemoteViews(context.getPackageName(), R.layout.widget);
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		
		if (intent.getAction().equals(WidgetConfigure.WIDGET_CLICK)
				|| intent.getAction().equals(WidgetConfigure.CONFIG_SAVED)) {

			//load SharedPreferences on receiving specific actions
			SharedPreferences settings = context.getSharedPreferences(WidgetConfigure.PREFS_NAME, Activity.MODE_PRIVATE);
			WidgetConfigurationHolder.loadPreferences(settings);

			if(!intent.hasExtra(WidgetConfigure.PERFORM_SHAREDPREFERECES_EDIT)) {
				// toggle state and store settings
				WidgetConfigurationHolder.getInstance(context).setEnabled(context, !WidgetConfigurationHolder.getInstance(context).isEnabled());
			} else if(intent.getBooleanExtra(WidgetConfigure.PERFORM_SHAREDPREFERECES_EDIT, false)) {
				// toggle state and store settings
				WidgetConfigurationHolder.getInstance(context).setEnabled(context, !WidgetConfigurationHolder.getInstance(context).isEnabled());
			}
				
			//toggle widget according to current state
			if (WidgetConfigurationHolder.getInstance(context).isEnabled()) {
				updateView.setImageViewResource(R.id.imagebutton, R.drawable.on);
			} else {
				updateView.setImageViewResource(R.id.imagebutton, R.drawable.off);
			}
			
			//get Appwidget manager and change widget image
			AppWidgetManager.getInstance(context).updateAppWidget(thisWidget, updateView);
			
			Log.d(LOG_TAG, "Storing values:" + WidgetConfigurationHolder.getInstance(context).toString());
		}
	}

}
