package com.asimq.artists.bandninja.service;

import java.util.List;

import javax.inject.Inject;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;

public class DataSyncJobService extends JobService {

	private static final String TAG = DataSyncJobService.class.getSimpleName();
	boolean isWorking = false;
	boolean jobCancelled = false;

	private void doWork(JobParameters jobParameters) {
		// 10 seconds of working (1000*10ms)
		for (int i = 0; i < 1000; i++) {
			// If the job has been cancelled, stop working; the job will be rescheduled.
			if (jobCancelled)
				return;

			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
		LiveData<List<ArtistData>> mLiveArtistDatas =  bandItemRepository.getAllArtistData();
		Log.d(TAG, "Job finished! " + mLiveArtistDatas.getValue());
		isWorking = false;
		boolean needsReschedule = true;
		jobFinished(jobParameters, needsReschedule);
	}

	private ApplicationComponent applicationComponent;

	@Inject
	BandItemRepository bandItemRepository;

	// Called by the Android system when it's time to run the job
	@Override
	public boolean onStartJob(JobParameters jobParameters) {
		Log.d(TAG, "Job started!");
		final MyApplication application = (MyApplication) getApplicationContext();
		applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
		isWorking = true;
		// We need 'jobParameters' so we can call 'jobFinished'
		startWorkOnNewThread(jobParameters); // Services do NOT run on a separate thread

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

	private void startWorkOnNewThread(final JobParameters jobParameters) {
		new Thread(new Runnable() {
			public void run() {
				doWork(jobParameters);
			}
		}).start();
	}
}