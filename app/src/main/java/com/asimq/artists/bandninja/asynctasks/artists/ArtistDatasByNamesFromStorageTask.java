package com.asimq.artists.bandninja.asynctasks.artists;

import java.util.ArrayList;
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

	private final BandItemRepository bandItemRepository;
	private final List<ArtistData> artistDatas;
	private final Map<String, Artist> searchResultsByName;
	private final MediatorLiveData<Boolean> isRefreshingObservable;
	private final MediatorLiveData<List<Artist>> musicItemObservable;

	final String TAG = this.getClass().getSimpleName();

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
			Log.i(TAG, "artist from storage: " + artist);
			searchResultsByName.put(artist.getName(), artist);
			remainingArtistNames.remove(artist.getName());
			artists.add(artist);
		}
		if (remainingArtistNames.isEmpty()) {
			isRefreshingObservable.setValue(false);
			musicItemObservable.setValue(artists);
			return;
		}
		new ArtistsByNamesFromServerTask(bandItemRepository, artistDatas, searchResultsByName, isRefreshingObservable,
				musicItemObservable).executeOnExecutor(Executors.newSingleThreadExecutor(), remainingArtistNames);
	}
}
