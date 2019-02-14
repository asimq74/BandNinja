package com.asimq.artists.bandninja.repositories;

import java.util.List;
import java.util.Map;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.asimq.artists.bandninja.json.Artist;

public interface SearchResultsRepository {

	LiveData<List<Artist>> getTopArtists();

	void searchForArtistByTag(@NonNull String tagName, @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<Map<String, Artist>> searchResultsByArtistObservable);

	void searchForTopArtists(@NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<Map<String, Artist>> searchResultsByArtistObservable);

	void searchResultsByArtistName(@NonNull String query, @NonNull MediatorLiveData<Boolean> isRefreshingObservable,
			@NonNull MediatorLiveData<Map<String, Artist>> searchResultsByArtistObservable);
}
