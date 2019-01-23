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
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
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

	static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {

		// Construct the RemoteViews object
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.bandninja_app_widget);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final String title = prefs.getString(Util.PREFS_WIDGET_TITLE, "");
		remoteViews.setTextViewText(R.id.titleTextView, title);

		final Intent widgetClickIntent = new Intent(context, MainActivity.class);
//        mainActivityIntent.putExtra(RECIPE_ID, recipe.getId());
//        mainActivityIntent.putExtra(SHOW_INGREDIENTS, true);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, widgetClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		//Widgets allow click handlers to only launch pending intents
		remoteViews.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

		Intent artistsIntent = new Intent(context, BandAppWidgetRemoteViewsService.class);
		remoteViews.setRemoteAdapter(R.id.widgetListView, artistsIntent);

		// template to handle the click listener for each item
		PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
				.addNextIntentWithParentStack(widgetClickIntent)
				.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		remoteViews.setPendingIntentTemplate(R.id.widgetListView, clickPendingIntentTemplate);

		appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
	}

	static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}

	@Override
	public void onDisabled(Context context) {
		super.onEnabled(context);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		//After after 3 seconds
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				10000, pi);
	}

	@Override
	public void onEnabled(Context context) {
		Toast.makeText(context, "onDisabled():last widget instance removed", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
		super.onDisabled(context);
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		final String action = intent.getAction();
		if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
			handleActionViewArtists(context);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final MyApplication application = (MyApplication) context.getApplicationContext();
		(application).getApplicationComponent().inject(BandAppWidgetProvider.this);

		// There may be multiple widgets active, so update all of them
		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
	}
}

