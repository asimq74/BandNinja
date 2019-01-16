package com.asimq.artists.bandninja.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class ServiceUtil {

	static final String TAG = ServiceUtil.class.getSimpleName();

	// schedule the start of the service every 10 - 30 seconds
	public static void scheduleJob(Context context) {
		ComponentName serviceComponent = new ComponentName(context, DataSyncJobService.class);
		JobInfo.Builder builder = new JobInfo.Builder(12, serviceComponent);
		builder.setMinimumLatency(1 * 1000); // wait at least
		builder.setOverrideDeadline(3 * 1000); // maximum delay
		//builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
		//builder.setRequiresDeviceIdle(true); // device should be idle
		//builder.setRequiresCharging(false); // we don't care if the device is charging or not
		JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
		int resultCode = jobScheduler.schedule(builder.build());
		if (resultCode == JobScheduler.RESULT_SUCCESS) {
			Log.d(TAG, "Job scheduled!");
		} else {
			Log.d(TAG, "Job not scheduled");
		}
	}
}
