package com.asimq.artists.bandninja.asynctasks.albums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.json.AlbumInfo;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.AlbumData;

public class AlbumDatasByAlbumNameFromStorageTask extends AsyncTask<String, Void, List<AlbumData>> {

	private final String TAG = this.getClass().getSimpleName();
	private final List<AlbumData> albumDatas;
	private String artist = "";
	private final BandItemRepository bandItemRepository;
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<AlbumInfo>> musicItemObservable;

	public AlbumDatasByAlbumNameFromStorageTask(@NonNull BandItemRepository bandItemRepository, @NonNull List<AlbumData> albumDatas,
			@NonNull MediatorLiveData<Boolean> isRefreshingObservable, @NonNull MediatorLiveData<List<AlbumInfo>> musicItemObservable) {
		this.bandItemRepository = bandItemRepository;
		this.albumDatas = albumDatas;
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}

	@Override
	protected List<AlbumData> doInBackground(String... params) {
		artist = params[0];
		return bandItemRepository.getAlbumsWithTracks(artist);
	}

	@Override
	protected void onPostExecute(List<AlbumData> albumDatas) {
		super.onPostExecute(albumDatas);
		this.albumDatas.clear();
		this.albumDatas.addAll(albumDatas);
		List<AlbumInfo> albums = new ArrayList<>();
		for (AlbumData albumData : this.albumDatas) {
			AlbumInfo album = new AlbumInfo(albumData);
			Log.d(TAG, String.format("adding album %s from database", album.getName()));
			albums.add(album);
		}
		if (!albums.isEmpty()) {
			Collections.sort(albums);
			isRefreshingObservable.setValue(false);
			musicItemObservable.setValue(albums);
			StringBuilder sb = new StringBuilder("[ ");
			for (AlbumInfo album : albums) {
				sb.append(album.getName()).append(" ");
			}
			sb.append("]");
			Log.d(TAG, "downloaded the following albums from local database: " + sb.toString());
			return;
		}
		Log.d(TAG, String.format("attempting to download albums from server for artist %s ", artist));
		new AlbumsByAlbumNamesFromServerTask(bandItemRepository, isRefreshingObservable, musicItemObservable)
				.executeOnExecutor(Executors.newSingleThreadExecutor(), artist);
	}
}
