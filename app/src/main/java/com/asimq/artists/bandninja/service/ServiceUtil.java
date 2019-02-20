package com.asimq.artists.bandninja.service;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class ServiceUtil {

	static final String TAG = ServiceUtil.class.getSimpleName();
	private static final long ONE_DAY_INTERVAL = 24 * 60 * 60 * 1000L; // 1 Day
	// schedule the start of the service every 10 - 30 seconds
	public static void scheduleJob(Context context) {
		ComponentName serviceComponent = new ComponentName(context, DataSyncJobService.class);
		JobInfo.Builder builder = new JobInfo.Builder(12, serviceComponent);
		builder.setPeriodic(ONE_DAY_INTERVAL);
		builder.setRequiresCharging(false); // we don't care if the device is charging or not
		JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
		int resultCode = jobScheduler.schedule(builder.build());
		if (resultCode == JobScheduler.RESULT_SUCCESS) {
			Log.d(TAG, "Job scheduled!");
		} else {
			Log.d(TAG, "Job not scheduled");
		}
	}
}
