package com.asimq.artists.bandninja.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.asimq.artists.bandninja.MainActivity;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.R;
import com.asimq.artists.bandninja.utils.Util;

/**
 * Implementation of App Widget functionality.
 */
public class BandAppWidgetProvider extends AppWidgetProvider {

	static final String TAG = BandAppWidgetProvider.class.getSimpleName();

	public static void handleActionViewArtists(@NonNull final Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BandAppWidgetProvider.class));
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widgetListView);
		appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.titleTextView);
		updateAppWidgets(context, appWidgetManager, appWidgetIds);
	}

	public static void sendRefreshBroadcast(Context context) {
		Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		intent.setComponent(new ComponentName(context, BandAppWidgetProvider.class));
		context.sendBroadcast(intent);
	}

	static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			RemoteViews views = new RemoteViews(
					context.getPackageName(), R.layout.bandninja_app_widget
			);


			views.setTextViewText(R.id.titleTextView, context.getString(R.string.appwidget_text));

			Intent intent = new Intent(context, BandAppWidgetRemoteViewsService.class);
			views.setRemoteAdapter(R.id.widgetListView, intent);

			Intent clickIntentTemplate = new Intent(context, MainActivity.class);
			PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
					.addNextIntentWithParentStack(clickIntentTemplate)
					.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			views.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}

	@Override
	public void onDisabled(Context context) {
		Toast.makeText(context, "onDisabled():last widget instance removed", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Toast.makeText(context, "onEnabled():attempting to get widget", Toast.LENGTH_SHORT).show();
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		//After after 3 seconds
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				10000, pi);
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		final String action = intent.getAction();
		if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
			handleActionViewArtists(context.getApplicationContext());
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		updateAppWidgets(context, appWidgetManager, appWidgetIds);
	}
}

