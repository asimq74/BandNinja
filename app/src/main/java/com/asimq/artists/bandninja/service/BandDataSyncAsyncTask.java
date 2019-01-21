package com.asimq.artists.bandninja.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import android.app.job.JobParameters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.MyApplication;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.room.ArtistData;
import com.asimq.artists.bandninja.room.dao.ArtistDataDao;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BandDataSyncAsyncTask extends AsyncTask<Void, Void, Void> {

	class UpdateArtistTask extends AsyncTask<Void, Void, List<ArtistData>> {

		@Override
		protected List<ArtistData> doInBackground(Void... params) {
			List<ArtistData> artistDatas = new ArrayList<>();
			artistDatas = artistDataDao.fetchAllArtistDatas();
			for (ArtistData artistData : artistDatas) {
				downloadArtistInfoToStorage(artistData.getName());
			}
			return artistDatas;
		}

		@Override
		protected void onPostExecute(List<ArtistData> artistData) {
			super.onPostExecute(artistData);
			Log.d(TAG, "update artist task finished! " + artistData.size() + " artists counted after download");
		}
	}
	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	private static final String TAG = BandDataSyncAsyncTask.class.getSimpleName();
	private ApplicationComponent applicationComponent;
	@Inject
	ArtistDataDao artistDataDao;

	public BandDataSyncAsyncTask(Context context) {
		final MyApplication application = (MyApplication) context;
		applicationComponent = application.getApplicationComponent();
		applicationComponent.inject(this);
	}

	@Override
	protected Void doInBackground(Void... voids) {
		doWork();
		return null;
	}

	private void doWork() {
		new UpdateArtistTask().executeOnExecutor(Executors.newSingleThreadExecutor());
	}

	public void downloadArtistInfoToStorage(@NonNull String artistName) {
		final GetMusicInfo service = BackgroundRetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
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
					return;
				}
				final ArtistData artistData = new ArtistData(artistWrapper.getArtist());
				artistDataDao.insertArtist(artistData);
				Log.d(TAG, String.format("updated artist info for %s %s", artistData.getName(), artistData.getMbid()));
			}
		});

	}
}