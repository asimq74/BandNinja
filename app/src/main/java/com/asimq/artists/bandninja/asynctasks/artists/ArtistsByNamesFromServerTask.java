package com.asimq.artists.bandninja.asynctasks.artists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.asynctasks.SaveArtistDataTask;
import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.json.ArtistWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;
import com.google.gson.internal.LinkedHashTreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistsByNamesFromServerTask extends AsyncTask<Set<String>, Void, Void> {

	private static Map<String, Boolean> mapOfAttachmentTasks = new LinkedHashTreeMap<>();

	public static synchronized void addTask(String taskQueryString) {
		mapOfAttachmentTasks.put(taskQueryString, true);
	}

	public static synchronized boolean isTasksEmpty() {
		return mapOfAttachmentTasks.isEmpty();
	}

	public static synchronized void removeTask(String taskQueryString) {
		mapOfAttachmentTasks.remove(taskQueryString);
	}

	private final BandItemRepository bandItemRepository;
	private final List<ArtistData> artistDatas;
	private final Map<String, Artist> searchResultsByName;
	public static final String API_KEY = BuildConfig.LastFMApiKey;
	public static final String DEFAULT_FORMAT = "json";
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<Artist>> musicItemObservable;

	public ArtistsByNamesFromServerTask(BandItemRepository bandItemRepository, List<ArtistData> artistDatas, Map<String, Artist> searchResultsByName,
			@NonNull MediatorLiveData<Boolean> isRefreshingObservable, @NonNull MediatorLiveData<List<Artist>> musicItemObservable) {
		this.bandItemRepository = bandItemRepository;
		this.artistDatas = artistDatas;
		this.searchResultsByName = searchResultsByName;
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}

	final String TAG = this.getClass().getSimpleName();

	@Override
	protected Void doInBackground(Set<String>... params) {
		final GetMusicInfo service
				= RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		Set<String> names = params[0];
		for (String artistName : names) {
			addTask(artistName);
			Call<ArtistWrapper> artistInfoCall = service.getArtistInfo("artist.getinfo", artistName,
					API_KEY, DEFAULT_FORMAT);
			artistInfoCall.enqueue(new Callback<ArtistWrapper>() {
				@Override
				public void onFailure(Call<ArtistWrapper> call, Throwable t) {
					removeTask(artistName);
					if (isTasksEmpty()) {
						Log.d(TAG, "final results map = " + searchResultsByName);
						ArrayList artists = new ArrayList(searchResultsByName.values());
						Collections.sort(artists);
						musicItemObservable.setValue(artists);
						isRefreshingObservable.setValue(false);
					}
				}

				@Override
				public void onResponse(Call<ArtistWrapper> call, Response<ArtistWrapper> response) {
					removeTask(artistName);
					final ArtistWrapper artistWrapper = response.body();
					if (artistWrapper == null) {
						return;
					}
					final Artist artist = artistWrapper.getArtist();
					final ArtistData artistData = new ArtistData(artist);
					searchResultsByName.put(artistData.getName(), artist);
					artistDatas.add(artistData);
					new SaveArtistDataTask(bandItemRepository).execute(artistData);
				}
			});
		}
		return null;
	}
}
