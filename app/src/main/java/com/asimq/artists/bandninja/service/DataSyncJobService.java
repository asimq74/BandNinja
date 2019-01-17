package com.asimq.artists.bandninja.service;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetArtists;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataSyncJobService extends JobService {

	private static final String TAG = DataSyncJobService.class.getSimpleName();
	private ApplicationComponent applicationComponent;
	@Inject
	ArtistDataDao artistDataDao;
	boolean isWorking = false;
	boolean jobCancelled = false;
	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";

	public void downloadArtistInfoToStorage(@NonNull String artistName) {
		final GetArtists service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetArtists.class);
		Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
				API_KEY, DEFAULT_FORMAT);
		artistInfoCall.enqueue(new Callback<ArtistWrapper>() {
			@Override
			public void onFailure(Call<ArtistWrapper> call, Throwable t) {
				Log.e(TAG, "get artist info for " + artistName + " failed.");
			}

			@Override
			public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
				final ArtistWrapper artistWrapper = response.body();
				if (artistWrapper == null) {
					return ;
				}
				final ArtistData artistData = new ArtistData(artistWrapper.getArtist());
				artistDataDao.insertArtist(artistData);
				Log.d(TAG, String.format("updated artist info for %s %s", artistData.getName(), artistData.getMbid()));
			}
		});

	}

	class UpdateArtistTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			List<ArtistData> artistDatas = artistDataDao.fetchAllArtistDatas();
			for (ArtistData artistData : artistDatas) {
				downloadArtistInfoToStorage(artistData.getName());
			}
			return null;
		}
	}

	private void doWork(JobParameters jobParameters) {
		if (jobCancelled) return;
		new UpdateArtistTask().executeOnExecutor(Executors.newSingleThreadExecutor());
		List<ArtistData> artistDatasAfterDownload = artistDataDao.fetchAllArtistDatas();
		Log.d(TAG, "Job finished! " + artistDatasAfterDownload.size() + " artists counted after download");
		isWorking = false;
		boolean needsReschedule = true;
		jobFinished(jobParameters, needsReschedule);
	}

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
		new SyncDataTask().executeOnExecutor(Executors.newSingleThreadExecutor(), jobParameters);
	}

	class SyncDataTask extends AsyncTask<JobParameters, Void, Void> {

		@Override
		protected Void doInBackground(JobParameters... jobParameters) {
			doWork(jobParameters[0]);
			return null;
		}
	}
}