package com.asimq.artists.bandninja.widget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;

import com.asimq.artists.bandninja.utils.Util;

/**
 * Broadcast receiver that provides a delay in order to handle the widget action view
 *
 * @author Asim Qureshi
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver  {

	public static final String WAKE_LOG_TAG = "WAKE_LOG_TAG";

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOG_TAG);
		//Acquire the lock
		wl.acquire();
		BandAppWidgetProvider.sendRefreshBroadcast(context);

		//Release the lock
		wl.release();
	}

}
