package com.asimq.artists.bandninja.asynctasks.artists;

import java.util.List;

import javax.inject.Inject;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.dagger.ApplicationComponent;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.remote.retrofit.BackgroundRetrofitClientInstance;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAllArtistsTask extends AsyncTask<Void, Void, List<ArtistData>> {

	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	private final String TAG = this.getClass().getSimpleName();
	@Inject
	BandItemRepository bandItemRepository;

	public UpdateAllArtistsTask(ApplicationComponent applicationComponent) {
		applicationComponent.inject(this);
	}

	@Override
	protected List<ArtistData> doInBackground(Void... params) {
		List<ArtistData> artistDatas = bandItemRepository.getAllArtistDatas();
		for (ArtistData artistData : artistDatas) {
			downloadArtistInfoToStorage(artistData.getName());
		}
		return artistDatas;
	}

	private void downloadArtistInfoToStorage(@NonNull String artistName) {
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
					Log.e(TAG, "get artist info for " + artistName + " failed. no data was returned");
					return;
				}
				final ArtistData artistData = new ArtistData(artistWrapper.getArtist());
				bandItemRepository.saveArtist(artistData);
				Log.d(TAG, String.format("updated artist info for %s %s", artistData.getName(), artistData.getMbid()));
			}
		});

	}

	@Override
	protected void onPostExecute(List<ArtistData> artistData) {
		super.onPostExecute(artistData);
		Log.d(TAG, "update artist task finished! " + artistData.size() + " artists counted after download");
	}

}
