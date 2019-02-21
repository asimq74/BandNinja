package com.asimq.artists.bandninja.asynctasks.albums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.BuildConfig;
import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.json.AlbumInfoWrapper;
import com.asimq.artists.bandninja.remote.retrofit.GetMusicInfo;
import com.asimq.artists.bandninja.remote.retrofit.RetrofitClientInstance;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;
import com.google.gson.internal.LinkedHashTreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumInfosByNamesFromServerTask extends AsyncTask<List<AlbumInfo>, Void, Void> {

	private static final String API_KEY = BuildConfig.LastFMApiKey;
	private static final String DEFAULT_FORMAT = "json";
	private static Map<String, Boolean> mapOfAttachmentTasks = new LinkedHashTreeMap<>();

	private static synchronized void addTask(String taskQueryString) {
		mapOfAttachmentTasks.put(taskQueryString, true);
	}

	private static synchronized boolean isTasksEmpty() {
		return mapOfAttachmentTasks.isEmpty();
	}

	private static synchronized void removeTask(String taskQueryString) {
		mapOfAttachmentTasks.remove(taskQueryString);
	}

	final String TAG = this.getClass().getSimpleName();
	private List<AlbumInfo> albumInfos = new ArrayList<>();
	private final BandItemRepository bandItemRepository;
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<AlbumInfo>> musicItemObservable;

	public AlbumInfosByNamesFromServerTask(BandItemRepository bandItemRepository,
			@NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<List<AlbumInfo>> musicItemObservable) {
		this.bandItemRepository = bandItemRepository;
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}

	protected void considerUpdatingLiveData() {
		if (isTasksEmpty()) {
			Collections.sort(albumInfos);
			Log.d(TAG, "final albumInfos results = " + albumInfos);
			new FoundAlbumsProcessor(isRefreshingObservable, musicItemObservable).executeOnExecutor(
					Executors.newSingleThreadExecutor(), albumInfos);
		}
	}

	@Override
	protected Void doInBackground(List<AlbumInfo>... params) {
		final GetMusicInfo service
				= RetrofitClientInstance.getRetrofitInstance().create(GetMusicInfo.class);
		List<AlbumInfo> inputAlbumInfos = params[0];
		for (AlbumInfo albumInfo : inputAlbumInfos) {
			final String albumName = albumInfo.getName();
			final String aristName = albumInfo.getArtist();
			addTask(albumName);
			Call<AlbumInfoWrapper> albumInfoCall = service.getAlbumInfo("album.getinfo", aristName, albumName, API_KEY, DEFAULT_FORMAT);
			albumInfoCall.enqueue(new Callback<AlbumInfoWrapper>() {
				@Override
				public void onFailure(Call<AlbumInfoWrapper> call, Throwable t) {
					removeTask(albumName);
					Log.d(TAG, String.format("failed to add albumInfo %s from server", albumName));
					considerUpdatingLiveData();
				}

				@Override
				public void onResponse(Call<AlbumInfoWrapper> call, Response<AlbumInfoWrapper> response) {
					removeTask(albumName);
					final AlbumInfoWrapper albumInfoWrapper = response.body();
					if (albumInfoWrapper == null) {
						Log.d(TAG, String.format("failed to add albumInfo %s from server", albumName));
						considerUpdatingLiveData();
						return;
					}
					final AlbumInfo albumInfo = albumInfoWrapper.getAlbumInfo();
					final AlbumData albumData = new AlbumData(albumInfo);
					Log.d(TAG, String.format("adding albumInfo %s from server", albumInfo.getName()));
					albumInfos.add(albumInfo);
					Log.d(TAG, String.format("saving album data for %s to database", albumData.getName()));
					new SaveAlbumDataTask(bandItemRepository).executeOnExecutor(Executors.newSingleThreadExecutor(),
							albumData);
					considerUpdatingLiveData();
				}
			});
		}
		return null;
	}
}
