package com.asimq.artists.bandninja.jobs;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;

import android.util.Log;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.asynctasks.main.RefreshBandDataSyncAsyncTask;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class BandSyncJobService extends JobService {

	private static final String TAG = BandSyncJobService.class.getSimpleName();

	@Override
	public boolean onStartJob(final JobParameters job) {
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
		Log.d(TAG, "Started job at " + currentDateTimeString);
		final MyApplication application = (MyApplication) getApplicationContext();
		new RefreshBandDataSyncAsyncTask(application.getApplicationComponent()).executeOnExecutor(Executors.newSingleThreadExecutor());
		jobFinished(job, true);
		return false;
	}

	@Override
	public boolean onStopJob(JobParameters job) {
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
		Log.d(TAG, String.format("Stopped job %s at %s", job, currentDateTimeString));
		return false;
	}
}
