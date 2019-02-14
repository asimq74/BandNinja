package com.asimq.artists.bandninja.service;

import java.util.concurrent.Executors;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.asynctasks.main.RefreshBandDataSyncAsyncTask;

public class DataSyncJobService extends JobService {

	private static final String TAG = DataSyncJobService.class.getSimpleName();
	boolean isWorking = false;
	boolean jobCancelled = false;

	// Called by the Android system when it's time to run the job
	@Override
	public boolean onStartJob(JobParameters jobParameters) {
		Log.d(TAG, "Job started!");
		final MyApplication application = (MyApplication) getApplicationContext();
		isWorking = true;
		// We need 'jobParameters' so we can call 'jobFinished'
		new RefreshBandDataSyncAsyncTask(application.getApplicationComponent()).executeOnExecutor(Executors.newSingleThreadExecutor());
		return isWorking;
	}

	// Called if the job was cancelled before being finished
	@Override
	public boolean onStopJob(JobParameters jobParameters) {
		Log.d(TAG, "Job cancelled before being completed.");
		jobCancelled = true;
		boolean needsReschedule = isWorking;
		jobFinished(jobParameters, needsReschedule);
		return needsReschedule;
	}

}