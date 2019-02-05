package com.asimq.artists.bandninja.asynctasks.artists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import android.arch.lifecycle.MediatorLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.asimq.artists.bandninja.json.Artist;
import com.asimq.artists.bandninja.repositories.BandItemRepository;
import com.asimq.artists.bandninja.room.ArtistData;

public class ArtistDatasByNamesFromStorageTask extends AsyncTask<Set<String>, Void, List<ArtistData>> {

	final String TAG = this.getClass().getSimpleName();
	private final List<ArtistData> artistDatas;
	private final BandItemRepository bandItemRepository;
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<Artist>> musicItemObservable;
	private final Map<String, Artist> searchResultsByName;

	public ArtistDatasByNamesFromStorageTask(@NonNull BandItemRepository bandItemRepository,
			@NonNull Map<String, Artist> searchResultsByName, @NonNull List<ArtistData> artistDatas,
			@NonNull MediatorLiveData<Boolean> isRefreshingObservable, @NonNull MediatorLiveData<List<Artist>> musicItemObservable) {
		this.bandItemRepository = bandItemRepository;
		this.artistDatas = artistDatas;
		this.searchResultsByName = searchResultsByName;
		this.isRefreshingObservable = isRefreshingObservable;
		this.musicItemObservable = musicItemObservable;
	}

	@Override
	protected List<ArtistData> doInBackground(Set<String>... params) {
		Set<String> names = params[0];
		return bandItemRepository.getArtistDatasByNames(names);
	}

	@Override
	protected void onPostExecute(List<ArtistData> artistDatas) {
		super.onPostExecute(artistDatas);
		Set<String> remainingArtistNames = new LinkedHashSet<>();
		remainingArtistNames.addAll(searchResultsByName.keySet());
		this.artistDatas.clear();
		this.artistDatas.addAll(artistDatas);
		List<Artist> artists = new ArrayList<>();
		for (ArtistData artistData : this.artistDatas) {
			Artist artist = new Artist(artistData);
			searchResultsByName.put(artist.getName(), artist);
			Log.d(TAG, String.format("adding artist %s from database", artist.getName()));
			remainingArtistNames.remove(artist.getName());
			artists.add(artist);
		}
		if (remainingArtistNames.isEmpty()) {
			Log.d(TAG, "no remaining matching artists in the database");
			Collections.sort(artists);
			isRefreshingObservable.setValue(false);
			musicItemObservable.setValue(artists);
			return;
		}
		StringBuilder sb = new StringBuilder("[ ");
		for (String remainingArtistName : remainingArtistNames) {
			sb.append(remainingArtistName).append(" ");
		}
		sb.append("]");
		Log.d(TAG, "attempting to download the following artists from server: " + sb.toString());
		new ArtistsByNamesFromServerTask(bandItemRepository, artistDatas, searchResultsByName, isRefreshingObservable,
				musicItemObservable).executeOnExecutor(Executors.newSingleThreadExecutor(), remainingArtistNames);
	}
}
