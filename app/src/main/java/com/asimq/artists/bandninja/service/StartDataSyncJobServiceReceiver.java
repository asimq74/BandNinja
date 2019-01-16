package com.asimq.artists.bandninja.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartDataSyncJobServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ServiceUtil.scheduleJob(context);
	}
}
