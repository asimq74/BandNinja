package com.asimq.artists.bandninja.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Broadcast receiver that provides a delay in order to handle the widget action view
 *
 * @author Asim Qureshi
 */
public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

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
